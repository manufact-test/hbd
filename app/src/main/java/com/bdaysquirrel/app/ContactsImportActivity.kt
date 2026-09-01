package com.bdaysquirrel.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.bdaysquirrel.app.data.BirthdayEntity
import com.bdaysquirrel.app.data.ContactBirthdayCandidate
import com.bdaysquirrel.app.data.ContactsBirthdayReader
import java.time.MonthDay
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private val ImportNightPlum = Color(0xFF171226)
private val ImportDeepIndigo = Color(0xFF241A3A)
private val ImportCardViolet = Color(0xFF2D2144)
private val ImportRaisedViolet = Color(0xFF392A55)
private val ImportCoral = Color(0xFFFF7A3D)
private val ImportMint = Color(0xFF6FE7C8)
private val ImportCream = Color(0xFFFFF4D8)
private val ImportSoftCream = Color(0xFFE8DEC7)
private val ImportMuted = Color(0xFFB8B1CA)
private val ImportDarkText = Color(0xFF21162B)

class ContactsImportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as BdaySquirrelApplication

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = ImportCoral,
                    secondary = ImportMint,
                    background = ImportNightPlum,
                    surface = ImportCardViolet,
                    onBackground = ImportCream,
                    onSurface = ImportCream,
                ),
            ) {
                ContactsImportScreen(
                    reader = ContactsBirthdayReader(this@ContactsImportActivity),
                    repositorySnapshot = app.birthdayRepository::snapshot,
                    addBirthday = { candidate ->
                        app.birthdayRepository.add(
                            name = candidate.name,
                            day = candidate.day,
                            month = candidate.month,
                            year = candidate.year,
                            note = "",
                            photoUri = null,
                        )
                    },
                    onBack = ::finish,
                    onImported = { count ->
                        Toast.makeText(
                            this@ContactsImportActivity,
                            "Импортировано: $count",
                            Toast.LENGTH_SHORT,
                        ).show()
                        setResult(
                            Activity.RESULT_OK,
                            Intent().putExtra(EXTRA_IMPORTED_COUNT, count),
                        )
                        finish()
                    },
                    openAppSettings = {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:$packageName"),
                            ),
                        )
                    },
                )
            }
        }
    }

    companion object {
        const val EXTRA_IMPORTED_COUNT = "imported_count"
    }
}

@Composable
private fun ContactsImportScreen(
    reader: ContactsBirthdayReader,
    repositorySnapshot: suspend () -> List<BirthdayEntity>,
    addBirthday: suspend (ContactBirthdayCandidate) -> Unit,
    onBack: () -> Unit,
    onImported: (Int) -> Unit,
    openAppSettings: () -> Unit,
) {
    val activity = androidx.compose.ui.platform.LocalContext.current as Activity
    val scope = rememberCoroutineScope()

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_CONTACTS,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var importing by remember { mutableStateOf(false) }
    var hasLoaded by remember { mutableStateOf(false) }
    var reloadToken by remember { mutableIntStateOf(0) }
    var candidates by remember { mutableStateOf<List<ContactBirthdayCandidate>>(emptyList()) }
    var existingBirthdays by remember { mutableStateOf<List<BirthdayEntity>>(emptyList()) }
    var selectedKeys by remember { mutableStateOf<Set<String>>(emptySet()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        permissionGranted = granted
        permissionDenied = !granted
        if (granted) reloadToken += 1
    }

    LaunchedEffect(permissionGranted, reloadToken) {
        if (!permissionGranted) return@LaunchedEffect
        loading = true
        errorMessage = null
        runCatching {
            val contactsTask = scope.async { reader.readBirthdays() }
            val existingTask = scope.async { repositorySnapshot() }
            contactsTask.await() to existingTask.await()
        }.onSuccess { (loadedCandidates, existing) ->
            candidates = loadedCandidates
            existingBirthdays = existing
            selectedKeys = loadedCandidates
                .filterNot { candidate -> isDuplicate(candidate, existing) }
                .mapTo(mutableSetOf()) { it.stableKey }
            hasLoaded = true
        }.onFailure { error ->
            errorMessage = error.message ?: "Не удалось прочитать контакты."
            hasLoaded = true
        }
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ImportNightPlum)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = ImportMint,
                )
            }
            Text(
                text = "Импорт из контактов",
                color = ImportCream,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        when {
            !permissionGranted -> PermissionContent(
                permissionDenied = permissionDenied,
                onRequestPermission = {
                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                },
                onOpenSettings = openAppSettings,
            )

            loading -> LoadingContent()

            errorMessage != null -> ErrorContent(
                message = errorMessage.orEmpty(),
                onRetry = { reloadToken += 1 },
            )

            hasLoaded && candidates.isEmpty() -> EmptyContactsContent(
                onRetry = { reloadToken += 1 },
            )

            else -> PreviewContent(
                candidates = candidates,
                existingBirthdays = existingBirthdays,
                selectedKeys = selectedKeys,
                importing = importing,
                onSelectionChange = { key, selected ->
                    selectedKeys = if (selected) {
                        selectedKeys + key
                    } else {
                        selectedKeys - key
                    }
                },
                onSelectAll = {
                    selectedKeys = candidates.mapTo(mutableSetOf()) { it.stableKey }
                },
                onClearAll = { selectedKeys = emptySet() },
                onImport = {
                    val selected = candidates.filter { it.stableKey in selectedKeys }
                    if (selected.isEmpty() || importing) return@PreviewContent
                    scope.launch {
                        importing = true
                        errorMessage = null
                        runCatching {
                            selected.forEach { candidate -> addBirthday(candidate) }
                        }.onSuccess {
                            onImported(selected.size)
                        }.onFailure { error ->
                            errorMessage = error.message ?: "Не удалось импортировать выбранные даты."
                            importing = false
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun PermissionContent(
    permissionDenied: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Contacts,
            contentDescription = null,
            tint = ImportCoral,
            modifier = Modifier.padding(bottom = 18.dp),
        )
        Text(
            text = "Найдём дни рождения автоматически",
            color = ImportCream,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "BdaySquirrel прочитает только сохранённые в контактах даты рождения. Контакты никуда не загружаются и остаются на устройстве.",
            color = ImportMuted,
            fontSize = 15.sp,
            lineHeight = 21.sp,
        )
        Spacer(modifier = Modifier.height(22.dp))
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = ImportCoral,
                contentColor = ImportDarkText,
            ),
            shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
        ) {
            Text(if (permissionDenied) "Попробовать снова" else "Разрешить доступ к контактам")
        }
        if (permissionDenied) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onOpenSettings) {
                Text("Открыть настройки приложения", color = ImportMint)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = ImportMint,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Local-first: данные не покидают телефон", color = ImportSoftCream, fontSize = 13.sp)
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Ищу дни рождения…", color = ImportMint, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Обычно это занимает мгновение.", color = ImportMuted)
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Не получилось прочитать контакты", color = ImportCream, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(message, color = ImportMuted)
        Spacer(modifier = Modifier.height(18.dp))
        Button(onClick = onRetry) { Text("Повторить") }
    }
}

@Composable
private fun EmptyContactsContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("В контактах нет дней рождения", color = ImportCream, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Можно добавить даты вручную в BdaySquirrel или сохранить дни рождения в системных контактах и повторить поиск.",
            color = ImportMuted,
        )
        Spacer(modifier = Modifier.height(18.dp))
        OutlinedButton(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
            Text("Проверить ещё раз")
        }
    }
}

