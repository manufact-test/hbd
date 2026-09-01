package com.bdaysquirrel.app

import android.app.Application
import com.bdaysquirrel.app.data.BdayDatabase
import com.bdaysquirrel.app.data.BirthdayBackupService
import com.bdaysquirrel.app.data.BirthdayRepository
import com.bdaysquirrel.app.reminders.BirthdayNotifications
import com.bdaysquirrel.app.reminders.BirthdayReminderScheduler
import com.bdaysquirrel.app.reminders.ReminderSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class BdaySquirrelApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database: BdayDatabase by lazy {
        BdayDatabase.create(this)
    }

    val birthdayRepository: BirthdayRepository by lazy {
        BirthdayRepository(database.birthdayDao())
    }

    val reminderSettingsRepository: ReminderSettingsRepository by lazy {
        ReminderSettingsRepository(this)
    }

    val reminderScheduler: BirthdayReminderScheduler by lazy {
        BirthdayReminderScheduler(this)
    }

    val backupService: BirthdayBackupService by lazy {
        BirthdayBackupService(this, birthdayRepository)
    }

    override fun onCreate() {
        super.onCreate()
        BirthdayNotifications.ensureChannel(this)

        applicationScope.launch {
            combine(
                birthdayRepository.birthdays,
                reminderSettingsRepository.settings,
            ) { birthdays, settings -> birthdays to settings }
                .distinctUntilChanged()
                .collect { (birthdays, settings) ->
                    reminderScheduler.sync(birthdays, settings)
                }
        }
    }

    suspend fun syncRemindersOnce() {
        reminderScheduler.sync(
            birthdays = birthdayRepository.snapshot(),
            settings = reminderSettingsRepository.settings.value,
        )
    }
}
