from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    if old not in text:
        raise SystemExit(f"missing expected block: {label}")
    return text.replace(old, new, 1)


app_path = Path("app/src/main/java/com/bdaysquirrel/app/ui/BdaySquirrelApp.kt")
app = app_path.read_text()
app = app.replace("import android.app.DatePickerDialog\n", "")
app = app.replace("import java.time.ZoneId\n", "")
app = replace_once(
    app,
    "import androidx.compose.foundation.background\n",
    "import androidx.compose.foundation.background\nimport androidx.compose.foundation.clickable\n",
    "clickable import",
)
app = replace_once(
    app,
    "    var editingBirthday by remember { mutableStateOf<BirthdayEntity?>(null) }\n",
    "    var editingBirthday by remember { mutableStateOf<BirthdayEntity?>(null) }\n    var viewingBirthday by remember { mutableStateOf<BirthdayUiModel?>(null) }\n",
    "viewing state",
)
app = replace_once(
    app,
    """                    BirthdayCard(
                        model = birthday,
                        burstOnEnter = birthday.birthday.id == newlyAddedBirthdayId,
                        onEdit = { editingBirthday = birthday.birthday },
                        onDelete = { onDeleteBirthday(birthday.birthday) },
                    )
""",
    """                    BirthdayCard(
                        model = birthday,
                        burstOnEnter = birthday.birthday.id == newlyAddedBirthdayId,
                        onOpen = { viewingBirthday = birthday },
                        onEdit = { editingBirthday = birthday.birthday },
                        onDelete = { onDeleteBirthday(birthday.birthday) },
                    )
""",
    "card open callback",
)
app = replace_once(
    app,
    """    editingBirthday?.let { birthday ->
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
""",
    """    editingBirthday?.let { birthday ->
        BirthdayEditorSheet(
            birthday = birthday,
            onDismiss = { editingBirthday = null },
            onSave = { name, day, month, year, note, photoUri ->
                onUpdateBirthday(birthday, name, day, month, year, note, photoUri)
                editingBirthday = null
            },
        )
    }

    viewingBirthday?.let { model ->
        BirthdayDetailsSheet(
            model = model,
            onDismiss = { viewingBirthday = null },
            onEdit = {
                viewingBirthday = null
                editingBirthday = model.birthday
            },
        )
    }
}

@Composable
private fun AppHeader() {
""",
    "detail sheet integration",
)
app = replace_once(
    app,
    """private fun BirthdayCard(
    model: BirthdayUiModel,
    burstOnEnter: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
""",
    """private fun BirthdayCard(
    model: BirthdayUiModel,
    burstOnEnter: Boolean,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
""",
    "card signature",
)
app = replace_once(
    app,
    """        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
""",
    """        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .graphicsLayer {
""",
    "card clickable",
)
app = replace_once(
    app,
    """                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftCream,
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = countdownLabel(model.daysUntil),
""",
    """                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftCream,
                )
                Spacer(modifier = Modifier.height(5.dp))
                val zodiac = zodiacInfo(model.birthday.day, model.birthday.month)
                Text(
                    text = "${zodiac.symbol} ${zodiac.name} • ${birthYearLabel(model.birthday.year)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Lavender,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = countdownLabel(model.daysUntil),
""",
    "card zodiac metadata",
)
app = replace_once(
    app,
    "    var showYearlessPicker by rememberSaveable(birthday?.id) { mutableStateOf(false) }\n",
    "    var showBirthdayPicker by rememberSaveable(birthday?.id) { mutableStateOf(false) }\n",
    "picker state",
)
app = replace_once(
    app,
    """            OutlinedButton(
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
""",
    """            OutlinedButton(
                onClick = { showBirthdayPicker = true },
""",
    "replace system picker",
)
app = replace_once(
    app,
    """                Checkbox(
                    checked = yearUnknown,
                    onCheckedChange = { yearUnknown = it },
                )
""",
    """                Checkbox(
                    checked = yearUnknown,
                    onCheckedChange = { unknown ->
                        if (yearUnknown && !unknown) {
                            selectedDate?.let { date ->
                                val targetYear = today.minusYears(30).year
                                val yearMonth = YearMonth.of(targetYear, date.monthValue)
                                selectedDateEpochDay = yearMonth
                                    .atDay(date.dayOfMonth.coerceAtMost(yearMonth.lengthOfMonth()))
                                    .toEpochDay()
                            }
                        }
                        yearUnknown = unknown
                    },
                )
""",
    "year toggle normalization",
)
app = replace_once(
    app,
    """    if (showYearlessPicker) {
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
""",
    """    if (showBirthdayPicker) {
        val pickerDate = selectedDate ?: today.minusYears(30)
        BirthdayDatePickerDialog(
            initialMonth = pickerDate.monthValue,
            initialDay = pickerDate.dayOfMonth,
            initialYear = if (yearUnknown) null else pickerDate.year,
            includeYear = !yearUnknown,
            locale = locale,
            today = today,
            onDismiss = { showBirthdayPicker = false },
            onConfirm = { year, month, day ->
                if (yearUnknown) {
                    val anchorYear = selectedDate?.year ?: today.minusYears(30).year
                    val anchorMonth = YearMonth.of(anchorYear, month)
                    val storageYear = if (day <= anchorMonth.lengthOfMonth()) anchorYear else 2000
                    selectedDateEpochDay = LocalDate.of(storageYear, month, day).toEpochDay()
                } else {
                    val selectedYear = year ?: today.minusYears(30).year
                    selectedDateEpochDay = LocalDate.of(selectedYear, month, day).toEpochDay()
                }
                showBirthdayPicker = false
            },
        )
    }
""",
    "adaptive picker invocation",
)
app_path.write_text(app)

onboarding_path = Path("app/src/main/java/com/bdaysquirrel/app/ui/OnboardingUi.kt")
onboarding = onboarding_path.read_text()
onboarding = onboarding.replace("import androidx.compose.material.icons.filled.Lock\n", "")
onboarding = replace_once(
    onboarding,
    """        Spacer(modifier = Modifier.height(18.dp))

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
""",
    """        Spacer(modifier = Modifier.height(26.dp))
""",
    "remove privacy panel",
)
onboarding = onboarding.replace(
    "Найдём контакты с датой рождения, покажем превью и отметим возможные дубли.",
    "Найдём контакты с датой рождения, покажем список и отметим возможные дубли.",
)
onboarding = replace_once(
    onboarding,
    """
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Доступ к контактам запрашивается только если ты выберешь импорт. Без него BdaySquirrel полностью работает с ручным вводом.",
            color = OnboardingMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )
""",
    "",
    "remove onboarding permission explanation",
)
onboarding_path.write_text(onboarding)

picker_path = Path("app/src/main/java/com/bdaysquirrel/app/ui/BirthdayDatePickerDialog.kt")
picker = picker_path.read_text()
picker = replace_once(
    picker,
    "import androidx.compose.foundation.lazy.LazyRow\n",
    "import androidx.compose.foundation.lazy.LazyRow\nimport androidx.compose.foundation.lazy.items\n",
    "lazy items import",
)
picker_path.write_text(picker)

build_path = Path("app/build.gradle.kts")
build = build_path.read_text()
build = replace_once(build, "versionCode = 5\n        versionName = \"0.3.0\"", "versionCode = 6\n        versionName = \"0.3.1\"", "version bump")
build_path.write_text(build)
