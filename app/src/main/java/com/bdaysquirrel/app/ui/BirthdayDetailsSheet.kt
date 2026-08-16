package com.bdaysquirrel.app.ui

import android.net.Uri
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DetailDeepIndigo = Color(0xFF241A3A)
private val DetailCardViolet = Color(0xFF2D2144)
private val DetailRaisedViolet = Color(0xFF392A55)
private val DetailCoral = Color(0xFFFF7A3D)
private val DetailMint = Color(0xFF6FE7C8)
private val DetailLavender = Color(0xFFA98BFF)
private val DetailCream = Color(0xFFFFF4D8)
private val DetailSoftCream = Color(0xFFE8DEC7)
private val DetailMuted = Color(0xFFB8B1CA)
private val DetailDarkText = Color(0xFF21162B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BirthdayDetailsSheet(
    model: BirthdayUiModel,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
) {
    val birthday = model.birthday
    val locale = Locale.getDefault()
    val zodiac = zodiacInfo(birthday.day, birthday.month)
    val datePattern = if (birthday.year == null) "d MMMM" else "d MMMM yyyy"
    val displayDate = runCatching {
        val year = birthday.year ?: 2000
        DateTimeFormatter.ofPattern(datePattern, locale).format(
            java.time.YearMonth.of(year, birthday.month)
                .atDay(birthday.day.coerceAtMost(java.time.YearMonth.of(year, birthday.month).lengthOfMonth())),
        )
    }.getOrElse { "${birthday.day}.${birthday.month}" }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DetailDeepIndigo,
        contentColor = DetailCream,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 26.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                        .background(DetailCardViolet),
                    contentAlignment = Alignment.Center,
                ) {
                    if (birthday.photoUri != null) {
                        AsyncImage(
                            model = Uri.parse(birthday.photoUri),
                            contentDescription = "Фото ${birthday.name}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = DetailMuted,
                            modifier = Modifier.size(40.dp),
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = birthday.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = DetailCream,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = CutCornerShape(topStart = 7.dp, bottomEnd = 7.dp),
                        color = DetailLavender.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, DetailLavender.copy(alpha = 0.7f)),
                    ) {
                        Text(
                            text = "${zodiac.symbol} ${zodiac.name}",
                            color = DetailSoftCream,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            DetailInfoPanel {
                DetailRow("Дата рождения", displayDate)
                DetailDivider()
                DetailRow("Год рождения", birthday.year?.toString() ?: "Не указан")
                DetailDivider()
                DetailRow("Знак зодиака", "${zodiac.symbol} ${zodiac.name}")
                model.ageOnBirthday?.let { age ->
                    DetailDivider()
                    DetailRow("В ближайший день рождения", "$age ${yearsWord(age)}")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            DetailInfoPanel {
                Text(
                    text = "БЛИЖАЙШИЙ ДЕНЬ РОЖДЕНИЯ",
                    color = DetailMint,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = DateTimeFormatter.ofPattern("d MMMM", locale).format(model.nextDate),
                    color = DetailCream,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = detailCountdownLabel(model.daysUntil),
                    color = if (model.daysUntil == 0L) DetailCoral else DetailMint,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (birthday.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                DetailInfoPanel {
                    Text(
                        text = "ЗАМЕТКА",
                        color = DetailMint,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = birthday.note,
                        color = DetailSoftCream,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = CutCornerShape(topStart = 9.dp, bottomEnd = 9.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DetailCoral,
                    contentColor = DetailDarkText,
                ),
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Редактировать", fontWeight = FontWeight.Bold)
            }
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text("Закрыть", color = DetailMuted)
            }
        }
    }
}

@Composable
private fun DetailInfoPanel(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CutCornerShape(topStart = 11.dp, bottomEnd = 11.dp),
        color = DetailCardViolet,
        border = BorderStroke(1.dp, DetailRaisedViolet),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            color = DetailMuted,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = DetailCream,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DetailDivider() {
    Spacer(modifier = Modifier.height(12.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DetailRaisedViolet),
    )
    Spacer(modifier = Modifier.height(12.dp))
}

private fun detailCountdownLabel(days: Long): String = when (days) {
    0L -> "Сегодня"
    1L -> "Завтра"
    else -> "Через $days ${detailDayWord(days)}"
}

private fun detailDayWord(days: Long): String {
    val lastTwo = days % 100
    val last = days % 10
    return when {
        lastTwo in 11L..14L -> "дней"
        last == 1L -> "день"
        last in 2L..4L -> "дня"
        else -> "дней"
    }
}

private fun yearsWord(age: Int): String {
    val lastTwo = age % 100
    val last = age % 10
    return when {
        lastTwo in 11..14 -> "лет"
        last == 1 -> "год"
        last in 2..4 -> "года"
        else -> "лет"
    }
}
