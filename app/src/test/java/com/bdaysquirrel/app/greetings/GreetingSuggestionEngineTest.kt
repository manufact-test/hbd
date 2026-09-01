package com.bdaysquirrel.app.greetings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GreetingSuggestionEngineTest {
    private val baseContext = GreetingContext(
        birthdayId = 42L,
        name = "Максим",
        zodiacName = "Лев",
        ageOnBirthday = 32,
        celebrationYear = 2026,
    )

    @Test
    fun same_input_produces_same_suggestion() {
        val first = GreetingSuggestionEngine.generate(
            context = baseContext,
            tone = GreetingTone.WARM,
            variantIndex = 2,
        )
        val second = GreetingSuggestionEngine.generate(
            context = baseContext,
            tone = GreetingTone.WARM,
            variantIndex = 2,
        )

        assertEquals(first, second)
    }

    @Test
    fun next_variant_changes_text() {
        val first = GreetingSuggestionEngine.generate(
            context = baseContext,
            tone = GreetingTone.SHORT,
            variantIndex = 0,
        )
        val second = GreetingSuggestionEngine.generate(
            context = baseContext,
            tone = GreetingTone.SHORT,
            variantIndex = 1,
        )

        assertNotEquals(first.text, second.text)
    }

    @Test
    fun first_five_variants_are_unique_for_every_tone() {
        GreetingTone.entries.forEach { tone ->
            val texts = (0 until 5).map { variant ->
                GreetingSuggestionEngine.generate(
                    context = baseContext,
                    tone = tone,
                    variantIndex = variant,
                ).text
            }

            assertEquals(5, texts.toSet().size)
        }
    }

    @Test
    fun greeting_uses_person_name_and_known_age_when_age_variant_is_selected() {
        val suggestion = GreetingSuggestionEngine.generate(
            context = baseContext,
            tone = GreetingTone.WARM,
            variantIndex = 0,
        )

        assertTrue(suggestion.text.contains("Максим"))
        assertTrue(suggestion.text.contains("32"))
    }

    @Test
    fun yearless_birthday_never_invents_age() {
        val context = baseContext.copy(ageOnBirthday = null)

        val texts = GreetingTone.entries.flatMap { tone ->
            (0 until 6).map { variant ->
                GreetingSuggestionEngine.generate(
                    context = context,
                    tone = tone,
                    variantIndex = variant,
                ).text
            }
        }

        assertTrue(texts.all { it.contains("Максим") })
        assertTrue(texts.none { it.contains("null") })
        assertTrue(texts.none { it.contains("32") })
    }

    @Test
    fun all_supported_zodiac_signs_have_personalized_output() {
        val signs = listOf(
            "Овен",
            "Телец",
            "Близнецы",
            "Рак",
            "Лев",
            "Дева",
            "Весы",
            "Скорпион",
            "Стрелец",
            "Козерог",
            "Водолей",
            "Рыбы",
        )

        val texts = signs.mapIndexed { index, sign ->
            GreetingSuggestionEngine.generate(
                context = baseContext.copy(
                    birthdayId = index.toLong() + 1,
                    zodiacName = sign,
                    ageOnBirthday = null,
                ),
                tone = GreetingTone.WARM,
                variantIndex = 1,
            ).text
        }

        assertEquals(signs.size, texts.size)
        assertEquals(signs.size, texts.toSet().size)
        assertFalse(texts.any(String::isBlank))
    }

    @Test(expected = IllegalArgumentException::class)
    fun negative_variant_is_rejected() {
        GreetingSuggestionEngine.generate(
            context = baseContext,
            tone = GreetingTone.FUN,
            variantIndex = -1,
        )
    }
}