@Composable
private fun PreviewContent(
    candidates: List<ContactBirthdayCandidate>,
    existingBirthdays: List<BirthdayEntity>,
    selectedKeys: Set<String>,
    importing: Boolean,
    onSelectionChange: (String, Boolean) -> Unit,
    onSelectAll: () -> Unit,
    onClearAll: () -> Unit,
    onImport: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = "Нашли ${candidates.size} ${contactBirthdayCountWord(candidates.size)}",
            color = ImportCream,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Проверь список перед импортом. Возможные дубли отмечены и по умолчанию не выбраны.",
            color = ImportMuted,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onSelectAll) {
                Text("Выбрать все", color = ImportMint)
            }
            TextButton(onClick = onClearAll) {
                Text("Снять выбор", color = ImportMuted)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = candidates,
                key = { it.stableKey },
            ) { candidate ->
                val duplicate = isDuplicate(candidate, existingBirthdays)
                ContactBirthdayRow(
                    candidate = candidate,
                    checked = candidate.stableKey in selectedKeys,
                    duplicate = duplicate,
                    onCheckedChange = { checked ->
                        onSelectionChange(candidate.stableKey, checked)
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onImport,
            enabled = selectedKeys.isNotEmpty() && !importing,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ImportCoral,
                contentColor = ImportDarkText,
            ),
            shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
        ) {
            Text(
                if (importing) {
                    "Импортирую…"
                } else {
                    "Импортировать (${selectedKeys.size})"
                },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ContactBirthdayRow(
    candidate: ContactBirthdayCandidate,
    checked: Boolean,
    duplicate: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
        color = ImportCardViolet,
        border = BorderStroke(
            1.dp,
            if (duplicate) ImportCoral else ImportRaisedViolet,
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = candidate.name,
                    color = ImportCream,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = contactBirthdayLabel(candidate),
                    color = ImportSoftCream,
                    fontSize = 13.sp,
                )
            }
            if (duplicate) {
                Text(
                    text = "УЖЕ ЕСТЬ",
                    color = ImportCoral,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun isDuplicate(
    candidate: ContactBirthdayCandidate,
    existingBirthdays: List<BirthdayEntity>,
): Boolean {
    val normalizedName = candidate.name.trim().lowercase(Locale.getDefault())
    return existingBirthdays.any { existing ->
        existing.day == candidate.day &&
            existing.month == candidate.month &&
            existing.name.trim().lowercase(Locale.getDefault()) == normalizedName
    }
}

private fun contactBirthdayLabel(candidate: ContactBirthdayCandidate): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
    val date = formatter.format(MonthDay.of(candidate.month, candidate.day))
    return candidate.year?.let { "$date $it" } ?: "$date • год не указан"
}

private fun contactBirthdayCountWord(count: Int): String {
    val lastTwo = count % 100
    val last = count % 10
    return when {
        lastTwo in 11..14 -> "дат"
        last == 1 -> "дату"
        last in 2..4 -> "даты"
        else -> "дат"
    }
}
