package app.rpkindwords.ads

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdsEligibilityTest {
    private val day = 24L * 60 * 60 * 1000

    @Test fun adFreeOnFirstDay() = assertFalse(AdsEligibility.adsAllowed(1_000, 1_000))
    @Test fun adFreeJustBeforeLimit() = assertFalse(AdsEligibility.adsAllowed(0, 3 * day - 1))
    @Test fun adsAfterThreeDays() = assertTrue(AdsEligibility.adsAllowed(0, 3 * day))
    @Test fun clockBeforeFirstRunStaysAdFree() = assertFalse(AdsEligibility.adsAllowed(10 * day, 5 * day))
}
