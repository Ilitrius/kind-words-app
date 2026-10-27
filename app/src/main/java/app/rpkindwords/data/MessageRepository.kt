package app.rpkindwords.data

import android.content.Context
import java.util.UUID
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Liefert den Rohtext einer Inhaltsdatei oder null, wenn sie fehlt. */
fun interface ContentSource {
    fun read(language: String): String?
}

class AssetContentSource(private val context: Context) : ContentSource {
    override fun read(language: String): String? = try {
        context.assets.open("messages/messages_$language.json").bufferedReader().use { it.readText() }
    } catch (_: Exception) {
        null
    }
}

/** Anteil der Vorschläge, die bevorzugt aus den Favoriten kommen. */
object FavoriteBias {
    const val SHARE = 0.4
    fun preferFavorite(roll: Double): Boolean = roll < SHARE
}

class MessageRepository(
    private val dao: MessageDao,
    private val settings: SettingsRepository,
    private val source: ContentSource,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private val seedMutex = Mutex()

    /** Idempotent: gleicht alle unterstützten Sprachen mit den gebündelten Dateien ab. Fehlende Dateien werden toleriert. */
    suspend fun ensureSeeded() = seedMutex.withLock {
        withContext(Dispatchers.IO) {
            for (lang in Languages.supported) {
                val file = source.read(lang)?.let { ContentParser.parse(it) } ?: continue
                if (file.language != lang) continue
                val installed = settings.contentVersion(lang)
                if (!ContentSync.needsSync(installed, file)) continue
                val plan = ContentSync.plan(dao.bundledForLanguage(lang), dao.userIds().toSet(), file, clock())
                if (plan.deleteIds.isNotEmpty()) dao.deleteByIds(plan.deleteIds)
                if (plan.updates.isNotEmpty()) dao.updateAll(plan.updates)
                if (plan.inserts.isNotEmpty()) dao.insertAll(plan.inserts)
                settings.setContentVersion(lang, file.version)
            }
        }
    }

    /**
     * Zufälliger Vorschlag. Favoriten werden bevorzugt: in [FavoriteBias.SHARE] der Fälle kommt – falls vorhanden –
     * ein Favorit (passend zu Sprache/Kategorie), sonst ein beliebiger Satz.
     */
    suspend fun random(
        language: String,
        category: String?,
        excludeId: String?,
        roll: Double = Random.nextDouble(),
        rng: Random = Random.Default,
    ): MessageEntity? {
        val exclude = excludeId ?: ""
        val audience = Audience.filter(settings.audience.first())
        if (FavoriteBias.preferFavorite(roll)) dao.randomFavorite(language, category, exclude, audience)?.let { return it }
        // Sonst: passend zum Stil der eigenen Sätze gewichtet (ohne eigene Sätze: gleichverteilt)
        val candidates = dao.candidates(language, category, exclude, audience)
        return StyleMatcher.pick(candidates, styleProfile(), rng) { it.text }
    }

    /** Stilprofil aus allen eigenen Sätzen (lokal berechnet). */
    suspend fun styleProfile(): StyleProfile = StyleProfile.from(dao.userTexts())

    /** Passt einen vorgeschlagenen Satz dem Satzende-Stil der eigenen Sätze an; eigene Sätze bleiben unverändert. */
    fun styled(message: MessageEntity, profile: StyleProfile): MessageEntity =
        if (message.source == Source.USER) message else message.copy(text = StyleAdapter.adapt(message.text, profile))

    fun observe(id: String): Flow<MessageEntity?> = dao.observe(id)
    fun favorites(): Flow<List<MessageEntity>> = dao.observeFavorites()
    fun userMessages(): Flow<List<MessageEntity>> = dao.observeUserMessages()

    suspend fun setFavorite(id: String, favorite: Boolean) = dao.setFavorite(id, favorite)

    suspend fun addUser(language: String, text: String) {
        val t = text.trim()
        if (t.isEmpty()) return
        dao.insertAll(listOf(MessageEntity("user-${UUID.randomUUID()}", language, CATEGORY_CUSTOM, t, Source.USER, false, false, clock())))
    }

    suspend fun editUser(id: String, text: String) {
        val t = text.trim()
        val old = dao.get(id) ?: return
        if (old.source != Source.USER || t.isEmpty()) return
        dao.updateAll(listOf(old.copy(text = t, updatedAt = clock())))
    }

    suspend fun deleteUser(id: String) = dao.deleteUser(id)

    companion object {
        const val CATEGORY_CUSTOM = "custom"
        val categories = listOf(
            "love", "appreciation", "morning", "goodnight", "encouragement",
            "missyou", "compliment", "humor", "thanks", "apology",
        )
    }
}
