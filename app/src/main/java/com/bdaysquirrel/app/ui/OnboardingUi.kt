package com.bdaysquirrel.app.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bdaysquirrel.app.data.BirthdayBackupService
import kotlinx.coroutines.launch

private val OnboardingNightPlum = Color(0xFF171226)
private val OnboardingDeepIndigo = Color(0xFF241A3A)
private val OnboardingCardViolet = Color(0xFF2D2144)
private val OnboardingRaisedViolet = Color(0xFF392A55)
private val OnboardingCoral = Color(0xFFFF7A3D)
private val OnboardingMint = Color(0xFF6FE7C8)
private val OnboardingCream = Color(0xFFFFF4D8)
private val OnboardingSoftCream = Color(0xFFE8DEC7)
private val OnboardingMuted = Color(0xFFB8B1CA)
private val OnboardingDarkText = Color(0xFF21162B)

private enum class OnboardingStep {
    WELCOME,
    START,
}

@Composable
fun FirstRunOnboarding(
    backupService: BirthdayBackupService,
    onImportContacts: () -> Unit,
    onAddManually: () -> Unit,
    onRestoreComplete: () -> Unit,
    onSkip: () -> Unit,
) {
    var step by remember { mutableStateOf(OnboardingStep.WELCOME) }
    var backupBusy by remember { mutableStateOf(false) }
    var backupError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                backupBusy = true
                backupError = null
                runCatching {
                    backupService.importFrom(uri)
                }.onSuccess {
                    onRestoreComplete()
                }.onFailure { error ->
                    backupError = error.message ?: "Не удалось восстановить резервную копию."
                }
                backupBusy = false
            }
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = OnboardingCoral,
            secondary = OnboardingMint,
            background = OnboardingNightPlum,
            surface = OnboardingCardViolet,
            onBackground = OnboardingCream,
            onSurface = OnboardingCream,
        ),
    ) {
        when (step) {
            OnboardingStep.WELCOME -> WelcomeStep(
                onContinue = { step = OnboardingStep.START },
                onSkip = onSkip,
            )

            OnboardingStep.START -> StartStep(
                backupBusy = backupBusy,
                backupError = backupError,
                onImportContacts = onImportContacts,
                onAddManually = onAddManually,
                onRestore = {
                    restoreLauncher.launch(
                        arrayOf("application/zip", "application/octet-stream"),
                    )
                },
                onBack = { step = OnboardingStep.WELCOME },
            )
        }
    }
}

@Composable
private fun WelcomeStep(
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "onboardingSquirrel")
    val scale by transition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_700),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "squirrelScale",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "BDAY SQUIRREL",
            color = OnboardingCoral,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.8.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(154.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CutCornerShape(topStart = 22.dp, bottomEnd = 22.dp))
                .background(OnboardingDeepIndigo)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = "file:///android_asset/bdaysquirrel-squirrel-icon.svg",
                contentDescription = "BdaySquirrel",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Ни одного важного дня мимо",
            color = OnboardingCream,
            fontSize = 30.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Black,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Добавь дни рождения один раз — BdaySquirrel сам напомнит, когда пора поздравлять.",
            color = OnboardingSoftCream,
            fontSize = 16.sp,
            lineHeight = 23.sp,
        )
        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
            color = OnboardingCardViolet,
            border = BorderStroke(1.dp, OnboardingRaisedViolet),
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = OnboardingMint)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Local-first: список дней рождения хранится на твоём устройстве.",
                    color = OnboardingMuted,
                    fontSize = 13.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OnboardingCoral,
                contentColor = OnboardingDarkText,
            ),
            shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
        ) {
            Text("Начать", fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onSkip) {
            Text("Пропустить", color = OnboardingMuted)
        }
    }
}

@Composable
private fun StartStep(
    backupBusy: Boolean,
    backupError: String?,
    onImportContacts: () -> Unit,
    onAddManually: () -> Unit,
    onRestore: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 22.dp),
    ) {
        TextButton(onClick = onBack) {
            Text("← Назад", color = OnboardingMint)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Как начнём?",
            color = OnboardingCream,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Самый быстрый способ — забрать уже сохранённые даты из контактов. Перед импортом ты увидишь и проверишь весь список.",
            color = OnboardingMuted,
            fontSize = 15.sp,
            lineHeight = 21.sp,
        )
        Spacer(modifier = Modifier.height(24.dp))

        StartActionCard(
            title = "Импортировать из контактов",
            description = "Найдём контакты с датой рождения, покажем превью и отметим возможные дубли.",
            icon = { Icon(Icons.Default.Contacts, contentDescription = null, tint = OnboardingCoral) },
            primary = true,
            enabled = !backupBusy,
            onClick = onImportContacts,
        )
        Spacer(modifier = Modifier.height(12.dp))
        StartActionCard(
            title = "Добавить вручную",
            description = "Сразу откроем создание первой карточки.",
            icon = { Icon(Icons.Default.Add, contentDescription = null, tint = OnboardingMint) },
            enabled = !backupBusy,
            onClick = onAddManually,
        )
        Spacer(modifier = Modifier.height(12.dp))
        StartActionCard(
            title = if (backupBusy) "Восстанавливаю…" else "Восстановить резервную копию",
            description = "Для тех, кто уже пользовался BdaySquirrel на другом устройстве.",
            icon = { Icon(Icons.Default.FileUpload, contentDescription = null, tint = OnboardingMint) },
            enabled = !backupBusy,
            onClick = onRestore,
        )

        backupError?.let { message ->
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = message,
                color = OnboardingCoral,
                fontSize = 13.sp,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Доступ к контактам запрашивается только если ты выберешь импорт. Без него BdaySquirrel полностью работает с ручным вводом.",
            color = OnboardingMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun StartActionCard(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    primary: Boolean = false,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (primary) OnboardingCoral else OnboardingRaisedViolet
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
        color = OnboardingCardViolet,
        border = BorderStroke(2.dp, borderColor),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = OnboardingCream,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = OnboardingMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
            Spacer(modifier = Modifier.height(14.dp))
            if (primary) {
                Button(
                    onClick = onClick,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OnboardingCoral,
                        contentColor = OnboardingDarkText,
                    ),
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                ) {
                    Text("Продолжить")
                }
            } else {
                OutlinedButton(
                    onClick = onClick,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth(),
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                ) {
                    Text("Выбрать")
                }
            }
        }
    }
}
