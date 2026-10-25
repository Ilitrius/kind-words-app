package app.kindwords.data

import android.content.Context
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    suspend fun random(language: String, category: String?, excludeId: String?): MessageEntity? =
        dao.random(language, category, excludeId ?: "")

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
