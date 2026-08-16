from pathlib import Path


def replace_once(path: str, old: str, new: str, label: str) -> None:
    file_path = Path(path)
    text = file_path.read_text(encoding="utf-8")
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match in {path}, got {count}")
    file_path.write_text(text.replace(old, new, 1), encoding="utf-8")


ui = "app/src/main/java/com/bdaysquirrel/app/ui/BdaySquirrelApp.kt"
main = "app/src/main/java/com/bdaysquirrel/app/MainActivity.kt"
effects = "app/src/main/java/com/bdaysquirrel/app/ui/BirthdayVisualEffects.kt"

replace_once(
    ui,
    "import androidx.compose.foundation.background\n",
    "import androidx.compose.foundation.background\nimport androidx.compose.foundation.border\n",
    "add border import",
)
replace_once(
    ui,
    "import androidx.compose.runtime.Composable\nimport androidx.compose.runtime.getValue\n",
    "import androidx.compose.runtime.Composable\nimport androidx.compose.runtime.LaunchedEffect\nimport androidx.compose.runtime.getValue\n",
    "add LaunchedEffect import",
)
replace_once(
    ui,
    "import androidx.compose.ui.graphics.Color\n",
    "import androidx.compose.ui.graphics.Brush\nimport androidx.compose.ui.graphics.Color\nimport androidx.compose.ui.graphics.graphicsLayer\n",
    "add graphics imports",
)
replace_once(
    ui,
    "import java.util.Locale\n",
    "import java.util.Locale\nimport kotlinx.coroutines.delay\n",
    "add delay import",
)

replace_once(
    ui,
    '''    var showAddSheet by rememberSaveable { mutableStateOf(false) }
    var editingBirthday by remember { mutableStateOf<BirthdayEntity?>(null) }

    Scaffold(''',
    '''    var showAddSheet by rememberSaveable { mutableStateOf(false) }
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

    Scaffold(''',
    "track new birthday cards",
)

replace_once(
    ui,
    "        containerColor = NightPlum,\n",
    "        containerColor = Color.Transparent,\n",
    "make main scaffold transparent for animated backdrop",
)

replace_once(
    ui,
    '''                    BirthdayCard(
                        model = birthday,
                        onEdit = { editingBirthday = birthday.birthday },''',
    '''                    BirthdayCard(
                        model = birthday,
                        burstOnEnter = birthday.birthday.id == newlyAddedBirthdayId,
                        onEdit = { editingBirthday = birthday.birthday },''',
    "pass card entrance effect flag",
)

replace_once(
    ui,
    '''@Composable
private fun HeroPanel(totalBirthdays: Int) {
    PixelPanel(modifier = Modifier.fillMaxWidth()) {''',
    '''@Composable
private fun HeroPanel(totalBirthdays: Int) {
    val visuals = rememberHeroPanelVisuals()

    PixelPanel(
        modifier = Modifier.fillMaxWidth(),
        backgroundBrush = visuals.backgroundBrush,
        borderColor = visuals.borderColor,
    ) {''',
    "animate hero panel",
)

replace_once(
    ui,
    '''private fun BirthdayCard(
    model: BirthdayUiModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
    val shortMonthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())

    PixelPanel(modifier = Modifier.fillMaxWidth()) {''',
    '''private fun BirthdayCard(
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
        ) {''',
    "wrap birthday card with animated visuals",
)

replace_once(
    ui,
    '''                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MutedText,
                    )
                }
            }
        }
    }
}

@Composable
private fun PixelPanel(''',
    '''                IconButton(onClick = onDelete) {
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
private fun PixelPanel(''',
    "add pixel burst overlay",
)

replace_once(
    ui,
    '''private fun PixelPanel(
    modifier: Modifier = Modifier,
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
}''',
    '''private fun PixelPanel(
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
}''',
    "support gradients in pixel panel",
)

replace_once(
    ui,
    '''    var photoUri by rememberSaveable(birthday?.id) { mutableStateOf(birthday?.photoUri) }
    var attemptedSubmit by rememberSaveable(birthday?.id) { mutableStateOf(false) }

    val selectedDate = selectedDateEpochDay?.let(LocalDate::ofEpochDay)''',
    '''    var photoUri by rememberSaveable(birthday?.id) { mutableStateOf(birthday?.photoUri) }
    var attemptedSubmit by rememberSaveable(birthday?.id) { mutableStateOf(false) }
    var showYearlessPicker by rememberSaveable(birthday?.id) { mutableStateOf(false) }

    val selectedDate = selectedDateEpochDay?.let(LocalDate::ofEpochDay)''',
    "add yearless picker state",
)

replace_once(
    ui,
    '''                onClick = {
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
                },''',
    '''                onClick = {
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
                },''',
    "switch to yearless date picker",
)

replace_once(
    ui,
    '''                    text = selectedDate?.let {
                        val pattern = if (yearUnknown) "d MMMM" else "d MMMM yyyy"
                        DateTimeFormatter.ofPattern(pattern, locale).format(it)
                    } ?: "Выбрать дату рождения",
                )''',
    '''                    text = selectedDate?.let {
                        val pattern = if (yearUnknown) "d MMMM" else "d MMMM yyyy"
                        DateTimeFormatter.ofPattern(pattern, locale).format(it)
                    } ?: if (yearUnknown) {
                        "Выбрать день и месяц"
                    } else {
                        "Выбрать дату рождения"
                    },
                )''',
    "clarify yearless picker label",
)

replace_once(
    ui,
    '''            TextButton(
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

private fun editorInitialDate''',
    '''            TextButton(
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

private fun editorInitialDate''',
    "render yearless picker dialog",
)

replace_once(
    main,
    "import androidx.compose.foundation.layout.fillMaxSize\n",
    "import androidx.compose.foundation.layout.fillMaxSize\nimport androidx.compose.foundation.layout.matchParentSize\n",
    "add backdrop sizing import",
)
replace_once(
    main,
    "import com.bdaysquirrel.app.ui.BdaySquirrelRoot\n",
    "import com.bdaysquirrel.app.ui.AnimatedAppBackdrop\nimport com.bdaysquirrel.app.ui.BdaySquirrelRoot\n",
    "import animated backdrop",
)
replace_once(
    main,
    '''            ) {
                BdaySquirrelRoot(viewModel = viewModel)

                IconButton(''',
    '''            ) {
                AnimatedAppBackdrop(modifier = Modifier.matchParentSize())
                BdaySquirrelRoot(viewModel = viewModel)

                IconButton(''',
    "draw animated backdrop",
)

replace_once(
    effects,
    "import androidx.compose.material3.BorderStroke\n",
    "import androidx.compose.foundation.BorderStroke\n",
    "fix BorderStroke import",
)
replace_once(
    effects,
    "import androidx.compose.foundation.layout.weight\n",
    "",
    "remove unnecessary weight import",
)

print("BdaySquirrel UI liveliness patch applied successfully")
