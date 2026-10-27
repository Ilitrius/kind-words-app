package app.rpkindwords.data

import app.rpkindwords.reminder.ReminderMode
import app.rpkindwords.reminder.ReminderPlanner
import app.rpkindwords.reminder.ReminderSettings
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.DayOfWeek
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/** Eigener Satz im Backup; `favorite` markiert Favoriten unter den eigenen Sätzen. */
data class BackupCustom(val language: String, val text: String, val favorite: Boolean)

data class BackupData(
    val custom: List<BackupCustom>,
    /** IDs gebündelter Sätze, die als Favorit markiert sind. */
    val favorites: List<String>,
    /** null = Systemsprache. */
    val contentLang: String?,
    val reminder: ReminderSettings,
)

/** Backup-Format v1 (JSON, offen lesbar). Lesen ist streng validiert: ungültige Dateien ergeben null. */
object BackupCodec {
    const val FORMAT = "kindwords-backup"
    const val VERSION = 1
    const val MAX_BYTES = 1_000_000
    const val MAX_TEXT = 500
    const val MAX_ITEMS = 2_000

    /** Liest höchstens MAX_BYTES; größere Eingaben ergeben null (gelten als ungültig). */
    fun readLimited(input: InputStream): String? {
        val out = ByteArrayOutputStream()
        val buf = ByteArray(8192)
        while (true) {
            val n = input.read(buf)
            if (n < 0) break
            out.write(buf, 0, n)
            if (out.size() > MAX_BYTES) return null
        }
        return out.toString(Charsets.UTF_8.name())
    }

    fun encode(d: BackupData): String {
        val r = d.reminder
        return JSONObject()
            .put("format", FORMAT)
            .put("version", VERSION)
            .put(
                "customMessages",
                JSONArray().also { a ->
                    d.custom.forEach {
                        a.put(JSONObject().put("language", it.language).put("text", it.text).put("favorite", it.favorite))
                    }
                },
            )
            .put("favorites", JSONArray(d.favorites))
            .put(
                "settings",
                JSONObject()
                    .put("contentLang", d.contentLang ?: JSONObject.NULL)
                    .put(
                        "reminder",
                        JSONObject()
                            .put("enabled", r.enabled)
                            .put("mode", r.mode.name)
                            .put("perDay", r.perDay)
                            .put("windowStartMin", r.windowStartMin)
                            .put("windowEndMin", r.windowEndMin)
                            .put("fixedTimesMin", JSONArray(r.fixedTimesMin))
                            .put("days", JSONArray(r.days.map { it.value }.sorted())),
                    ),
            )
            .toString(2)
    }

    fun decode(json: String): BackupData? {
        if (json.length > MAX_BYTES) return null
        return try {
            val o = JSONObject(json)
            if (o.optString("format") != FORMAT || o.optInt("version", -1) != VERSION) return null
            val custom = ArrayList<BackupCustom>()
            o.optJSONArray("customMessages")?.let { a ->
                for (i in 0 until minOf(a.length(), MAX_ITEMS)) {
                    val c = a.optJSONObject(i) ?: continue
                    val lang = c.optString("language")
                    val text = c.optString("text").trim().take(MAX_TEXT)
                    if (lang in Languages.supported && text.isNotEmpty()) {
                        custom += BackupCustom(lang, text, c.optBoolean("favorite", false))
                    }
                }
            }
            val favorites = ArrayList<String>()
            o.optJSONArray("favorites")?.let { a ->
                for (i in 0 until minOf(a.length(), MAX_ITEMS)) a.optString(i).takeIf { it.isNotEmpty() && it.length <= 100 }?.let(favorites::add)
            }
            val settings = o.optJSONObject("settings")
            val lang = settings?.optString("contentLang", "")?.takeIf { it in Languages.supported }
            BackupData(custom, favorites, lang, decodeReminder(settings?.optJSONObject("reminder")))
        } catch (_: JSONException) {
            null
        }
    }

    private fun decodeReminder(o: JSONObject?): ReminderSettings {
        val d = ReminderSettings()
        if (o == null) return d
        val times = o.optJSONArray("fixedTimesMin")?.let { a ->
            (0 until minOf(a.length(), 6)).map { a.optInt(it, -1) }.filter { it in 0 until 24 * 60 }
        }
        val days = o.optJSONArray("days")?.let { a ->
            (0 until minOf(a.length(), 7)).map { a.optInt(it, 0) }.filter { it in 1..7 }.map { DayOfWeek.of(it) }.toSet()
        }
        return d.copy(
            enabled = o.optBoolean("enabled", d.enabled),
            mode = runCatching { ReminderMode.valueOf(o.optString("mode")) }.getOrDefault(d.mode),
            perDay = o.optInt("perDay", d.perDay).coerceIn(ReminderPlanner.MIN_PER_DAY, ReminderPlanner.MAX_PER_DAY),
            windowStartMin = o.optInt("windowStartMin", d.windowStartMin).coerceIn(0, 24 * 60 - 1),
            windowEndMin = o.optInt("windowEndMin", d.windowEndMin).coerceIn(0, 24 * 60 - 1),
            fixedTimesMin = times ?: d.fixedTimesMin,
            days = days?.takeIf { it.isNotEmpty() } ?: d.days,
        )
    }
}
