package com.bdaysquirrel.app.reminders

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREFS_NAME = "bdaysquirrel_reminder_settings"
private const val KEY_HOUR = "hour"
private const val KEY_MINUTE = "minute"
private const val KEY_SAME_DAY = "same_day"
private const val KEY_ONE_DAY = "one_day"
private const val KEY_THREE_DAYS = "three_days"
private const val KEY_SEVEN_DAYS = "seven_days"

data class ReminderSettings(
    val hour: Int = 9,
    val minute: Int = 0,
    val sameDay: Boolean = true,
    val oneDayBefore: Boolean = true,
    val threeDaysBefore: Boolean = true,
    val sevenDaysBefore: Boolean = true,
) {
    fun enabledOffsets(): List<Int> = buildList {
        if (sameDay) add(0)
        if (oneDayBefore) add(1)
        if (threeDaysBefore) add(3)
        if (sevenDaysBefore) add(7)
    }
}

class ReminderSettingsRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE,
    )

    private val _settings = MutableStateFlow(readSettings())
    val settings: StateFlow<ReminderSettings> = _settings.asStateFlow()

    fun setTime(hour: Int, minute: Int) {
        require(hour in 0..23)
        require(minute in 0..59)
        update(
            _settings.value.copy(
                hour = hour,
                minute = minute,
            ),
        )
    }

    fun setOffsetEnabled(offsetDays: Int, enabled: Boolean) {
        val current = _settings.value
        val updated = when (offsetDays) {
            0 -> current.copy(sameDay = enabled)
            1 -> current.copy(oneDayBefore = enabled)
            3 -> current.copy(threeDaysBefore = enabled)
            7 -> current.copy(sevenDaysBefore = enabled)
            else -> error("Unsupported reminder offset: $offsetDays")
        }
        update(updated)
    }

    private fun update(settings: ReminderSettings) {
        preferences.edit()
            .putInt(KEY_HOUR, settings.hour)
            .putInt(KEY_MINUTE, settings.minute)
            .putBoolean(KEY_SAME_DAY, settings.sameDay)
            .putBoolean(KEY_ONE_DAY, settings.oneDayBefore)
            .putBoolean(KEY_THREE_DAYS, settings.threeDaysBefore)
            .putBoolean(KEY_SEVEN_DAYS, settings.sevenDaysBefore)
            .apply()
        _settings.value = settings
    }

    private fun readSettings(): ReminderSettings = ReminderSettings(
        hour = preferences.getInt(KEY_HOUR, 9).coerceIn(0, 23),
        minute = preferences.getInt(KEY_MINUTE, 0).coerceIn(0, 59),
        sameDay = preferences.getBoolean(KEY_SAME_DAY, true),
        oneDayBefore = preferences.getBoolean(KEY_ONE_DAY, true),
        threeDaysBefore = preferences.getBoolean(KEY_THREE_DAYS, true),
        sevenDaysBefore = preferences.getBoolean(KEY_SEVEN_DAYS, true),
    )
}
