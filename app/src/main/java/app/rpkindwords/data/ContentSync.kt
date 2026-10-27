package app.rpkindwords.data

import org.json.JSONObject

data class ContentItem(val id: String, val category: String, val text: String, val audience: String = Audience.ANY)

data class ContentFile(val language: String, val version: Int, val items: List<ContentItem>)

/** Ergebnis der reinen Abgleichslogik: was in die DB geschrieben werden muss. */
data class SyncPlan(
    val inserts: List<MessageEntity>,
    val updates: List<MessageEntity>,
    val deleteIds: List<String>,
) {
    val isEmpty get() = inserts.isEmpty() && updates.isEmpty() && deleteIds.isEmpty()
}

object ContentParser {
    /** Liefert null bei kaputtem/leerem JSON; einzelne ungültige Einträge werden übersprungen. */
    fun parse(json: String): ContentFile? = try {
        val root = JSONObject(json)
        val language = root.getString("language")
        val version = root.optInt("version", 1)
        val arr = root.getJSONArray("messages")
        val seen = HashSet<String>()
        val items = buildList {
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                val id = o.optString("id").trim()
                val text = o.optString("text").trim()
                val category = o.optString("category").trim()
                if (id.isEmpty() || text.isEmpty() || category.isEmpty() || !seen.add(id)) continue
                val audience = o.optString("audience").takeIf { Audience.isValid(it) } ?: Audience.ANY
                add(ContentItem(id, category, text, audience))
            }
        }
        ContentFile(language, version, items)
    } catch (_: Exception) {
        null
    }
}

object ContentSync {
    /**
     * Abgleich einer gebündelten Inhaltsdatei mit dem DB-Bestand derselben Sprache.
     * - neue ids werden eingefügt
     * - bekannte BUNDLED-ids erhalten neuen Text/Kategorie; favorite/hidden bleiben
     * - BUNDLED-ids, die nicht mehr in der Datei stehen, werden entfernt (außer Favoriten)
     * - USER-Zeilen werden nie angefasst (auch nicht bei id-Kollision)
     */
    fun plan(existingBundled: List<MessageEntity>, existingUserIds: Set<String>, file: ContentFile, now: Long): SyncPlan {
        val byId = existingBundled.associateBy { it.id }
        val inserts = ArrayList<MessageEntity>()
        val updates = ArrayList<MessageEntity>()
        for (item in file.items) {
            if (item.id in existingUserIds) continue
            val old = byId[item.id]
            if (old == null) {
                inserts += MessageEntity(item.id, file.language, item.category, item.text, Source.BUNDLED, false, false, now, item.audience)
            } else if (old.text != item.text || old.category != item.category || old.audience != item.audience) {
                updates += old.copy(category = item.category, text = item.text, audience = item.audience, updatedAt = now)
            }
        }
        val fileIds = file.items.mapTo(HashSet()) { it.id }
        val deletes = existingBundled.filter { it.id !in fileIds && !it.favorite }.map { it.id }
        return SyncPlan(inserts, updates, deletes)
    }

    /** Nur synchronisieren, wenn die Datei neuer ist als die installierte Version. */
    fun needsSync(installedVersion: Int, file: ContentFile) = file.version > installedVersion
}
