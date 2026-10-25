package app.rpkindwords.reminder

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderPlannerTest {
    private val zone = ZoneId.of("Europe/Berlin")
    private fun at(y: Int, m: Int, d: Int, h: Int, min: Int) = ZonedDateTime.of(y, m, d, h, min, 0, 0, zone)

    // 2026-10-26 ist ein Montag
    private val fixed = ReminderSettings(enabled = true, mode = ReminderMode.FIXED, fixedTimesMin = listOf(18 * 60, 8 * 60))

    @Test fun disabledGivesNull() {
        assertNull(ReminderPlanner.nextTrigger(fixed.copy(enabled = false), at(2026, 10, 26, 7, 0)))
    }

    @Test fun fixedPicksNextTimeToday() {
        assertEquals(at(2026, 10, 26, 8, 0), ReminderPlanner.nextTrigger(fixed, at(2026, 10, 26, 7, 0)))
        assertEquals(at(2026, 10, 26, 18, 0), ReminderPlanner.nextTrigger(fixed, at(2026, 10, 26, 8, 0)))
    }

    @Test fun fixedRollsToNextAllowedDay() {
        val s = fixed.copy(days = setOf(DayOfWeek.WEDNESDAY))
        assertEquals(at(2026, 10, 28, 8, 0), ReminderPlanner.nextTrigger(s, at(2026, 10, 26, 19, 0)))
    }

    @Test fun noDaysOrNoTimesGivesNull() {
        assertNull(ReminderPlanner.nextTrigger(fixed.copy(days = emptySet()), at(2026, 10, 26, 7, 0)))
        assertNull(ReminderPlanner.nextTrigger(fixed.copy(fixedTimesMin = emptyList()), at(2026, 10, 26, 7, 0)))
    }

    @Test fun randomTimesRespectWindowCountAndAreDeterministic() {
        val s = ReminderSettings(enabled = true, mode = ReminderMode.RANDOM, perDay = 3, windowStartMin = 9 * 60, windowEndMin = 21 * 60)
        val date = LocalDate.of(2026, 10, 26)
        val t = ReminderPlanner.timesForDate(s, date)
        assertEquals(3, t.size)
        assertTrue(t.all { it in 9 * 60..21 * 60 })
        assertEquals(t, t.sorted())
        assertEquals(t, ReminderPlanner.timesForDate(s, date))
        // je ein Termin pro Drittel des Fensters
        assertTrue(t[0] <= 13 * 60 && t[1] in 13 * 60..17 * 60 && t[2] >= 17 * 60)
    }

    @Test fun randomPerDayIsClamped() {
        val s = ReminderSettings(enabled = true, perDay = 99)
        assertEquals(ReminderPlanner.MAX_PER_DAY, ReminderPlanner.timesForDate(s, LocalDate.of(2026, 10, 26)).size)
    }

    @Test fun randomNextIsAlwaysInFutureAndInWindow() {
        val s = ReminderSettings(enabled = true, perDay = 2, windowStartMin = 10 * 60, windowEndMin = 12 * 60)
        var now = at(2026, 10, 26, 0, 0)
        repeat(20) {
            val next = ReminderPlanner.nextTrigger(s, now)!!
            assertTrue(next.isAfter(now))
            val min = next.hour * 60 + next.minute
            assertTrue(min in 10 * 60..12 * 60)
            now = next
        }
    }

    @Test fun inactiveDayGivesNoTimes() {
        val s = fixed.copy(days = setOf(DayOfWeek.MONDAY))
        assertTrue(ReminderPlanner.timesForDate(s, LocalDate.of(2026, 10, 27)).isEmpty())
    }
}
