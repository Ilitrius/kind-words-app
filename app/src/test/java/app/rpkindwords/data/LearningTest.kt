package app.rpkindwords.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningTest {
    private val now = 1_000_000_000L

    @Test fun noDataMeansNeutralWeights() {
        val s = LearningState()
        assertEquals(1.0, s.weight("de-love-001", "love", now), 1e-9)
    }

    @Test fun usedSentencesBeatIgnoredOnes() {
        var s = LearningState()
        repeat(4) { s = s.shown("a", now - LearningState.RECENT_MS * 2).positive("a") }
        repeat(4) { s = s.shown("b", now - LearningState.RECENT_MS * 2).skipped("b") }
        assertTrue(s.messageFactor("a") > 1.2)
        assertTrue(s.messageFactor("b") < 0.8)
        assertTrue(s.messageFactor("b") >= 0.4)
    }

    @Test fun recentlyShownIsLessLikelyAndRecoversAfterADay() {
        val s = LearningState().shown("a", now)
        assertTrue(s.freshnessFactor("a", now + 60_000) < 0.3)
        assertEquals(1.0, s.freshnessFactor("a", now + LearningState.RECENT_MS), 1e-9)
    }

    @Test fun categoryPreferenceNeedsEnoughDataAndStaysBounded() {
        var s = LearningState()
        repeat(5) { i -> s = s.shown("de-love-00$i", 1).positive("de-love-00$i").positive("de-love-00$i") }
        assertEquals(1.0, s.categoryFactor("love"), 1e-9)
        repeat(25) { i -> s = s.shown("de-humor-0${10 + i}", 1) }
        assertTrue(s.categoryFactor("love") > 1.2)
        assertTrue(s.categoryFactor("humor") < 1.0)
        assertTrue(s.categoryFactor("love") <= 1.8 && s.categoryFactor("humor") >= 0.6)
    }

    @Test fun encodeDecodeRoundTripAndGarbageIsIgnored() {
        val s = LearningState().shown("de-love-001", 5).positive("de-love-001").skipped("user-x")
        assertEquals(s, LearningState.decode(s.encode()))
        assertEquals(LearningState(), LearningState.decode("not json"))
        assertEquals(LearningState(), LearningState.decode(null))
    }

    @Test fun sizeIsCapped() {
        var s = LearningState()
        repeat(LearningState.MAX_ENTRIES + 50) { s = s.shown("de-love-$it", it.toLong() + 1) }
        assertEquals(LearningState.MAX_ENTRIES, s.stats.size)
        assertTrue(s.stats.containsKey("de-love-${LearningState.MAX_ENTRIES + 49}"))
    }

    @Test fun categoryFromId() {
        assertEquals("love", LearningState.categoryOf("de-love-012"))
        assertEquals("custom", LearningState.categoryOf("user-1234"))
    }
}
