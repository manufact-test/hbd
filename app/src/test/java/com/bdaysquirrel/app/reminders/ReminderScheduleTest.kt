package com.bdaysquirrel.app.reminders

import com.bdaysquirrel.app.data.BirthdayEntity
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderScheduleTest {
    private val zone = ZoneId.of("Europe/Warsaw")

    @Test
    fun sameDayReminderUsesTodayWhenTimeIsStillAhead() {
        val birthday = birthday(month = 8, day = 8)
        val now = ZonedDateTime.of(2026, 8, 8, 8, 30, 0, 0, zone)

        val trigger = nextReminderTrigger(
            birthday = birthday,
            offsetDays = 0,
            hour = 9,
            minute = 0,
            now = now,
        )

        assertEquals(
            ZonedDateTime.of(2026, 8, 8, 9, 0, 0, 0, zone),
            trigger,
        )
    }

    @Test
    fun sameDayReminderMovesToNextYearWhenTimeAlreadyPassed() {
        val birthday = birthday(month = 8, day = 8)
        val now = ZonedDateTime.of(2026, 8, 8, 9, 1, 0, 0, zone)

        val trigger = nextReminderTrigger(
            birthday = birthday,
            offsetDays = 0,
            hour = 9,
            minute = 0,
            now = now,
        )

        assertEquals(
            ZonedDateTime.of(2027, 8, 8, 9, 0, 0, 0, zone),
            trigger,
        )
    }

    @Test
    fun sevenDayReminderCanCrossCalendarYear() {
        val birthday = birthday(month = 1, day = 2)
        val now = ZonedDateTime.of(2026, 12, 20, 12, 0, 0, 0, zone)

        val trigger = nextReminderTrigger(
            birthday = birthday,
            offsetDays = 7,
            hour = 18,
            minute = 45,
            now = now,
        )

        assertEquals(
            ZonedDateTime.of(2026, 12, 26, 18, 45, 0, 0, zone),
            trigger,
        )
    }

    @Test
    fun leapDayFallsBackToFebruaryTwentyEightInNonLeapYear() {
        val birthday = birthday(month = 2, day = 29)
        val now = ZonedDateTime.of(2027, 2, 1, 12, 0, 0, 0, zone)

        val trigger = nextReminderTrigger(
            birthday = birthday,
            offsetDays = 0,
            hour = 9,
            minute = 0,
            now = now,
        )

        assertEquals(
            ZonedDateTime.of(2027, 2, 28, 9, 0, 0, 0, zone),
            trigger,
        )
    }

    private fun birthday(month: Int, day: Int): BirthdayEntity = BirthdayEntity(
        id = 42,
        name = "Test",
        day = day,
        month = month,
        year = 1990,
    )
}
