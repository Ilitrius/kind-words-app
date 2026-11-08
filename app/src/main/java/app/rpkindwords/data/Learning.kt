package app.rpkindwords.data

import org.json.JSONArray
import org.json.JSONObject

/** Lokale Rückmeldung zu einem Satz: wie oft gezeigt, wie oft positiv (geteilt, kopiert, Favorit), wie oft übersprungen. */
data class Stat(val shown: Int = 0, val positive: Int = 0, val skipped: Int = 0, val lastShown: Long = 0L)

/**
 * Was die App auf dem Gerät über den Geschmack der Nutzerin oder des Nutzers lernt. Alles lokal, nichts verlässt das Gerät.
 * Gelernt wird aus Verhalten (Teilen, Kopieren, Favorit = positiv; direkt weitergeklickt = übersprungen) und liefert
 * Gewichte für die Vorschlagsauswahl: pro Satz, pro Kategorie und gegen Wiederholungen. Gewichte sind immer > 0.
 */
data class LearningState(val stats: Map<String, Stat> = emptyMap()) {

    fun shown(id: String, now: Long) = update(id) { it.copy(shown = it.shown + 1, lastShown = now) }
    fun positive(id: String) = update(id) { it.copy(positive = it.positive + 1) }
    fun skipped(id: String) = update(id) { it.copy(skipped = it.skipped + 1) }

    private fun update(id: String, f: (Stat) -> Stat): LearningState {
        val next = stats + (id to f(stats[id] ?: Stat()))
        return LearningState(if (next.size > MAX_ENTRIES) next.entries.sortedByDescending { it.value.lastShown }.take(MAX_ENTRIES).associate { it.key to it.value } else next)
    }

    /** Beliebtheit eines einzelnen Satzes: 0.55 (oft gezeigt, nie genutzt) bis 1.6 (meist genutzt). Ohne Daten 1.0. */
    fun messageFactor(id: String): Double {
        val s = stats[id] ?: return 1.0
        val rate = (s.positive + 1.0) / (s.shown + 2.0)
        val skipPenalty = 1.0 - 0.08 * minOf(4, s.skipped)
        return ((0.4 + 1.2 * rate) * skipPenalty).coerceIn(0.4, 1.6)
    }

    /** Gegen Wiederholungen: kürzlich gezeigte Sätze sind unwahrscheinlicher (nach 24 h wieder voll). */
    fun freshnessFactor(id: String, now: Long): Double {
        val last = stats[id]?.lastShown ?: return 1.0
        if (last <= 0L) return 1.0
        val age = (now - last).coerceAtLeast(0L)
        return if (age >= RECENT_MS) 1.0 else 0.15 + 0.85 * (age.toDouble() / RECENT_MS)
    }

    /** Vorliebe für eine Kategorie relativ zum Durchschnitt, 0.6 bis 1.8; wirkt erst ab genug Daten (20 gezeigte Sätze). */
    fun categoryFactor(category: String): Double {
        val all = stats.values
        val shownAll = all.sumOf { it.shown }
        if (shownAll < MIN_SHOWN_FOR_CATEGORY) return 1.0
        val rateAll = (all.sumOf { it.positive } + 1.0) / (shownAll + 3.0)
        val inCat = stats.filterKeys { categoryOf(it) == category }.values
        val rateCat = (inCat.sumOf { it.positive } + 1.0) / (inCat.sumOf { it.shown } + 3.0)
        return (rateCat / rateAll).coerceIn(0.6, 1.8)
    }

    fun weight(id: String, category: String, now: Long): Double =
        messageFactor(id) * freshnessFactor(id, now) * categoryFactor(category)

    fun encode(): String {
        val o = JSONObject()
        for ((id, s) in stats) o.put(id, JSONArray(listOf(s.shown, s.positive, s.skipped, s.lastShown)))
        return o.toString()
    }

    companion object {
        const val MAX_ENTRIES = 600
        const val RECENT_MS = 24L * 60 * 60 * 1000
        const val MIN_SHOWN_FOR_CATEGORY = 20

        /** Kategorie aus der Satz-ID ("de-love-012" -> love); eigene Sätze ("user-...") sind "custom". */
        fun categoryOf(id: String): String = id.split('-').takeIf { it.size >= 3 && it[0].length == 2 }?.get(1) ?: MessageRepository.CATEGORY_CUSTOM

        fun decode(json: String?): LearningState {
            if (json.isNullOrBlank()) return LearningState()
            return try {
                val o = JSONObject(json)
                val map = HashMap<String, Stat>()
                for (id in o.keys()) {
                    val a = o.optJSONArray(id) ?: continue
                    map[id] = Stat(a.optInt(0), a.optInt(1), a.optInt(2), a.optLong(3))
                }
                LearningState(map)
            } catch (_: Exception) {
                LearningState()
            }
        }
    }
}
