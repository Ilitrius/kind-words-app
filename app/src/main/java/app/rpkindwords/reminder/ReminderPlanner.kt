package app.rpkindwords.reminder

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random

enum class ReminderMode { RANDOM, FIXED }

data class ReminderSettings(
    val enabled: Boolean = false,
    val mode: ReminderMode = ReminderMode.RANDOM,
    val perDay: Int = 1,
    val windowStartMin: Int = 9 * 60,
    val windowEndMin: Int = 20 * 60,
    val fixedTimesMin: List<Int> = listOf(18 * 60),
    val days: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
)

/** Reine Zeitplanberechnung (ohne Android), deterministisch pro Datum -> gut testbar. */
object ReminderPlanner {
    const val MIN_PER_DAY = 1
    const val MAX_PER_DAY = 3
    private const val MIN_WINDOW = 30

    /** Auslösezeiten (Minuten seit Mitternacht, aufsteigend) für ein Datum; leer, wenn der Tag nicht aktiv ist. */
    fun timesForDate(s: ReminderSettings, date: LocalDate): List<Int> {
        if (date.dayOfWeek !in s.days) return emptyList()
        return when (s.mode) {
            ReminderMode.FIXED -> s.fixedTimesMin.map { it.coerceIn(0, 1439) }.distinct().sorted()
            ReminderMode.RANDOM -> randomTimes(s, date)
        }
    }

    private fun randomTimes(s: ReminderSettings, date: LocalDate): List<Int> {
        val start = s.windowStartMin.coerceIn(0, 1439)
        val end = maxOf(s.windowEndMin.coerceIn(0, 1439), start + MIN_WINDOW).coerceAtMost(1439)
        val n = s.perDay.coerceIn(MIN_PER_DAY, MAX_PER_DAY)
        val span = end - start
        val seg = span / n
        val rnd = Random(date.toEpochDay())
        return List(n) { i ->
            val lo = start + i * seg
            val hi = if (i == n - 1) end else lo + seg
            if (hi > lo) rnd.nextInt(lo, hi + 1).coerceAtMost(end) else lo
        }.sorted()
    }

    /** Nächster Auslösezeitpunkt strikt nach [now] (Suche bis 8 Tage voraus) oder null. */
    fun nextTrigger(s: ReminderSettings, now: ZonedDateTime, zone: ZoneId = now.zone): ZonedDateTime? {
        if (!s.enabled || s.days.isEmpty()) return null
        if (s.mode == ReminderMode.FIXED && s.fixedTimesMin.isEmpty()) return null
        val today = now.toLocalDate()
        for (offset in 0L..8L) {
            val date = today.plusDays(offset)
            for (m in timesForDate(s, date)) {
                val candidate = ZonedDateTime.of(date, LocalTime.of(m / 60, m % 60), zone)
                if (candidate.isAfter(now)) return candidate
            }
        }
        return null
    }
}
