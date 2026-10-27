package app.rpkindwords.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        val enabled = booleanPreferencesKey("rem_enabled")
        val mode = stringPreferencesKey("rem_mode")
        val perDay = intPreferencesKey("rem_per_day")
        val winStart = intPreferencesKey("rem_win_start")
        val winEnd = intPreferencesKey("rem_win_end")
        val fixed = stringPreferencesKey("rem_fixed")
        val days = stringPreferencesKey("rem_days")
        val contentLang = stringPreferencesKey("content_lang")
        val audience = stringPreferencesKey("audience")
        fun contentVersion(lang: String) = intPreferencesKey("content_version_$lang")
    }

    private val d = ReminderSettings()

    val reminder: Flow<ReminderSettings> = context.dataStore.data.map { reminderFrom(it) }

    /** Explizit gewählte Inhaltssprache oder null = der Systemsprache folgen. */
    val contentLanguageOverride: Flow<String?> = context.dataStore.data.map { it[K.contentLang]?.takeIf { l -> l in Languages.supported } }

    /** Für wen die Sätze sind ([Audience.FEMALE]/[Audience.MALE]/[Audience.DIVERSE]) oder null = noch nicht gewählt (nur neutrale Sätze). */
    val audience: Flow<String?> = context.dataStore.data.map { it[K.audience]?.takeIf(Audience::isChoice) }

    suspend fun setAudience(value: String?) {
        context.dataStore.edit { if (value == null || !Audience.isChoice(value)) it.remove(K.audience) else it[K.audience] = value }
    }

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
}
