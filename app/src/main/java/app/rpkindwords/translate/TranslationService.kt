package app.rpkindwords.translate

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import app.rpkindwords.data.SettingsRepository
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.common.sdkinternal.MlKitContext
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Übersetzt Sätze auf dem Gerät mit Google ML Kit (On-Device Translation).
 *
 * Datenschutz: Das ML-Kit-SDK startet NICHT von selbst (der Init-Provider ist im Manifest entfernt). Es wird erst nach
 * ausdrücklicher Einwilligung initialisiert ([SettingsRepository.hasTranslationConsent]). Übersetzt wird lokal; Sätze
 * werden nicht gesendet. Sprachmodelle (ca. 30 MB je Sprache) werden nur auf ausdrückliche Aktion geladen.
 */
class TranslationService(private val context: Context, private val settings: SettingsRepository) {
    enum class Reason { NO_CONSENT, UNSUPPORTED, MODEL_MISSING, DOWNLOAD_FAILED, FAILED, NO_NETWORK, WIFI_REQUIRED, NO_SPACE }

    sealed interface Result {
        data class Ok(val text: String) : Result
        data class Fail(val reason: Reason) : Result
    }

    private var initialized = false
    private val translators = HashMap<String, Translator>()

    private fun init() {
        if (!initialized) {
            MlKitContext.initializeIfNeeded(context.applicationContext)
            initialized = true
        }
    }

    private fun options(from: String, to: String): TranslatorOptions? {
        val f = TranslateLanguage.fromLanguageTag(from) ?: return null
        val t = TranslateLanguage.fromLanguageTag(to) ?: return null
        return TranslatorOptions.Builder().setSourceLanguage(f).setTargetLanguage(t).build()
    }

    private fun translator(from: String, to: String): Translator? {
        val key = "$from>$to"
        translators[key]?.let { return it }
        val o = options(from, to) ?: return null
        return Translation.getClient(o).also { translators[key] = it }
    }

    private fun model(lang: String): TranslateRemoteModel? =
        TranslateLanguage.fromLanguageTag(lang)?.let { TranslateRemoteModel.Builder(it).build() }

    /** Sind die Modelle für beide Sprachen schon auf dem Gerät? Ohne Einwilligung immer false (SDK bleibt aus). */
    suspend fun modelsReady(from: String, to: String): Boolean {
        if (!settings.hasTranslationConsent()) return false
        init()
        val manager = RemoteModelManager.getInstance()
        val a = model(from) ?: return false
        val b = model(to) ?: return false
        return try {
            manager.isModelDownloaded(a).await() && manager.isModelDownloaded(b).await()
        } catch (_: Exception) {
            false
        }
    }

    /** Lädt die Modelle (nur nach ausdrücklicher Aktion). Null = Erfolg. */
    suspend fun download(from: String, to: String, allowMobile: Boolean): Reason? {
        if (!settings.hasTranslationConsent()) return Reason.NO_CONSENT
        init()
        val t = translator(from, to) ?: return Reason.UNSUPPORTED
        networkProblem(allowMobile)?.let { return it }
        val conditions = DownloadConditions.Builder().apply { if (!allowMobile) requireWifi() }.build()
        return try {
            t.downloadModelIfNeeded(conditions).await()
            null
        } catch (_: Exception) {
            if (context.filesDir.usableSpace < MIN_FREE_BYTES) Reason.NO_SPACE else Reason.DOWNLOAD_FAILED
        }
    }

    /** Vorab-Prüfung, damit der Nutzer erfährt, woran es liegt (kein Netz, nur Mobilfunk bei WLAN-Pflicht). */
    private fun networkProblem(allowMobile: Boolean): Reason? {
        val cm = context.getSystemService(ConnectivityManager::class.java) ?: return null
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return Reason.NO_NETWORK
        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) return Reason.NO_NETWORK
        if (!allowMobile && !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)) return Reason.WIFI_REQUIRED
        return null
    }

    /** Übersetzt lokal. Lädt nie selbst Modelle nach. */
    suspend fun translate(text: String, from: String, to: String): Result {
        if (!settings.hasTranslationConsent()) return Result.Fail(Reason.NO_CONSENT)
        if (options(from, to) == null) return Result.Fail(Reason.UNSUPPORTED)
        if (!modelsReady(from, to)) return Result.Fail(Reason.MODEL_MISSING)
        val t = translator(from, to) ?: return Result.Fail(Reason.UNSUPPORTED)
        return try {
            Result.Ok(t.translate(text).await())
        } catch (_: Exception) {
            Result.Fail(Reason.FAILED)
        }
    }

    /** Anzahl geladener Sprachmodelle (für die Anzeige und das Löschen). */
    suspend fun downloadedCount(): Int {
        if (!settings.hasTranslationConsent()) return 0
        init()
        return try {
            RemoteModelManager.getInstance().getDownloadedModels(TranslateRemoteModel::class.java).await().size
        } catch (_: Exception) {
            0
        }
    }

    /** Löscht alle geladenen Modelle und gibt die Übersetzer frei. Funktioniert auch nach widerrufener Einwilligung. */
    suspend fun deleteAllModels() {
        close()
        init()
        try {
            val manager = RemoteModelManager.getInstance()
            for (m in manager.getDownloadedModels(TranslateRemoteModel::class.java).await()) manager.deleteDownloadedModel(m).await()
        } catch (_: Exception) {
        }
    }

    fun close() {
        translators.values.forEach { it.close() }
        translators.clear()
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { c ->
        addOnSuccessListener { c.resume(it) }
        addOnFailureListener { c.resumeWithException(it) }
    }

    companion object {
        private const val MIN_FREE_BYTES = 150L * 1024 * 1024

        /** Sprachen, die in der Auswahl angeboten werden (ISO-Codes, von ML Kit unterstützt). */
        val TARGET_LANGUAGES = listOf("it", "es", "fr", "pt", "nl", "pl", "sv", "da", "tr", "ru", "en", "de")
    }
}
