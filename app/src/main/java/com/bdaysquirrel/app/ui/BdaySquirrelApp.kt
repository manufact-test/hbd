package com.bdaysquirrel.app.ui

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
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bdaysquirrel.app.data.BirthdayEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val NightPlum = Color(0xFF171226)
private val DeepIndigo = Color(0xFF241A3A)
private val CardViolet = Color(0xFF2D2144)
private val RaisedViolet = Color(0xFF392A55)
private val DarkOutline = Color(0xFF100B1C)
private val SquirrelCoral = Color(0xFFFF7A3D)
private val Peach = Color(0xFFFFB36B)
private val Mint = Color(0xFF6FE7C8)
private val Lavender = Color(0xFFA98BFF)
private val Cream = Color(0xFFFFF4D8)
private val SoftCream = Color(0xFFE8DEC7)
private val MutedText = Color(0xFFB8B1CA)
private val Danger = Color(0xFFFF5A6E)
private val DarkText = Color(0xFF21162B)

private val BdayColorScheme = darkColorScheme(
    primary = SquirrelCoral,
    onPrimary = DarkText,
    secondary = Mint,
    onSecondary = DarkText,
    tertiary = Lavender,
    background = NightPlum,
    onBackground = Cream,
    surface = CardViolet,
    onSurface = Cream,
    surfaceVariant = RaisedViolet,
    onSurfaceVariant = SoftCream,
    error = Danger,
    onError = DarkText,
    outline = RaisedViolet,
)

private val BdayShapes = Shapes(
    small = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
    medium = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
    large = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
)

private val BdayTypography = Typography(
    headlineMedium = TextStyle(
        fontSize = 27.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Bold,
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 25.sp,
        fontWeight = FontWeight.Bold,
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 23.sp,
        fontWeight = FontWeight.Normal,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Bold,
    ),
)

@Composable
fun BdaySquirrelRoot(viewModel: BirthdayViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MaterialTheme(
        colorScheme = BdayColorScheme,
        typography = BdayTypography,
        shapes = BdayShapes,
    ) {
        BirthdayScreen(
            state = state,
            onAddBirthday = viewModel::addBirthday,
            onDeleteBirthday = viewModel::deleteBirthday,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthdayScreen(
    state: BirthdayUiState,
    onAddBirthday: (String, Int, Int, Int?, String) -> Unit,
    onDeleteBirthday: (BirthdayEntity) -> Unit,
) {
    var showAddSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = NightPlum,
        topBar = { AppHeader() },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddSheet = true },
                shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                containerColor = SquirrelCoral,
                contentColor = DarkText,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                    )
                },
                text = { Text("Добавить") },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 8.dp,
                end = 20.dp,
                bottom = 108.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                HeroPanel(totalBirthdays = state.birthdays.size)
            }

            item {
                SectionHeader()
            }

            if (state.birthdays.isEmpty()) {
                item {
                    EmptyBirthdays(onAddClick = { showAddSheet = true })
                }
            } else {
                items(
                    items = state.birthdays,
                    key = { it.birthday.id },
                ) { birthday ->
                    BirthdayCard(
                        model = birthday,
                        onDelete = { onDeleteBirthday(birthday.birthday) },
                    )
                }
            }
        }
    }

    if (showAddSheet) {
        AddBirthdaySheet(
            onDismiss = { showAddSheet = false },
            onAdd = { name, day, month, year, note ->
                onAddBirthday(name, day, month, year, note)
                showAddSheet = false
            },
        )
    }
}

@Composable
private fun AppHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NightPlum)
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Text(
            text = "BDAY SQUIRREL",
            style = MaterialTheme.typography.labelLarge,
            color = SquirrelCoral,
            letterSpacing = 1.6.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Ближайшие дни рождения",
            style = MaterialTheme.typography.headlineMedium,
            color = Cream,
        )
    }
}

@Composable
private fun HeroPanel(totalBirthdays: Int) {
    PixelPanel(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                    .background(SquirrelCoral),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "S",
                    color = DarkText,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (totalBirthdays == 0) {
                        "Белочка готова собирать важные даты"
                    } else {
                        "В памяти уже ${birthdayCountLabel(totalBirthdays)}"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = Cream,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Все данные хранятся только на этом устройстве.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "ПО КАЛЕНДАРЮ",
            style = MaterialTheme.typography.labelLarge,
            color = Mint,
            letterSpacing = 1.2.sp,
        )
        Spacer(modifier = Modifier.width(12.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = RaisedViolet,
        )
    }
}

@Composable
private fun EmptyBirthdays(onAddClick: () -> Unit) {
    PixelPanel(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Пока здесь тихо",
                style = MaterialTheme.typography.titleLarge,
                color = Cream,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Добавь первый день рождения — он сразу появится в списке ближайших событий.",
                style = MaterialTheme.typography.bodyLarge,
                color = MutedText,
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onAddClick,
                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquirrelCoral,
                    contentColor = DarkText,
                ),
                border = BorderStroke(2.dp, DarkOutline),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Добавить дату")
            }
        }
    }
}

