package com.bdaysquirrel.app

import android.app.Application
import com.bdaysquirrel.app.data.BdayDatabase
import com.bdaysquirrel.app.data.BirthdayRepository

class BdaySquirrelApplication : Application() {
    val database: BdayDatabase by lazy {
        BdayDatabase.create(this)
    }

    val birthdayRepository: BirthdayRepository by lazy {
        BirthdayRepository(database.birthdayDao())
    }
}
