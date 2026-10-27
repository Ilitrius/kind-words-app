package app.rpkindwords.data

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

/** Schützt den freundlichen Ton: keine Neckereien, Beleidigungen oder negativ gefärbten Wörter in den mitgelieferten Sätzen. */
class ContentToneTest {
    /** Wortstämme, die als Teilstring geprüft werden. */
    private val stems = listOf(
        "nervensäge", "nervt", "schnarch", "blöd", "doof", "dumm", "idiot", "hässlich", "zickig", "sturkopf", "launisch",
        "chaotisch", "trottel", "meckern", "streit", "verletz", "wehtun", "traurig", "einsam", "schlecht", "wütend",
        "katastrophe", "versag", "peinlich", "deine schuld", "meine schuld",
        "pain in the", "grumpy", "annoying", "stupid", "messed up", "selfish", "embarrass", "disaster", "my fault",
        "your fault", "wrong of me", "terrible", "awful", "snorer", "lonely",
        // Couch/Sofa ist kein romantisches Wort (Nutzerregel 2026-10-27)
        "couch", "sofa",
    )

    /** Kurze Wörter, die nur als ganzes Wort gelten (vermeidet z. B. "whatever" -> "hate"). */
    private val words = listOf("mist", "stur", "depp", "ärger", "hasse", "dumb", "sucks", "hate", "ugly", "stubborn", "lazy", "sad", "empty", "hurt", "fight", "angry")

    private fun violations(text: String): List<String> {
        val low = text.lowercase()
        val padded = " " + low.replace(Regex("[^\\p{L} ]"), " ") + " "
        return stems.filter { low.contains(it) } + words.filter { padded.contains(" $it ") }
    }

    @Test fun noNegativeOrTeasingWords() {
        for (lang in Languages.supported) {
            val f = File("src/main/assets/messages/messages_$lang.json")
            assumeTrue(f.exists())
            val parsed = ContentParser.parse(f.readText())!!
            val hits = parsed.items.filter { violations(it.text).isNotEmpty() }
            assertTrue("Negativ/neckend in $lang: " + hits.joinToString { "${it.id}: ${it.text}" }, hits.isEmpty())
        }
    }

    @Test fun detectorCatchesKnownBadAndIgnoresInnocentWords() {
        assertTrue(violations("Ich lieb dich, du Nervensäge.").isNotEmpty())
        assertTrue(violations("I hate fighting with you").isNotEmpty())
        assertTrue(violations("I'm on your side, whatever comes.").isEmpty())
        assertTrue(violations("Entschuldige, ich schulde dir was.").isEmpty())
    }
}
