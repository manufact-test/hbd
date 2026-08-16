from pathlib import Path


def replace_once(path: str, old: str, new: str) -> None:
    file_path = Path(path)
    text = file_path.read_text()
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"Expected exactly one match in {path}, found {count}: {old[:80]!r}")
    file_path.write_text(text.replace(old, new, 1))


app = "app/src/main/java/com/bdaysquirrel/app/ui/BdaySquirrelApp.kt"
settings = "app/src/main/java/com/bdaysquirrel/app/SettingsActivity.kt"
gradle = "app/build.gradle.kts"

replace_once(
    app,
    "import androidx.compose.material.icons.filled.PhotoLibrary\n",
    "import androidx.compose.material.icons.filled.PhotoLibrary\nimport androidx.compose.material.icons.filled.Notifications\n",
)

replace_once(
    app,
    """@Composable
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
""",
    """@Composable
fun BdaySquirrelRoot(
    viewModel: BirthdayViewModel,
    startAddEditor: Boolean = false,
    onImportContacts: () -> Unit = {},
    showNotificationPrompt: Boolean = false,
    onEnableNotifications: () -> Unit = {},
    onDismissNotificationPrompt: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MaterialTheme(
        colorScheme = BdayColorScheme,
        typography = BdayTypography,
        shapes = BdayShapes,
    ) {
        BirthdayScreen(
            state = state,
            startAddEditor = startAddEditor,
            onImportContacts = onImportContacts,
            showNotificationPrompt = showNotificationPrompt,
            onEnableNotifications = onEnableNotifications,
            onDismissNotificationPrompt = onDismissNotificationPrompt,
            onAddBirthday = viewModel::addBirthday,
            onUpdateBirthday = viewModel::updateBirthday,
            onDeleteBirthday = viewModel::deleteBirthday,
        )
    }
}
""",
)

replace_once(
    app,
    """private fun BirthdayScreen(
    state: BirthdayUiState,
    onAddBirthday: (String, Int, Int, Int?, String, String?) -> Unit,
    onUpdateBirthday: (BirthdayEntity, String, Int, Int, Int?, String, String?) -> Unit,
    onDeleteBirthday: (BirthdayEntity) -> Unit,
) {
    var showAddSheet by rememberSaveable { mutableStateOf(false) }
""",
    """private fun BirthdayScreen(
    state: BirthdayUiState,
    startAddEditor: Boolean,
    onImportContacts: () -> Unit,
    showNotificationPrompt: Boolean,
    onEnableNotifications: () -> Unit,
    onDismissNotificationPrompt: () -> Unit,
    onAddBirthday: (String, Int, Int, Int?, String, String?) -> Unit,
    onUpdateBirthday: (BirthdayEntity, String, Int, Int, Int?, String, String?) -> Unit,
    onDeleteBirthday: (BirthdayEntity) -> Unit,
) {
    var showAddSheet by rememberSaveable { mutableStateOf(startAddEditor) }
""",
)

replace_once(
    app,
    """            item {
                HeroPanel(totalBirthdays = state.birthdays.size)
            }

            item {
                SectionHeader()
            }
""",
    """            item {
                HeroPanel(totalBirthdays = state.birthdays.size)
            }

            if (showNotificationPrompt) {
                item {
                    NotificationPermissionPanel(
                        onEnable = onEnableNotifications,
                        onDismiss = onDismissNotificationPrompt,
                    )
                }
            }

            item {
                SectionHeader()
            }
""",
)

replace_once(
    app,
    """                item {
                    EmptyBirthdays(onAddClick = { showAddSheet = true })
                }
""",
    """                item {
                    EmptyBirthdays(
                        onAddClick = { showAddSheet = true },
                        onImportClick = onImportContacts,
                    )
                }
""",
)

replace_once(
    app,
    """@Composable
private fun EmptyBirthdays(onAddClick: () -> Unit) {
    PixelPanel(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = \"Пока здесь тихо\",
                style = MaterialTheme.typography.titleLarge,
                color = Cream,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = \"Добавь первый день рождения — он сразу появится в списке ближайших событий.\",
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
                Text(\"Добавить дату\")
            }
        }
    }
}
""",
    """@Composable
private fun EmptyBirthdays(
    onAddClick: () -> Unit,
    onImportClick: () -> Unit,
) {
    PixelPanel(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = \"Пока здесь тихо\",
                style = MaterialTheme.typography.titleLarge,
                color = Cream,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = \"Импортируй сохранённые дни рождения из контактов или добавь первую дату вручную.\",
                style = MaterialTheme.typography.bodyLarge,
                color = MutedText,
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onImportClick,
                modifier = Modifier.fillMaxWidth(),
                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquirrelCoral,
                    contentColor = DarkText,
                ),
                border = BorderStroke(2.dp, DarkOutline),
            ) {
                Text(\"Импортировать из контактов\")
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onAddClick,
                modifier = Modifier.fillMaxWidth(),
                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(\"Добавить вручную\")
            }
        }
    }
}

@Composable
private fun NotificationPermissionPanel(
    onEnable: () -> Unit,
    onDismiss: () -> Unit,
) {
    PixelPanel(
        modifier = Modifier.fillMaxWidth(),
        backgroundBrush = Brush.horizontalGradient(
            listOf(DeepIndigo, Color(0xFF34234C)),
        ),
        borderColor = Mint,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Mint,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = \"Разрешить напоминания?\",
                        style = MaterialTheme.typography.titleMedium,
                        color = Cream,
                    )
                    Text(
                        text = \"Теперь в списке есть даты. Разрешение нужно только чтобы BdaySquirrel мог вовремя присылать уведомления.\",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText,
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = onEnable,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Mint,
                        contentColor = DarkText,
                    ),
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                ) {
                    Text(\"Включить\")
                }
                TextButton(onClick = onDismiss) {
                    Text(\"Позже\", color = MutedText)
                }
            }
        }
    }
}
""",
)

replace_once(
    settings,
    "import androidx.compose.material.icons.filled.ArrowBack\n",
    "import androidx.compose.material.icons.filled.ArrowBack\nimport androidx.compose.material.icons.filled.Contacts\n",
)

replace_once(
    settings,
    """            SettingsSectionLabel(\"РЕЗЕРВНАЯ КОПИЯ\")
""",
    """            SettingsSectionLabel(\"КОНТАКТЫ\")
            SettingsPanel {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = \"Быстро добавить даты\",
                        color = SettingsCream,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = \"BdaySquirrel найдёт дни рождения, уже сохранённые в системных контактах, и покажет список до импорта.\",
                        color = SettingsMuted,
                        fontSize = 13.sp,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = {
                            context.startActivity(Intent(context, ContactsImportActivity::class.java))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    ) {
                        Icon(Icons.Default.Contacts, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(\"Импортировать из контактов\")
                    }
                }
            }

            SettingsSectionLabel(\"РЕЗЕРВНАЯ КОПИЯ\")
""",
)

replace_once(
    gradle,
    '        versionCode = 4\n        versionName = "0.2.0"\n',
    '        versionCode = 5\n        versionName = "0.3.0"\n',
)

Path("dev-patches/apply_onboarding_ui.py").unlink(missing_ok=True)
Path(".github/workflows/apply-onboarding-ui.yml").unlink(missing_ok=True)
