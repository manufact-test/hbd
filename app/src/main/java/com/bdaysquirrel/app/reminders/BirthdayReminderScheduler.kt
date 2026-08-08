package com.bdaysquirrel.app.reminders

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bdaysquirrel.app.BdaySquirrelApplication
import com.bdaysquirrel.app.MainActivity
import com.bdaysquirrel.app.R
import com.bdaysquirrel.app.data.BirthdayEntity
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val SCHEDULE_PREFS = "bdaysquirrel_scheduled_reminders"
private const val KEY_PENDING_ALARMS = "pending_alarm_uris"
private const val REMINDER_ACTION = "com.bdaysquirrel.app.action.BIRTHDAY_REMINDER"
private const val EXTRA_NAME = "name"
private const val EXTRA_NOTE = "note"
private const val EXTRA_OFFSET_DAYS = "offset_days"

class BirthdayReminderScheduler(context: Context) {
    private val appContext = context.applicationContext
    private val alarmManager = appContext.getSystemService(AlarmManager::class.java)
    private val scheduledPreferences = appContext.getSharedPreferences(
        SCHEDULE_PREFS,
        Context.MODE_PRIVATE,
    )

    @Synchronized
    fun sync(
        birthdays: List<BirthdayEntity>,
        settings: ReminderSettings,
        now: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()),
    ) {
        cancelAllKnownAlarms()

        val pendingAlarmUris = mutableSetOf<String>()
        birthdays.forEach { birthday ->
            settings.enabledOffsets().forEach { offsetDays ->
                val triggerAt = nextReminderTrigger(
                    birthday = birthday,
                    offsetDays = offsetDays,
                    hour = settings.hour,
                    minute = settings.minute,
                    now = now,
                )

                val alarmUri = reminderUri(birthday.id, offsetDays)
                val pendingIntent = createReminderPendingIntent(
                    alarmUri = alarmUri,
                    birthday = birthday,
                    offsetDays = offsetDays,
                )

                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt.toInstant().toEpochMilli(),
                    pendingIntent,
                )
                pendingAlarmUris += alarmUri
            }
        }

        scheduledPreferences.edit()
            .putStringSet(KEY_PENDING_ALARMS, pendingAlarmUris)
            .apply()
    }

    private fun cancelAllKnownAlarms() {
        val alarmUris = scheduledPreferences
            .getStringSet(KEY_PENDING_ALARMS, emptySet())
            .orEmpty()
            .toSet()

        alarmUris.forEach { alarmUri ->
            findReminderPendingIntent(alarmUri)?.let { pendingIntent ->
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        scheduledPreferences.edit()
            .remove(KEY_PENDING_ALARMS)
            .apply()
    }

    private fun createReminderPendingIntent(
        alarmUri: String,
        birthday: BirthdayEntity,
        offsetDays: Int,
    ): PendingIntent {
        val intent = baseReminderIntent(alarmUri).apply {
            putExtra(EXTRA_NAME, birthday.name)
            putExtra(EXTRA_NOTE, birthday.note)
            putExtra(EXTRA_OFFSET_DAYS, offsetDays)
        }
        return PendingIntent.getBroadcast(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun findReminderPendingIntent(alarmUri: String): PendingIntent? =
        PendingIntent.getBroadcast(
            appContext,
            0,
            baseReminderIntent(alarmUri),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun baseReminderIntent(alarmUri: String): Intent =
        Intent(appContext, BirthdayReminderReceiver::class.java).apply {
            action = REMINDER_ACTION
            data = Uri.parse(alarmUri)
        }
}

internal fun nextReminderTrigger(
    birthday: BirthdayEntity,
    offsetDays: Int,
    hour: Int,
    minute: Int,
    now: ZonedDateTime,
): ZonedDateTime {
    require(offsetDays >= 0)
    val reminderTime = LocalTime.of(hour, minute)

    for (year in now.year..(now.year + 2)) {
        val birthdayDate = safeBirthdayDate(year, birthday.month, birthday.day)
        val reminderDate = birthdayDate.minusDays(offsetDays.toLong())
        val triggerAt = ZonedDateTime.of(reminderDate, reminderTime, now.zone)
        if (triggerAt.isAfter(now)) {
            return triggerAt
        }
    }

    error("Unable to calculate the next reminder for birthday ${birthday.id}")
}

private fun safeBirthdayDate(year: Int, month: Int, day: Int): LocalDate {
    val yearMonth = YearMonth.of(year, month)
    return yearMonth.atDay(day.coerceAtMost(yearMonth.lengthOfMonth()))
}

private fun reminderUri(birthdayId: Long, offsetDays: Int): String =
    "bdaysquirrel://reminder/$birthdayId/$offsetDays"

object BirthdayNotifications {
    const val CHANNEL_ID = "birthday_reminders"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "Напоминания о днях рождения",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Напоминания BdaySquirrel о ближайших днях рождения"
            },
        )
    }

    fun canPost(context: Context): Boolean {
        val runtimePermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        return runtimePermissionGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}

class BirthdayReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != REMINDER_ACTION) return

        BirthdayNotifications.ensureChannel(context)
        val name = intent.getStringExtra(EXTRA_NAME).orEmpty().ifBlank { "близкого человека" }
        val note = intent.getStringExtra(EXTRA_NOTE).orEmpty()
        val offsetDays = intent.getIntExtra(EXTRA_OFFSET_DAYS, 0)

        if (BirthdayNotifications.canPost(context)) {
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val contentIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            val message = when (offsetDays) {
                0 -> "Сегодня день рождения у $name 🎉"
                1 -> "Завтра день рождения у $name"
                3 -> "Через 3 дня день рождения у $name"
                7 -> "Через 7 дней день рождения у $name"
                else -> "Скоро день рождения у $name"
            }

            val notification = NotificationCompat.Builder(context, BirthdayNotifications.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("BdaySquirrel")
                .setContentText(message)
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        if (note.isBlank()) message else "$message\n$note",
                    ),
                )
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            val notificationId = intent.dataString.orEmpty().hashCode()
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }

        resyncAfterBroadcast(context, goAsync())
    }
}

class ReminderRescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            -> resyncAfterBroadcast(context, goAsync())
        }
    }
}

private fun resyncAfterBroadcast(
    context: Context,
    pendingResult: BroadcastReceiver.PendingResult,
) {
    val app = context.applicationContext as? BdaySquirrelApplication
    if (app == null) {
        pendingResult.finish()
        return
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            app.syncRemindersOnce()
        } finally {
            pendingResult.finish()
        }
    }
}
