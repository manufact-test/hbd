package com.bdaysquirrel.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.bdaysquirrel.app.ui.AnimatedAppBackdrop
import com.bdaysquirrel.app.ui.BdaySquirrelRoot
import com.bdaysquirrel.app.ui.BirthdayViewModel

private const val PERMISSION_PREFS = "bdaysquirrel_permission_state"
private const val KEY_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"

class MainActivity : ComponentActivity() {
    private val viewModel: BirthdayViewModel by viewModels {
        val app = application as BdaySquirrelApplication
        BirthdayViewModel.Factory(app.birthdayRepository)
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF171226))
                    .statusBarsPadding()
                    .padding(top = 8.dp),
            ) {
                AnimatedAppBackdrop(modifier = Modifier.fillMaxSize())
                BdaySquirrelRoot(viewModel = viewModel)

                IconButton(
                    onClick = {
                        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 5.dp, end = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Настройки",
                        tint = Color(0xFF6FE7C8),
                    )
                }
            }
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        maybeRequestNotificationPermission()
    }

    private fun maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val preferences = getSharedPreferences(PERMISSION_PREFS, MODE_PRIVATE)
        if (preferences.getBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, false)) return

        preferences.edit()
            .putBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, true)
            .apply()
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
