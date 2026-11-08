package app.rpkindwords.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.rpkindwords.reminder.ReminderMode
import app.rpkindwords.reminder.ReminderSettings
import java.time.DayOfWeek
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

object Languages {
    val supported = listOf("de", "en")
    const val FALLBACK = "en"

    /** Systemsprache -> unterstützte Inhaltssprache (sonst Englisch). */
    fun resolve(systemLanguage: String?): String =
        systemLanguage?.lowercase()?.takeIf { it in supported } ?: FALLBACK
}

class SettingsRepository(private val context: Context) {
    private object K {
        val startCount = intPreferencesKey("start_count")
        val trConsentVersion = intPreferencesKey("tr_consent_version")
        val trConsentAt = longPreferencesKey("tr_consent_at")
        val trTarget = stringPreferencesKey("tr_target")
        val trMobile = booleanPreferencesKey("tr_mobile")
        val speechRate = intPreferencesKey("speech_rate_pct")
        val learning = stringPreferencesKey("learning_v1")
        val onboardingSeen = booleanPreferencesKey("onboarding_seen")
        val enabled = booleanPreferencesKey("rem_enabled")
        val mode = stringPreferencesKey("rem_mode")
        val perDay = intPreferencesKey("rem_per_day")
        val winStart = intPreferencesKey("rem_win_start")
        val winEnd = intPreferencesKey("rem_win_end")
        val fixed = stringPreferencesKey("rem_fixed")
        val days = stringPreferencesKey("rem_days")
        val contentLang = stringPreferencesKey("content_lang")
        val lastUpdateCheck = longPreferencesKey("last_update_check")
        fun contentVersion(lang: String) = intPreferencesKey("content_version_$lang")
    }

    private val d = ReminderSettings()

    val reminder: Flow<ReminderSettings> = context.dataStore.data.map { reminderFrom(it) }

    /** Explizit gewählte Inhaltssprache oder null = der Systemsprache folgen. */
    val contentLanguageOverride: Flow<String?> = context.dataStore.data.map { it[K.contentLang]?.takeIf { l -> l in Languages.supported } }

    suspend fun updateReminder(transform: (ReminderSettings) -> ReminderSettings) {
        context.dataStore.edit { p ->
            val cur = reminderFrom(p)
            val n = transform(cur)
            p[K.enabled] = n.enabled
            p[K.mode] = n.mode.name
            p[K.perDay] = n.perDay
            p[K.winStart] = n.windowStartMin
            p[K.winEnd] = n.windowEndMin
            p[K.fixed] = n.fixedTimesMin.joinToString(",")
            p[K.days] = n.days.joinToString(",") { it.value.toString() }
        }
    }

    private fun reminderFrom(p: Preferences) = ReminderSettings(
        enabled = p[K.enabled] ?: d.enabled,
        mode = p[K.mode]?.let { runCatching { ReminderMode.valueOf(it) }.getOrNull() } ?: d.mode,
        perDay = p[K.perDay] ?: d.perDay,
        windowStartMin = p[K.winStart] ?: d.windowStartMin,
        windowEndMin = p[K.winEnd] ?: d.windowEndMin,
        fixedTimesMin = p[K.fixed]?.let { s -> s.split(',').mapNotNull { it.toIntOrNull() } } ?: d.fixedTimesMin,
        days = p[K.days]?.let { s -> s.split(',').mapNotNull { it.toIntOrNull() }.filter { it in 1..7 }.map { DayOfWeek.of(it) }.toSet() }
            ?: d.days,
    )

    suspend fun setContentLanguage(lang: String?) {
        context.dataStore.edit { if (lang == null) it.remove(K.contentLang) else it[K.contentLang] = lang }
    }

    suspend fun contentVersion(lang: String): Int = context.dataStore.data.first()[K.contentVersion(lang)] ?: 0

    suspend fun setContentVersion(lang: String, v: Int) {
        context.dataStore.edit { it[K.contentVersion(lang)] = v }
    }

    /** Höchstens alle 24 h auf Updates prüfen (spart Akku und Netz); merkt sich den Zeitpunkt, wenn geprüft werden soll. */
    suspend fun shouldCheckUpdate(now: Long = System.currentTimeMillis()): Boolean {
        val last = context.dataStore.data.first()[K.lastUpdateCheck] ?: 0L
        if (now - last in 0 until UPDATE_CHECK_INTERVAL_MS) return false
        context.dataStore.edit { it[K.lastUpdateCheck] = now }
        return true
    }

