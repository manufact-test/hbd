package com.bdaysquirrel.app.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class BirthdayProfileTest {
    @Test
    fun zodiac_boundaries_are_correct() {
        assertEquals("Козерог", zodiacInfo(day = 19, month = 1).name)
        assertEquals("Водолей", zodiacInfo(day = 20, month = 1).name)
        assertEquals("Рыбы", zodiacInfo(day = 20, month = 3).name)
        assertEquals("Овен", zodiacInfo(day = 21, month = 3).name)
        assertEquals("Скорпион", zodiacInfo(day = 21, month = 11).name)
        assertEquals("Стрелец", zodiacInfo(day = 22, month = 11).name)
        assertEquals("Стрелец", zodiacInfo(day = 21, month = 12).name)
        assertEquals("Козерог", zodiacInfo(day = 22, month = 12).name)
    }

    @Test
    fun yearless_february_keeps_leap_day_available() {
        assertEquals(29, birthdayMonthLength(year = null, month = 2))
        assertEquals(28, birthdayMonthLength(year = 2025, month = 2))
        assertEquals(29, birthdayMonthLength(year = 2024, month = 2))
    }
}
