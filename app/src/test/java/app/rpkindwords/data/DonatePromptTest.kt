package app.rpkindwords.data

import org.junit.Assert.assertEquals
import org.junit.Test

class DonatePromptTest {
    @Test fun dueOnlyOnEveryFourthStart() {
        assertEquals(listOf(4, 8, 12), (1..12).filter { isDonatePromptDue(it) })
    }
}
