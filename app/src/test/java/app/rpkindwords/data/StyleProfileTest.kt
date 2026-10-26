package app.rpkindwords.data

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StyleProfileTest {
    @Test fun intenseSentenceScoresHigherThanPlainOne() {
        val strong = StyleAnalyzer.analyze("Ich liebe dich über alles!")
        val plain = StyleAnalyzer.analyze("Danke fürs Kochen.")
        assertTrue(strong.intensity > plain.intensity + 0.4)
        assertTrue(strong.energy > plain.energy)
    }

    @Test fun playfulAndCasualAreDetectedInBothLanguages() {
        val de = StyleAnalyzer.analyze("Hab dich lieb, du Nervensäge haha")
        val en = StyleAnalyzer.analyze("You're my favorite pain in the neck lol")
        val formal = StyleAnalyzer.analyze("Ich danke dir für deine Unterstützung.")
        assertTrue(de.playful > formal.playful)
        assertTrue(en.playful > formal.playful)
        assertTrue(de.casual > formal.casual)
    }

    @Test fun emptyProfileGivesUniformWeights() {
        assertEquals(1.0, StyleMatcher.weight("irgendwas", StyleProfile.EMPTY), 1e-9)
    }

    @Test fun matchingStyleIsFavoredButNeverExcluded() {
        val own = listOf("Ich liebe dich so sehr!", "Ich vermiss dich immer!", "Du bist mein ein und alles!")
        val profile = StyleProfile.from(own)
        val close = StyleMatcher.weight("Ich liebe dich immer und überall!", profile)
        val far = StyleMatcher.weight("ok", profile)
        assertTrue(close > far)
        assertTrue(far >= 1.0)
        assertTrue(close <= 1.0 + 4.0 + 1e-9)
    }

    @Test fun pickIsDeterministicWithSeedAndSkewedToMatches() {
        val profile = StyleProfile.from(listOf("Ich liebe dich so sehr!", "Ich vermiss dich immer!", "Du bist mein ein und alles!"))
        val items = listOf("Ich liebe dich so sehr, immer!", "Danke fürs Kochen.")
        val rnd = Random(1)
        val hits = (1..2000).count { StyleMatcher.pick(items, profile, rnd) { it } == items[0] }
        assertTrue("intense sentence should win clearly: $hits", hits > 1300)
        assertEquals(StyleMatcher.pick(items, profile, Random(5)) { it }, StyleMatcher.pick(items, profile, Random(5)) { it })
    }

    @Test fun adapterMirrorsEndingOnlyWithEnoughEvidence() {
        val excited = StyleProfile.from(listOf("Du bist toll!", "Ich freu mich auf dich!"))
        assertEquals("Ich hab dich lieb!", StyleAdapter.adapt("Ich hab dich lieb.", excited))
        assertEquals("Wirklich?", StyleAdapter.adapt("Wirklich?", excited))
        assertEquals("Kurz und gut…", StyleAdapter.adapt("Kurz und gut…", excited))
        val casual = StyleProfile.from(listOf("hab dich lieb", "bis später"))
        assertEquals("Ich hab dich lieb", StyleAdapter.adapt("Ich hab dich lieb.", casual))
        val single = StyleProfile.from(listOf("Du bist toll!"))
        assertEquals("Ich hab dich lieb.", StyleAdapter.adapt("Ich hab dich lieb.", single))
        assertEquals("Ich hab dich lieb.", StyleAdapter.adapt("Ich hab dich lieb.", StyleProfile.EMPTY))
    }

    @Test fun profileAveragesOwnSentences() {
        val p = StyleProfile.from(listOf("Kurz!", "Noch einer!", "und einer"))
        assertEquals(3, p.count)
        assertEquals(1.0, p.confidence, 1e-9)
        assertEquals(2.0 / 3.0, p.exclamationEnd, 1e-9)
        assertEquals(1.0 / 3.0, p.noPunctuationEnd, 1e-9)
    }
}
