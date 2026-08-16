package com.bdaysquirrel.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bdaysquirrel.app.data.BirthdayEntity
import com.bdaysquirrel.app.data.BirthdayRepository
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BirthdayUiModel(
    val birthday: BirthdayEntity,
    val nextDate: LocalDate,
    val daysUntil: Long,
    val ageOnBirthday: Int?,
)

data class BirthdayUiState(
    val birthdays: List<BirthdayUiModel> = emptyList(),
    val isLoaded: Boolean = false,
)

class BirthdayViewModel(
    private val repository: BirthdayRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
) : ViewModel() {

    val uiState: StateFlow<BirthdayUiState> = repository.birthdays
        .map { birthdays ->
            val today = LocalDate.now(clock)
            BirthdayUiState(
                birthdays = birthdays
                    .map { it.toUiModel(today) }
                    .sortedWith(
                        compareBy<BirthdayUiModel> { it.daysUntil }
                            .thenBy { it.birthday.name.lowercase() },
                    ),
                isLoaded = true,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BirthdayUiState(),
        )

    fun addBirthday(
        name: String,
        day: Int,
        month: Int,
        year: Int?,
        note: String,
        photoUri: String?,
    ) {
        viewModelScope.launch {
            repository.add(
                name = name,
                day = day,
                month = month,
                year = year,
                note = note,
                photoUri = photoUri,
            )
        }
    }

    fun updateBirthday(
        birthday: BirthdayEntity,
        name: String,
        day: Int,
        month: Int,
        year: Int?,
        note: String,
        photoUri: String?,
    ) {
        viewModelScope.launch {
            repository.update(
                birthday = birthday,
                name = name,
                day = day,
                month = month,
                year = year,
                note = note,
                photoUri = photoUri,
            )
        }
    }

    fun deleteBirthday(birthday: BirthdayEntity) {
        viewModelScope.launch {
            repository.delete(birthday)
        }
    }

    class Factory(
        private val repository: BirthdayRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(BirthdayViewModel::class.java)) {
                "Unknown ViewModel class: ${modelClass.name}"
            }
            return BirthdayViewModel(repository) as T
        }
    }
}

private fun BirthdayEntity.toUiModel(today: LocalDate): BirthdayUiModel {
    val birthdayThisYear = safeDate(today.year, month, day)
    val nextDate = if (birthdayThisYear.isBefore(today)) {
        safeDate(today.year + 1, month, day)
    } else {
        birthdayThisYear
    }

    return BirthdayUiModel(
        birthday = this,
        nextDate = nextDate,
        daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, nextDate),
        ageOnBirthday = year?.let { birthYear -> nextDate.year - birthYear },
    )
}

private fun safeDate(year: Int, month: Int, day: Int): LocalDate {
    val yearMonth = YearMonth.of(year, month)
    return yearMonth.atDay(day.coerceAtMost(yearMonth.lengthOfMonth()))
}
