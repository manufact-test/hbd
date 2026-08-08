package com.bdaysquirrel.app

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bdaysquirrel.app.data.BirthdayBackupService
import com.bdaysquirrel.app.reminders.BirthdayNotifications
import com.bdaysquirrel.app.reminders.ReminderSettings
import com.bdaysquirrel.app.reminders.ReminderSettingsRepository
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.launch

private val SettingsNightPlum = Color(0xFF171226)
private val SettingsDeepIndigo = Color(0xFF241A3A)
private val SettingsCardViolet = Color(0xFF2D2144)
private val SettingsRaisedViolet = Color(0xFF392A55)
private val SettingsCoral = Color(0xFFFF7A3D)
private val SettingsMint = Color(0xFF6FE7C8)
private val SettingsCream = Color(0xFFFFF4D8)
private val SettingsSoftCream = Color(0xFFE8DEC7)
private val SettingsMuted = Color(0xFFB8B1CA)
private val SettingsDarkText = Color(0xFF21162B)

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as BdaySquirrelApplication

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = SettingsCoral,
                    secondary = SettingsMint,
                    background = SettingsNightPlum,
                    surface = SettingsCardViolet,
                    onBackground = SettingsCream,
                    onSurface = SettingsCream,
                ),
            ) {
                SettingsScreen(
                    settingsRepository = app.reminderSettingsRepository,
                    backupService = app.backupService,
                    onBack = ::finish,
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    settingsRepository: ReminderSettingsRepository,
    backupService: BirthdayBackupService,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val settings by settingsRepository.settings.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var notificationsEnabled by remember {
        mutableStateOf(BirthdayNotifications.canPost(context))
    }
    var backupBusy by remember { mutableStateOf(false) }
    var backupMessage by remember { mutableStateOf<String?>(null) }
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        notificationsEnabled = BirthdayNotifications.canPost(context)
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip"),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                backupBusy = true
                backupMessage = null
                backupMessage = runCatching {
                    val result = backupService.exportTo(uri)
                    "Сохранено: ${result.birthdays} дат, ${result.photos} фото."
                }.getOrElse { error ->
                    "Не удалось создать копию: ${error.message ?: "неизвестная ошибка"}"
                }
                backupBusy = false
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            pendingImportUri = uri
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationsEnabled = BirthdayNotifications.canPost(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    pendingImportUri?.let { uri ->
        AlertDialog(
            onDismissRequest = { pendingImportUri = null },
            containerColor = SettingsDeepIndigo,
            title = { Text("Восстановить резервную копию?") },
            text = {
                Text(
                    "Текущий список дней рождения будет заменён данными из выбранного файла.",
                    color = SettingsSoftCream,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingImportUri = null
                        scope.launch {
                            backupBusy = true
                            backupMessage = null
                            backupMessage = runCatching {
                                val result = backupService.importFrom(uri)
                                "Восстановлено: ${result.birthdays} дат, ${result.photos} фото."
                            }.getOrElse { error ->
                                "Не удалось восстановить копию: ${error.message ?: "неизвестная ошибка"}"
                            }
                            backupBusy = false
                        }
                    },
                ) {
                    Text("Восстановить", color = SettingsCoral)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingImportUri = null }) {
                    Text("Отмена", color = SettingsMuted)
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SettingsNightPlum)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = SettingsMint,
                )
            }
            Text(
                text = "Настройки",
                color = SettingsCream,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SettingsSectionLabel("УВЕДОМЛЕНИЯ")
            SettingsPanel {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = if (notificationsEnabled) SettingsMint else SettingsMuted,
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (notificationsEnabled) {
                                    "Уведомления разрешены"
                                } else {
                                    "Нужно разрешение на уведомления"
                                },
                                color = SettingsCream,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "Без разрешения BdaySquirrel не сможет напомнить о дате.",
                                color = SettingsMuted,
                                fontSize = 13.sp,
                            )
                        }
                    }

                    if (!notificationsEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                requestNotificationAccess(
                                    context = context,
                                    permissionLauncher = { permission ->
                                        permissionLauncher.launch(permission)
                                    },
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SettingsCoral,
                                contentColor = SettingsDarkText,
                            ),
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                        ) {
                            Text("Разрешить уведомления")
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = SettingsRaisedViolet)
                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Время напоминаний",
                        color = SettingsCream,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute -> settingsRepository.setTime(hour, minute) },
                                settings.hour,
                                settings.minute,
                                true,
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(formatTime(settings))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Когда предупреждать",
                        color = SettingsCream,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ReminderToggle(
                        title = "В день рождения",
                        checked = settings.sameDay,
                        onCheckedChange = { settingsRepository.setOffsetEnabled(0, it) },
                    )
                    ReminderToggle(
                        title = "За 1 день",
                        checked = settings.oneDayBefore,
                        onCheckedChange = { settingsRepository.setOffsetEnabled(1, it) },
                    )
                    ReminderToggle(
                        title = "За 3 дня",
                        checked = settings.threeDaysBefore,
                        onCheckedChange = { settingsRepository.setOffsetEnabled(3, it) },
                    )
                    ReminderToggle(
                        title = "За 7 дней",
                        checked = settings.sevenDaysBefore,
                        onCheckedChange = { settingsRepository.setOffsetEnabled(7, it) },
                    )

                    if (settings.enabledOffsets().isEmpty()) {
                        Text(
                            text = "Все напоминания выключены.",
                            color = SettingsCoral,
                            fontSize = 13.sp,
                        )
                    }
                }
            }

            SettingsSectionLabel("РЕЗЕРВНАЯ КОПИЯ")
            SettingsPanel {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Данные остаются твоими",
                        color = SettingsCream,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Экспорт сохраняет даты, заметки и доступные фотографии в один локальный ZIP-файл. Импорт позволяет перенести данные на другое устройство.",
                        color = SettingsMuted,
                        fontSize = 13.sp,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            exportLauncher.launch(
                                "BdaySquirrel-backup-${LocalDate.now()}.zip",
                            )
                        },
                        enabled = !backupBusy,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SettingsCoral,
                            contentColor = SettingsDarkText,
                        ),
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Создать резервную копию")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            importLauncher.launch(
                                arrayOf("application/zip", "application/octet-stream"),
                            )
                        },
                        enabled = !backupBusy,
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Восстановить из копии")
                    }

                    if (backupBusy) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Работаю с резервной копией…", color = SettingsMint)
                    }
                    backupMessage?.let { message ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(message, color = SettingsSoftCream, fontSize = 13.sp)
                    }
                }
            }

            Text(
                text = "BdaySquirrel хранит основной список дней рождения локально на устройстве.",
                color = SettingsMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}

@Composable
private fun ReminderToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
        )
        Text(
            text = title,
            color = SettingsSoftCream,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun SettingsPanel(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
        color = SettingsCardViolet,
        border = BorderStroke(2.dp, SettingsRaisedViolet),
        content = content,
    )
}

@Composable
private fun SettingsSectionLabel(text: String) {
    Text(
        text = text,
        color = SettingsMint,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(top = 4.dp),
    )
}

private fun formatTime(settings: ReminderSettings): String = String.format(
    Locale.getDefault(),
    "%02d:%02d",
    settings.hour,
    settings.minute,
)

private fun requestNotificationAccess(
    context: Context,
    permissionLauncher: (String) -> Unit,
) {
    if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        val activity = context as? Activity
        if (
            activity != null &&
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        ) {
            permissionLauncher(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            openNotificationSettings(context)
        }
        return
    }

    if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
        openNotificationSettings(context)
    }
}

private fun openNotificationSettings(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        },
    )
}
