package com.bdaysquirrel.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContactsBirthdayReaderTest {
    @Test
    fun parsesBirthdayWithYear() {
        assertEquals(
            ParsedContactBirthday(day = 16, month = 8, year = 1995),
            parseContactBirthdayDate("1995-08-16"),
        )
    }

    @Test
    fun parsesBirthdayWithoutYear() {
        assertEquals(
            ParsedContactBirthday(day = 29, month = 2, year = null),
            parseContactBirthdayDate("--02-29"),
        )
    }

    @Test
    fun parsesCompactBirthday() {
        assertEquals(
            ParsedContactBirthday(day = 3, month = 11, year = 2001),
            parseContactBirthdayDate("20011103"),
        )
    }

    @Test
    fun rejectsInvalidDate() {
        assertNull(parseContactBirthdayDate("--02-31"))
    }
}
