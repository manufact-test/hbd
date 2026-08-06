package com.bdaysquirrel.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.bdaysquirrel.app.ui.BdaySquirrelRoot
import com.bdaysquirrel.app.ui.BirthdayViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BirthdayViewModel by viewModels {
        val app = application as BdaySquirrelApplication
        BirthdayViewModel.Factory(app.birthdayRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BdaySquirrelRoot(viewModel = viewModel)
        }
    }
}
