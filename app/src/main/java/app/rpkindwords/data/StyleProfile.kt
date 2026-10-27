package app.rpkindwords.data

import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Stil eines Satzes auf fünf Achsen (alle 0..1). Alles lokal und regelbasiert, keine KI, kein Netzwerk.
 *  - intensity: emotionale Stärke (Liebeserklärung, "immer", "vermiss", Verstärker, Ausrufezeichen)
 *  - playful:   Verspieltheit/Humor (Scherzwörter, Fragezeichen, Emoji)
 *  - energy:    Temperatur/Energie (Ausrufezeichen, GROSSBUCHSTABEN)
 *  - length:    Form/Länge (0 = sehr kurz, 1 = ca. 110 Zeichen und mehr)
 *  - casual:    Lockerheit (Verkürzungen, Umgangssprache, kein Schlusspunkt)
 */
data class StyleVector(
    val intensity: Double,
    val playful: Double,
    val energy: Double,
    val length: Double,
    val casual: Double,
) {
    companion object {
        val NEUTRAL = StyleVector(0.4, 0.2, 0.2, 0.4, 0.4)
    }
}

object StyleAnalyzer {
    private val intense = listOf(
        "liebe dich", "lieb dich", "love you", "vermiss", "miss you", "immer", "always", "nie ", "never", "alles",
        "everything", "von herzen", "mehr als", "more than", "furchtbar", "unendlich", "forever", "ohne dich",
        "without you", "über alles", "sehnsucht",
    )
    private val strong = listOf("liebe dich", "love you", "von herzen", "über alles", "mein ein und alles")
    private val intensifiers = listOf(
        "sehr", "total", "so viel", "ganz doll", "mega", "super", "unglaublich", "really", "so much", "very",
        "incredibly", "absolutely", "deeply", "madly",
    )
    private val humor = listOf(
        "haha", "hihi", "lol", "witz", "lustig", "lach", "funny", "joke", "laugh", "silly", "nervensäge", "schnarch",
        "chaos", "pizza", "snack", "grumpy", "pain in the neck", "verrückt", "crazy", "albern",
    )
    private val casualWords = listOf(
        " hab ", " ne ", " nen ", " grad ", " mal ", " ey ", "moin", " hey ", "gonna", "wanna", "kinda", "bisschen",
        " ok ", " na ", " ne,",
    )

    private fun count(haystack: String, needles: List<String>) = needles.count { haystack.contains(it) }

    fun analyze(text: String): StyleVector {
        val t = text.trim()
        if (t.isEmpty()) return StyleVector.NEUTRAL
        val low = " ${t.lowercase()} "
        val excl = t.count { it == '!' }
        val questions = t.count { it == '?' }
        val emoji = t.codePoints().filter { it >= 0x1F300 || it == 0x2764 }.count()
        val capsWords = t.split(' ', '\n').count { w -> w.length >= 3 && w.all { it.isUpperCase() || !it.isLetter() } && w.any { it.isLetter() } }
        val endsPunct = t.last() in ".!?…"

        var intensity = 0.38 * minOf(2, count(low, intense)) + 0.14 * minOf(2, count(low, intensifiers)) + 0.12 * minOf(3, excl)
        if (count(low, strong) > 0) intensity = maxOf(intensity, 0.85)
        val playful = 0.4 * minOf(2, count(low, humor)) + 0.25 * (if (questions > 0) 1 else 0) + 0.4 * (if (emoji > 0) 1 else 0)
        val energy = 0.35 * minOf(3, excl) + 0.25 * minOf(2, capsWords)
        val length = t.length / 110.0
        val casual = 0.3 * minOf(3, count(low, casualWords)) + 0.3 * (if (t.contains('\'') || t.contains('’')) 1 else 0) +
            0.2 * (if (!endsPunct) 1 else 0)

        return StyleVector(
            intensity.coerceIn(0.0, 1.0), playful.coerceIn(0.0, 1.0), energy.coerceIn(0.0, 1.0),
            length.coerceIn(0.0, 1.0), casual.coerceIn(0.0, 1.0),
        )
    }
}

