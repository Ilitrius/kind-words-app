package app.rpkindwords.data

import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray

/** Liest eine Supabase-REST-Anfrage (Pfad inkl. Query) als Text; null bei jedem Fehler. */
fun interface RemoteApi {
    fun get(path: String): String?
}

/** Supabase-REST (nur Lesen, anon). Der publishable Key ist öffentlich gedacht; Schreiben sperrt die DB per RLS. */
class SupabaseApi(private val baseUrl: String, private val key: String) : RemoteApi {
    override fun get(path: String): String? = try {
        val c = URL("$baseUrl/rest/v1/$path").openConnection() as HttpURLConnection
        try {
            c.connectTimeout = TIMEOUT_MS
            c.readTimeout = TIMEOUT_MS
            c.setRequestProperty("apikey", key)
            c.setRequestProperty("Accept", "application/json")
            if (c.responseCode == 200) c.inputStream.bufferedReader().use { it.readText() } else null
        } finally {
            c.disconnect()
        }
    } catch (_: Exception) {
        null
    }

    private companion object {
        const val TIMEOUT_MS = 8_000
    }
}

object RemoteContent {
    /** PostgREST liefert höchstens 1000 Zeilen je Anfrage; je Sprache gibt es mehr, daher seitenweise. */
    const val PAGE = 1000
    private const val MAX_PAGES = 20

    /**
     * Lädt die Sätze einer Sprache, wenn auf dem Server eine höhere Version liegt als [installedVersion].
     * Liefert null bei Fehler, unvollständigem Laden oder wenn nichts Neueres da ist; der Bestand bleibt dann unverändert.
     */
    fun fetch(api: RemoteApi, language: String, installedVersion: Int): ContentFile? {
        val version = parseVersion(api.get("content_version?language=eq.$language&select=version")) ?: return null
        if (version <= installedVersion) return null
        val items = ArrayList<ContentItem>()
        for (page in 0 until MAX_PAGES) {
            val body = api.get(
                "messages?language=eq.$language&active=eq.true&select=id,category,text" +
                    "&order=sort.asc,id.asc&limit=$PAGE&offset=${page * PAGE}",
            ) ?: return null
            val rows = parseItems(body) ?: return null
            items += rows.items
            if (rows.rawCount < PAGE) {
                // Sicherheitsnetz: Version erneut lesen; hat sich der Stand während des Ladens geändert, nichts übernehmen.
                val again = parseVersion(api.get("content_version?language=eq.$language&select=version"))
                return if (again == version) ContentFile(language, version, items.distinctBy { it.id }) else null
            }
        }
        return null
    }

    fun parseVersion(json: String?): Int? = try {
        json?.let { JSONArray(it).optJSONObject(0)?.optInt("version", -1) }?.takeIf { it >= 0 }
    } catch (_: Exception) {
        null
    }

    class Rows(val items: List<ContentItem>, val rawCount: Int)

    /** Ungültige Einträge werden übersprungen, kaputtes JSON ergibt null. */
    fun parseItems(json: String): Rows? = try {
        val arr = JSONArray(json)
        val items = buildList {
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                val id = o.optString("id").trim()
                val text = o.optString("text").trim()
                val category = o.optString("category").trim()
                if (id.isNotEmpty() && text.isNotEmpty() && category.isNotEmpty()) add(ContentItem(id, category, text))
            }
        }
        Rows(items, arr.length())
    } catch (_: Exception) {
        null
    }

    /** Schutz vor einem Fehlstand in der DB: ein Remote-Stand, der viel kleiner ist als der Bestand, wird abgelehnt. */
    fun plausible(existingCount: Int, remoteCount: Int): Boolean =
        remoteCount > 0 && remoteCount * 10 >= existingCount * 7
}
