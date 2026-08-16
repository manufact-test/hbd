package com.bdaysquirrel.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings as SettingsIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bdaysquirrel.app.reminders.BirthdayNotifications
import com.bdaysquirrel.app.ui.AnimatedAppBackdrop
import com.bdaysquirrel.app.ui.BdaySquirrelRoot
import com.bdaysquirrel.app.ui.BirthdayViewModel
import com.bdaysquirrel.app.ui.FirstRunOnboarding

private const val APP_STATE_PREFS = "bdaysquirrel_app_state"
private const val KEY_ONBOARDING_COMPLETE = "onboarding_v1_complete"
private const val KEY_NOTIFICATION_NUDGE_DISMISSED = "notification_nudge_dismissed"

class MainActivity : ComponentActivity() {
    private val viewModel: BirthdayViewModel by viewModels {
        val app = application as BdaySquirrelApplication
        BirthdayViewModel.Factory(app.birthdayRepository)
    }

    private val onboardingCompleteState = mutableStateOf(false)
    private val startAddEditorState = mutableStateOf(false)
    private val notificationPermissionGrantedState = mutableStateOf(false)
    private val notificationNudgeDismissedState = mutableStateOf(false)
    private var initialDatabaseChecked = false

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        notificationPermissionGrantedState.value = BirthdayNotifications.canPost(this)
        if (!notificationPermissionGrantedState.value) {
            dismissNotificationPrompt()
        }
    }

    private val contactsImportLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (
            result.resultCode == Activity.RESULT_OK &&
            !onboardingCompleteState.value
        ) {
            completeOnboarding()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(APP_STATE_PREFS, MODE_PRIVATE)
        onboardingCompleteState.value = preferences.getBoolean(KEY_ONBOARDING_COMPLETE, false)
        notificationNudgeDismissedState.value = preferences.getBoolean(
            KEY_NOTIFICATION_NUDGE_DISMISSED,
            false,
        )
        notificationPermissionGrantedState.value = BirthdayNotifications.canPost(this)

        val app = application as BdaySquirrelApplication

        setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(state.isLoaded) {
                if (state.isLoaded && !initialDatabaseChecked) {
                    initialDatabaseChecked = true
                    if (state.birthdays.isNotEmpty() && !onboardingCompleteState.value) {
                        completeOnboarding()
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF171226))
                    .statusBarsPadding()
                    .padding(top = 8.dp),
            ) {
                AnimatedAppBackdrop(modifier = Modifier.fillMaxSize())

                if (onboardingCompleteState.value) {
                    BdaySquirrelRoot(
                        viewModel = viewModel,
                        startAddEditor = startAddEditorState.value,
                        onImportContacts = ::openContactsImport,
                        showNotificationPrompt = state.isLoaded &&
                            state.birthdays.isNotEmpty() &&
                            !notificationPermissionGrantedState.value &&
                            !notificationNudgeDismissedState.value,
                        onEnableNotifications = ::requestNotificationAccess,
                        onDismissNotificationPrompt = ::dismissNotificationPrompt,
                    )

                    IconButton(
                        onClick = {
                            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 5.dp, end = 8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.SettingsIcon,
                            contentDescription = "Настройки",
                            tint = Color(0xFF6FE7C8),
                        )
                    }
                } else {
                    FirstRunOnboarding(
                        backupService = app.backupService,
                        onImportContacts = ::openContactsImport,
                        onAddManually = {
                            startAddEditorState.value = true
                            completeOnboarding()
                        },
                        onRestoreComplete = ::completeOnboarding,
                        onSkip = ::completeOnboarding,
                    )
                }
            }
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        notificationPermissionGrantedState.value = BirthdayNotifications.canPost(this)
    }

    private fun openContactsImport() {
        contactsImportLauncher.launch(
            Intent(this, ContactsImportActivity::class.java),
        )
    }

    private fun completeOnboarding() {
        onboardingCompleteState.value = true
        getSharedPreferences(APP_STATE_PREFS, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ONBOARDING_COMPLETE, true)
            .apply()
    }

    private fun dismissNotificationPrompt() {
        notificationNudgeDismissedState.value = true
        getSharedPreferences(APP_STATE_PREFS, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_NOTIFICATION_NUDGE_DISMISSED, true)
            .apply()
    }

    private fun requestNotificationAccess() {
        if (BirthdayNotifications.canPost(this)) {
            notificationPermissionGrantedState.value = true
            return
        }

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }

        startActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                data = Uri.parse("package:$packageName")
            },
        )
    }
}
