package com.bdaysquirrel.app.ui

import com.bdaysquirrel.app.data.BirthdayEntity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BirthdayBrowseTest {
    private val today = LocalDate.of(2026, 8, 21)

    @Test
    fun search_matches_name_and_note_case_insensitively_with_all_tokens() {
        val birthday = birthday(
            id = 1,
            name = "Анна Петрова",
            month = 10,
            day = 4,
            note = "Любит ретро игры",
        )

        assertTrue(birthdayMatches(birthday, normalizeSearchQuery("  АННА   игры ")))
        assertTrue(birthdayMatches(birthday, normalizeSearchQuery("петрова")))
        assertTrue(birthdayMatches(birthday, normalizeSearchQuery("ретро")))
        assertEquals(false, birthdayMatches(birthday, normalizeSearchQuery("Анна музыка")))
    }

    @Test
    fun search_keeps_complete_collection_and_month_counts_unchanged() {
        val state = buildBirthdayUiState(
            birthdays = listOf(
                birthday(1, "Анна", 10, 4, "Настольные игры"),
                birthday(2, "Борис", 10, 20),
                birthday(3, "Вера", 2, 7),
            ),
            query = "игры",
            browseMode = BirthdayBrowseMode.UPCOMING,
            selectedMonth = null,
            today = today,
        )

        assertEquals(3, state.birthdays.size)
        assertEquals(listOf("Анна"), state.matchedBirthdays.map { it.birthday.name })
        assertEquals(
            listOf(BirthdayMonthSummary(2, 1), BirthdayMonthSummary(10, 2)),
            state.monthSummaries,
        )
    }

    @Test
    fun monthly_sections_are_calendar_ordered_and_cards_are_day_ordered() {
        val state = buildBirthdayUiState(
            birthdays = listOf(
                birthday(1, "Яна", 12, 20),
                birthday(2, "Борис", 2, 17),
                birthday(3, "Анна", 2, 3),
            ),
            query = "",
            browseMode = BirthdayBrowseMode.MONTHS,
            selectedMonth = null,
            today = today,
        )

        assertEquals(listOf(2, 12), state.monthSections.map { it.month })
        assertEquals(
            listOf("Анна", "Борис"),
            state.monthSections.first().birthdays.map { it.birthday.name },
        )
    }

    @Test
    fun nearest_view_wraps_past_birthdays_to_next_year() {
        val state = buildBirthdayUiState(
            birthdays = listOf(
                birthday(1, "Прошедший", 8, 20),
                birthday(2, "Скоро", 8, 22),
            ),
            query = "",
            browseMode = BirthdayBrowseMode.UPCOMING,
            selectedMonth = null,
            today = today,
        )

        assertEquals(listOf("Скоро", "Прошедший"), state.birthdays.map { it.birthday.name })
        assertEquals(LocalDate.of(2027, 8, 20), state.birthdays.last().nextDate)
    }

    @Test
    fun relevant_month_uses_current_or_next_and_wraps_to_start() {
        val summaries = listOf(
            BirthdayMonthSummary(month = 2, count = 3),
            BirthdayMonthSummary(month = 9, count = 1),
        )

        assertEquals(9, nextRelevantMonth(summaries, currentMonth = 8))
        assertEquals(2, nextRelevantMonth(summaries, currentMonth = 11))
        assertEquals(null, nextRelevantMonth(emptyList(), currentMonth = 8))
    }

    private fun birthday(
        id: Long,
        name: String,
        month: Int,
        day: Int,
        note: String = "",
    ) = BirthdayEntity(
        id = id,
        name = name,
        month = month,
        day = day,
        note = note,
    )
}