    /** Sprechtempo beim Vorlesen in Prozent (Standard 80: ruhig und langsam). */
    val speechRatePct: Flow<Int> = context.dataStore.data.map { it[K.speechRate] ?: DEFAULT_SPEECH_RATE_PCT }

    suspend fun setSpeechRate(pct: Int) { context.dataStore.edit { it[K.speechRate] = pct.coerceIn(50, 120) } }

    /** Gewählte Stimme je Sprache (Stimmenname der Sprachausgabe) oder null für die Standardstimme. */
    fun voiceFor(lang: String): Flow<String?> = context.dataStore.data.map { it[stringPreferencesKey("voice_$lang")] }

    suspend fun setVoice(lang: String, name: String?) {
        context.dataStore.edit { p ->
            val k = stringPreferencesKey("voice_$lang")
            if (name == null) p.remove(k) else p[k] = name
        }
    }

    /** Einwilligung zur Übersetzung (Google ML Kit) und Zielsprache; verlässt das Gerät nie. */
    val translationPrefs: Flow<TranslationPrefs> = context.dataStore.data.map {
        TranslationPrefs(
            consent = (it[K.trConsentVersion] ?: 0) >= TRANSLATION_CONSENT_VERSION,
            consentAt = it[K.trConsentAt] ?: 0L,
            target = it[K.trTarget],
            allowMobile = it[K.trMobile] ?: false,
        )
    }

    suspend fun hasTranslationConsent(): Boolean = translationPrefs.first().consent

    suspend fun grantTranslationConsent(now: Long = System.currentTimeMillis()) {
        context.dataStore.edit { it[K.trConsentVersion] = TRANSLATION_CONSENT_VERSION; it[K.trConsentAt] = now }
    }

    /** Widerruf: Einwilligung und Zielsprache entfernen. */
    suspend fun revokeTranslationConsent() {
        context.dataStore.edit { it.remove(K.trConsentVersion); it.remove(K.trConsentAt); it.remove(K.trTarget) }
    }

    suspend fun setTranslationTarget(lang: String?) {
        context.dataStore.edit { p -> if (lang == null) p.remove(K.trTarget) else p[K.trTarget] = lang }
    }

    suspend fun setTranslationAllowMobile(allow: Boolean) { context.dataStore.edit { it[K.trMobile] = allow } }

    /** Lokal gelerntes Nutzungsverhalten (siehe [LearningState]); verlässt das Gerät nie. */
    suspend fun learning(): LearningState = LearningState.decode(context.dataStore.data.first()[K.learning])

    suspend fun updateLearning(transform: (LearningState) -> LearningState) {
        context.dataStore.edit { p -> p[K.learning] = transform(LearningState.decode(p[K.learning])).encode() }
    }

    /** Einführung schon gesehen? Ohne Eintrag (neue Installation oder Update) false. */
    val onboardingSeen: Flow<Boolean> = context.dataStore.data.map { it[K.onboardingSeen] ?: false }

    suspend fun setOnboardingSeen() { context.dataStore.edit { it[K.onboardingSeen] = true } }

    /** Zählt einen echten App-Start; true, wenn die Spendenbitte fällig ist (jeder DONATE_PROMPT_EVERY-te Start). */
    suspend fun registerStartAndCheckDonatePrompt(): Boolean {
        var count = 0
        context.dataStore.edit { p ->
            count = (p[K.startCount] ?: 0) + 1
            p[K.startCount] = count
        }
        return isDonatePromptDue(count)
    }

    private companion object {
        const val DEFAULT_SPEECH_RATE_PCT = 80
        const val UPDATE_CHECK_INTERVAL_MS = 24L * 60 * 60 * 1000
    }
}

const val DONATE_PROMPT_EVERY = 4

fun isDonatePromptDue(startCount: Int): Boolean = startCount > 0 && startCount % DONATE_PROMPT_EVERY == 0

/** Version des Einwilligungstextes zur Übersetzung; bei wesentlicher Änderung erhöhen (dann erneut fragen). */
const val TRANSLATION_CONSENT_VERSION = 1

data class TranslationPrefs(val consent: Boolean = false, val consentAt: Long = 0L, val target: String? = null, val allowMobile: Boolean = false) {
    /** Übersetzt wird nur mit Einwilligung und gewählter Zielsprache. */
    val active: Boolean get() = consent && target != null
}
