package com.bdaysquirrel.app.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bdaysquirrel.app.data.BirthdayEntity
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

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
            onUpdateBirthday = viewModel::updateBirthday,
            onDeleteBirthday = viewModel::deleteBirthday,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthdayScreen(
    state: BirthdayUiState,
    onAddBirthday: (String, Int, Int, Int?, String, String?) -> Unit,
    onUpdateBirthday: (BirthdayEntity, String, Int, Int, Int?, String, String?) -> Unit,
    onDeleteBirthday: (BirthdayEntity) -> Unit,
) {
    var showAddSheet by rememberSaveable { mutableStateOf(false) }
    var editingBirthday by remember { mutableStateOf<BirthdayEntity?>(null) }
    var hasBirthdaySnapshot by rememberSaveable { mutableStateOf(false) }
    var knownBirthdayIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var newlyAddedBirthdayId by remember { mutableStateOf<Long?>(null) }

    val currentBirthdayIds = state.birthdays.map { it.birthday.id }

    LaunchedEffect(state.isLoaded, currentBirthdayIds) {
        if (!state.isLoaded) return@LaunchedEffect

        val currentSet = currentBirthdayIds.toSet()
        if (!hasBirthdaySnapshot) {
            knownBirthdayIds = currentSet
            hasBirthdaySnapshot = true
        } else {
            val addedIds = currentSet - knownBirthdayIds
            if (addedIds.isNotEmpty()) {
                newlyAddedBirthdayId = state.birthdays
                    .firstOrNull { it.birthday.id in addedIds }
                    ?.birthday
                    ?.id
            }
            knownBirthdayIds = currentSet
        }
    }

    LaunchedEffect(newlyAddedBirthdayId) {
        if (newlyAddedBirthdayId != null) {
            delay(1_300)
            newlyAddedBirthdayId = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
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
                        burstOnEnter = birthday.birthday.id == newlyAddedBirthdayId,
                        onEdit = { editingBirthday = birthday.birthday },
                        onDelete = { onDeleteBirthday(birthday.birthday) },
                    )
                }
            }
        }
    }

    if (showAddSheet) {
        BirthdayEditorSheet(
            birthday = null,
            onDismiss = { showAddSheet = false },
            onSave = { name, day, month, year, note, photoUri ->
                onAddBirthday(name, day, month, year, note, photoUri)
                showAddSheet = false
            },
        )
    }

    editingBirthday?.let { birthday ->
        BirthdayEditorSheet(
            birthday = birthday,
            onDismiss = { editingBirthday = null },
            onSave = { name, day, month, year, note, photoUri ->
                onUpdateBirthday(birthday, name, day, month, year, note, photoUri)
                editingBirthday = null
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
    val visuals = rememberHeroPanelVisuals()

    PixelPanel(
        modifier = Modifier.fillMaxWidth(),
        backgroundBrush = visuals.backgroundBrush,
        borderColor = visuals.borderColor,
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                    .background(DeepIndigo)
                    .padding(5.dp),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = "file:///android_asset/bdaysquirrel-squirrel-icon.svg",
                    contentDescription = "BdaySquirrel",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (totalBirthdays == 0) {
                        "Белочка готова собирать важные даты"
                    } else {
                        "Сохранено ${birthdayCountLabel(totalBirthdays)}"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = Cream,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Поздравляй вовремя.",
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
    burstOnEnter: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
    val shortMonthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())
    val isBirthdayToday = model.daysUntil == 0L
    val visuals = rememberBirthdayCardVisuals(
        birthdayId = model.birthday.id,
        isToday = isBirthdayToday,
        burstOnEnter = burstOnEnter,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = visuals.scale
                scaleY = visuals.scale
            },
    ) {
        PixelPanel(
            modifier = Modifier.fillMaxWidth(),
            backgroundBrush = visuals.backgroundBrush,
            borderColor = visuals.borderColor,
            shadowColor = visuals.shadowColor,
        ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (model.birthday.photoUri != null) {
                AsyncImage(
                    model = Uri.parse(model.birthday.photoUri),
                    contentDescription = "Фото ${model.birthday.name}",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
                        .background(DeepIndigo),
                    contentScale = ContentScale.Crop,
                )
            } else {
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = Mint,
                    )
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

        PixelBurstOverlay(
            active = burstOnEnter,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun PixelPanel(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CardViolet,
    backgroundBrush: Brush? = null,
    borderColor: Color = RaisedViolet,
    shadowColor: Color = DarkOutline,
    content: @Composable () -> Unit,
) {
    val shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)

    Box(
        modifier = modifier.padding(end = 4.dp, bottom = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 4.dp, y = 4.dp)
                .clip(shape)
                .background(shadowColor),
        )

        val panelModifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (backgroundBrush != null) {
                    Modifier.background(backgroundBrush)
                } else {
                    Modifier.background(backgroundColor)
                },
            )
            .border(BorderStroke(2.dp, borderColor), shape)

        Box(
            modifier = panelModifier,
            content = { content() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthdayEditorSheet(
    birthday: BirthdayEntity?,
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int?, String, String?) -> Unit,
) {
    val context = LocalContext.current
    val locale = Locale.getDefault()
    val today = LocalDate.now()
    val initialDate = remember(birthday?.id) { editorInitialDate(birthday) }

    var name by rememberSaveable(birthday?.id) { mutableStateOf(birthday?.name.orEmpty()) }
    var selectedDateEpochDay by rememberSaveable(birthday?.id) {
        mutableStateOf(initialDate?.toEpochDay())
    }
    var yearUnknown by rememberSaveable(birthday?.id) {
        mutableStateOf(birthday != null && birthday.year == null)
    }
    var note by rememberSaveable(birthday?.id) { mutableStateOf(birthday?.note.orEmpty()) }
    var photoUri by rememberSaveable(birthday?.id) { mutableStateOf(birthday?.photoUri) }
    var attemptedSubmit by rememberSaveable(birthday?.id) { mutableStateOf(false) }
    var showYearlessPicker by rememberSaveable(birthday?.id) { mutableStateOf(false) }

    val selectedDate = selectedDateEpochDay?.let(LocalDate::ofEpochDay)
    val isValid = name.isNotBlank() && selectedDate != null

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            photoUri = uri.toString()
        }
    }

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = if (birthday == null) "Новый день рождения" else "Редактировать карточку",
                style = MaterialTheme.typography.titleLarge,
                color = Cream,
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp))
                        .background(CardViolet),
                    contentAlignment = Alignment.Center,
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model = Uri.parse(photoUri),
                            contentDescription = "Выбранное фото",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (photoUri == null) "Добавить фото" else "Сменить фото")
                    }
                    if (photoUri != null) {
                        TextButton(
                            onClick = { photoUri = null },
                            modifier = Modifier.align(Alignment.End),
                        ) {
                            Text("Убрать фото", color = MutedText)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Имя") },
                singleLine = true,
                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    if (yearUnknown) {
                        showYearlessPicker = true
                    } else {
                        val pickerDate = selectedDate ?: today.minusYears(30)
                        DatePickerDialog(
                            context,
                            { _, year, monthZeroBased, dayOfMonth ->
                                selectedDateEpochDay = LocalDate.of(
                                    year,
                                    monthZeroBased + 1,
                                    dayOfMonth,
                                ).toEpochDay()
                            },
                            pickerDate.year,
                            pickerDate.monthValue - 1,
                            pickerDate.dayOfMonth,
                        ).apply {
                            datePicker.maxDate = today
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()
                        }.show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = selectedDate?.let {
                        val pattern = if (yearUnknown) "d MMMM" else "d MMMM yyyy"
                        DateTimeFormatter.ofPattern(pattern, locale).format(it)
                    } ?: if (yearUnknown) {
                        "Выбрать день и месяц"
                    } else {
                        "Выбрать дату рождения"
                    },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = yearUnknown,
                    onCheckedChange = { yearUnknown = it },
                )
                Text(
                    text = "Не указывать год рождения",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftCream,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    text = "Укажи имя и выбери дату рождения.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Danger,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    attemptedSubmit = true
                    val date = selectedDate
                    if (isValid && date != null) {
                        onSave(
                            name.trim(),
                            date.dayOfMonth,
                            date.monthValue,
                            if (yearUnknown) null else date.year,
                            note.trim(),
                            photoUri,
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
                Text(if (birthday == null) "Сохранить день рождения" else "Сохранить изменения")
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

    if (showYearlessPicker) {
        val pickerDate = selectedDate ?: today
        YearlessBirthdayPickerDialog(
            initialMonth = pickerDate.monthValue,
            initialDay = pickerDate.dayOfMonth,
            locale = locale,
            onDismiss = { showYearlessPicker = false },
            onConfirm = { month, day ->
                val anchorYear = selectedDate?.year ?: today.minusYears(30).year
                val anchorMonth = YearMonth.of(anchorYear, month)
                val storageYear = if (day <= anchorMonth.lengthOfMonth()) anchorYear else 2000
                selectedDateEpochDay = LocalDate.of(storageYear, month, day).toEpochDay()
                showYearlessPicker = false
            },
        )
    }
}

private fun editorInitialDate(birthday: BirthdayEntity?): LocalDate? {
    birthday ?: return null
    val year = birthday.year ?: 2000
    val yearMonth = YearMonth.of(year, birthday.month)
    return yearMonth.atDay(birthday.day.coerceAtMost(yearMonth.lengthOfMonth()))
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
