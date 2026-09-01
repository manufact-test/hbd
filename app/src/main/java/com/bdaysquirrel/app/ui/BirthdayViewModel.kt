package com.bdaysquirrel.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bdaysquirrel.app.data.BirthdayEntity
import com.bdaysquirrel.app.data.BirthdayRepository
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BirthdayUiModel(
    val birthday: BirthdayEntity,
    val nextDate: LocalDate,
    val daysUntil: Long,
    val ageOnBirthday: Int?,
)

enum class BirthdayBrowseMode {
    UPCOMING,
    MONTHS,
}

data class BirthdayMonthSummary(
    val month: Int,
    val count: Int,
)

data class BirthdayMonthSection(
    val month: Int,
    val birthdays: List<BirthdayUiModel>,
)

data class BirthdayUiState(
    /** Complete collection, always sorted by the next occurrence. */
    val birthdays: List<BirthdayUiModel> = emptyList(),
    /** Search matches in the current default (nearest-first) order. */
    val matchedBirthdays: List<BirthdayUiModel> = emptyList(),
    /** Search matches grouped by calendar month and sorted by day. */
    val monthSections: List<BirthdayMonthSection> = emptyList(),
    /** Counts for the complete collection; search never changes these. */
    val monthSummaries: List<BirthdayMonthSummary> = emptyList(),
    val isLoaded: Boolean = false,
    val searchQuery: String = "",
    val browseMode: BirthdayBrowseMode = BirthdayBrowseMode.UPCOMING,
    /** null means all month sections. */
    val selectedMonth: Int? = null,
)

class BirthdayViewModel(
    private val repository: BirthdayRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val browseMode = MutableStateFlow(BirthdayBrowseMode.UPCOMING)
    private val selectedMonth = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<BirthdayUiState> = combine(
        repository.birthdays,
        searchQuery,
        browseMode,
        selectedMonth,
    ) { birthdays, query, mode, month ->
        buildBirthdayUiState(
            birthdays = birthdays,
            query = query,
            browseMode = mode,
            selectedMonth = month,
            today = LocalDate.now(clock),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BirthdayUiState(),
    )

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun showUpcoming() {
        browseMode.value = BirthdayBrowseMode.UPCOMING
    }

    fun showMonths() {
        browseMode.value = BirthdayBrowseMode.MONTHS
        val state = uiState.value
        selectedMonth.value = nextRelevantMonth(
            monthSummaries = state.monthSummaries,
            currentMonth = LocalDate.now(clock).monthValue,
        )
    }

    fun selectMonth(month: Int?) {
        require(month == null || month in 1..12) { "Month must be between 1 and 12" }
        selectedMonth.value = month
    }

    fun addBirthday(
        name: String,
        day: Int,
        month: Int,
        year: Int?,
        note: String,
        photoUri: String?,
    ) {
        viewModelScope.launch {
            repository.add(name, day, month, year, note, photoUri)
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
            repository.update(birthday, name, day, month, year, note, photoUri)
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

internal fun buildBirthdayUiState(
    birthdays: List<BirthdayEntity>,
    query: String,
    browseMode: BirthdayBrowseMode,
    selectedMonth: Int?,
    today: LocalDate,
): BirthdayUiState {
    val allModels = birthdays
        .map { it.toUiModel(today) }
        .sortedWith(upcomingBirthdayComparator)
    val queryTokens = normalizeSearchQuery(query)
    val matches = allModels.filter { model ->
        birthdayMatches(model.birthday, queryTokens)
    }
    val sections = matches
        .groupBy { it.birthday.month }
        .toSortedMap()
        .map { (month, models) ->
            BirthdayMonthSection(
                month = month,
                birthdays = models.sortedWith(monthBirthdayComparator),
            )
        }
    val summaries = allModels
        .groupingBy { it.birthday.month }
        .eachCount()
        .toSortedMap()
        .map { (month, count) -> BirthdayMonthSummary(month, count) }

    return BirthdayUiState(
        birthdays = allModels,
        matchedBirthdays = matches,
        monthSections = sections,
        monthSummaries = summaries,
        isLoaded = true,
        searchQuery = query,
        browseMode = browseMode,
        selectedMonth = selectedMonth,
    )
}

internal fun normalizeSearchQuery(query: String): List<String> = query
    .trim()
    .lowercase(Locale.ROOT)
    .split(Regex("\\s+"))
    .filter(String::isNotBlank)

internal fun birthdayMatches(
    birthday: BirthdayEntity,
    queryTokens: List<String>,
): Boolean {
    if (queryTokens.isEmpty()) return true
    val searchableText = "${birthday.name} ${birthday.note}".lowercase(Locale.ROOT)
    return queryTokens.all(searchableText::contains)
}

internal fun nextRelevantMonth(
    monthSummaries: List<BirthdayMonthSummary>,
    currentMonth: Int,
): Int? {
    if (monthSummaries.isEmpty()) return null
    return monthSummaries.firstOrNull { it.month >= currentMonth }?.month
        ?: monthSummaries.first().month
}

private val upcomingBirthdayComparator =
    compareBy<BirthdayUiModel> { it.daysUntil }
        .thenBy { it.birthday.name.lowercase(Locale.ROOT) }

private val monthBirthdayComparator =
    compareBy<BirthdayUiModel> { it.birthday.day }
        .thenBy { it.birthday.name.lowercase(Locale.ROOT) }

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
        daysUntil = ChronoUnit.DAYS.between(today, nextDate),
        ageOnBirthday = year?.let { birthYear -> nextDate.year - birthYear },
    )
}

private fun safeDate(year: Int, month: Int, day: Int): LocalDate {
    val yearMonth = YearMonth.of(year, month)
    return yearMonth.atDay(day.coerceAtMost(yearMonth.lengthOfMonth()))
}