@Composable
private fun BirthdayCard(
    model: BirthdayUiModel,
    onDelete: () -> Unit,
) {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
    val shortMonthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())

    PixelPanel(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .width(64.dp)
                    .clip(CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
                    .background(DeepIndigo)
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = model.nextDate.dayOfMonth.toString(),
                    color = Peach,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = shortMonthFormatter.format(model.nextDate).uppercase(Locale.getDefault()),
                    color = MutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = model.birthday.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Cream,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildString {
                        append(dateFormatter.format(model.nextDate))
                        model.ageOnBirthday?.let { age ->
                            append(" • исполнится ")
                            append(age)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftCream,
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = countdownLabel(model.daysUntil),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (model.daysUntil == 0L) SquirrelCoral else Mint,
                )
                if (model.birthday.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = model.birthday.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MutedText,
                )
            }
        }
    }
}

@Composable
private fun PixelPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)

    Box(
        modifier = modifier.padding(end = 4.dp, bottom = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .clip(shape)
                .background(DarkOutline),
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            color = CardViolet,
            contentColor = Cream,
            border = BorderStroke(2.dp, RaisedViolet),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBirthdaySheet(
    onDismiss: () -> Unit,
    onAdd: (String, Int, Int, Int?, String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var day by rememberSaveable { mutableStateOf("") }
    var month by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var attemptedSubmit by rememberSaveable { mutableStateOf(false) }

    val dayValue = day.toIntOrNull()
    val monthValue = month.toIntOrNull()
    val yearValue = year.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
    val validDate = runCatching {
        LocalDate.of(
            2000,
            requireNotNull(monthValue),
            requireNotNull(dayValue),
        )
    }.isSuccess
    val validYearText = year.isBlank() || yearValue != null
    val validYear = yearValue == null || yearValue in 1900..LocalDate.now().year
    val isValid = name.isNotBlank() && validDate && validYearText && validYear

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DeepIndigo,
        contentColor = Cream,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = "Новый день рождения",
                style = MaterialTheme.typography.titleLarge,
                color = Cream,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Год можно не указывать — напоминание всё равно будет работать.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Имя") },
                singleLine = true,
                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = day,
                    onValueChange = { day = it.filter(Char::isDigit).take(2) },
                    modifier = Modifier.weight(1f),
                    label = { Text("День") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                )
                OutlinedTextField(
                    value = month,
                    onValueChange = { month = it.filter(Char::isDigit).take(2) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Месяц") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it.filter(Char::isDigit).take(4) },
                    modifier = Modifier.weight(1.25f),
                    label = { Text("Год") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Заметка — необязательно") },
                minLines = 2,
                maxLines = 4,
                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
            )

            if (attemptedSubmit && !isValid) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Проверь имя и дату. Год должен быть от 1900 до текущего.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Danger,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    attemptedSubmit = true
                    if (isValid) {
                        onAdd(
                            name.trim(),
                            requireNotNull(dayValue),
                            requireNotNull(monthValue),
                            yearValue,
                            note.trim(),
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquirrelCoral,
                    contentColor = DarkText,
                ),
                border = BorderStroke(2.dp, DarkOutline),
            ) {
                Text("Сохранить день рождения")
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = "Отмена",
                    color = MutedText,
                )
            }
        }
    }
}

private fun countdownLabel(days: Long): String = when (days) {
    0L -> "Сегодня день рождения"
    1L -> "Завтра"
    else -> "Через $days ${dayWord(days)}"
}

private fun dayWord(days: Long): String {
    val lastTwo = days % 100
    val last = days % 10
    return when {
        lastTwo in 11L..14L -> "дней"
        last == 1L -> "день"
        last in 2L..4L -> "дня"
        else -> "дней"
    }
}

private fun birthdayCountLabel(count: Int): String {
    val lastTwo = count % 100
    val last = count % 10
    val noun = when {
        lastTwo in 11..14 -> "дней рождения"
        last == 1 -> "день рождения"
        last in 2..4 -> "дня рождения"
        else -> "дней рождения"
    }
    return "$count $noun"
}