/** Durchschnittsstil der eigenen Sätze. Ab 3 Sätzen volle Wirkung, bei weniger entsprechend schwächer. */
data class StyleProfile(
    val count: Int,
    val vector: StyleVector,
    /** Anteil der eigenen Sätze, die mit "!" enden. */
    val exclamationEnd: Double,
    /** Anteil der eigenen Sätze ohne Satzzeichen am Ende. */
    val noPunctuationEnd: Double,
) {
    val confidence: Double get() = minOf(1.0, count / 3.0)

    companion object {
        val EMPTY = StyleProfile(0, StyleVector.NEUTRAL, 0.0, 0.0)

        fun from(texts: List<String>): StyleProfile {
            val clean = texts.map { it.trim() }.filter { it.isNotEmpty() }
            if (clean.isEmpty()) return EMPTY
            val vs = clean.map(StyleAnalyzer::analyze)
            fun avg(f: (StyleVector) -> Double) = vs.map(f).average()
            return StyleProfile(
                count = clean.size,
                vector = StyleVector(avg { it.intensity }, avg { it.playful }, avg { it.energy }, avg { it.length }, avg { it.casual }),
                exclamationEnd = clean.count { it.endsWith("!") }.toDouble() / clean.size,
                noPunctuationEnd = clean.count { it.last() !in ".!?…" }.toDouble() / clean.size,
            )
        }
    }
}

object StyleMatcher {
    private const val BASE_WEIGHT = 1.0
    private const val MAX_BONUS = 4.0

    /** 1.0 = identischer Stil, 0.0 = maximal verschieden (gewichteter Abstand). */
    fun similarity(a: StyleVector, b: StyleVector): Double {
        val d = 1.5 * sq(a.intensity - b.intensity) + 1.0 * sq(a.playful - b.playful) + 0.8 * sq(a.energy - b.energy) +
            1.0 * sq(a.length - b.length) + 0.7 * sq(a.casual - b.casual)
        val max = 1.5 + 1.0 + 0.8 + 1.0 + 0.7
        return (1.0 - sqrt(d / max)).coerceIn(0.0, 1.0)
    }

    /** Ziehgewicht eines Satzes: immer > 0, passende Sätze bis zu 5x wahrscheinlicher (abhängig von der Datenlage). */
    fun weight(text: String, profile: StyleProfile): Double {
        if (profile.count == 0) return BASE_WEIGHT
        val s = similarity(StyleAnalyzer.analyze(text), profile.vector)
        return BASE_WEIGHT + MAX_BONUS * profile.confidence * s * s
    }

    fun <T> pick(items: List<T>, profile: StyleProfile, rng: Random, textOf: (T) -> String): T? {
        if (items.isEmpty()) return null
        val weights = items.map { weight(textOf(it), profile) }
        var r = rng.nextDouble() * weights.sum()
        for (i in items.indices) {
            r -= weights[i]
            if (r <= 0) return items[i]
        }
        return items.last()
    }

    private fun sq(x: Double) = x * x
}

/** Gleicht nur das Satzende dem eigenen Stil an ("!" oder kein Punkt), nie den Inhalt. */
object StyleAdapter {
    private const val MIN_COUNT = 2
    private const val THRESHOLD = 0.6

    fun adapt(text: String, profile: StyleProfile): String {
        if (profile.count < MIN_COUNT || text.isBlank()) return text
        val t = text.trimEnd()
        if (t.endsWith("...") || t.endsWith("…")) return text
        return when {
            profile.exclamationEnd >= THRESHOLD && t.endsWith(".") -> t.dropLast(1) + "!"
            profile.noPunctuationEnd >= THRESHOLD && t.endsWith(".") -> t.dropLast(1)
            else -> text
        }
    }
}
