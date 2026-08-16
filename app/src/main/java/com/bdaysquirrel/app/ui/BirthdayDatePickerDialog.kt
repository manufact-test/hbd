package com.bdaysquirrel.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val PickerDeepIndigo = Color(0xFF241A3A)
private val PickerCardViolet = Color(0xFF2D2144)
private val PickerRaisedViolet = Color(0xFF392A55)
private val PickerCoral = Color(0xFFFF7A3D)
private val PickerMint = Color(0xFF6FE7C8)
private val PickerLavender = Color(0xFFA98BFF)
private val PickerCream = Color(0xFFFFF4D8)
private val PickerSoftCream = Color(0xFFE8DEC7)
private val PickerMuted = Color(0xFFB8B1CA)
private val PickerDarkText = Color(0xFF21162B)

private const val MIN_BIRTH_YEAR = 1900

@Composable
internal fun BirthdayDatePickerDialog(
    initialMonth: Int,
    initialDay: Int,
    initialYear: Int?,
    includeYear: Boolean,
    locale: Locale,
    today: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit,
    onConfirm: (year: Int?, month: Int, day: Int) -> Unit,
) {
    val initialSelectedYear = (initialYear ?: today.minusYears(30).year)
        .coerceIn(MIN_BIRTH_YEAR, today.year)
    var selectedYear by rememberSaveable { mutableIntStateOf(initialSelectedYear) }
    var selectedMonth by rememberSaveable { mutableIntStateOf(initialMonth.coerceIn(1, 12)) }
    var selectedDay by rememberSaveable { mutableIntStateOf(initialDay.coerceAtLeast(1)) }

    LaunchedEffect(includeYear, selectedYear) {
        if (includeYear && selectedYear == today.year && selectedMonth > today.monthValue) {
            selectedMonth = today.monthValue
        }
    }

    val monthLength = birthdayMonthLength(
        year = if (includeYear) selectedYear else null,
        month = selectedMonth,
    )
    val maxSelectableDay = if (
        includeYear &&
        selectedYear == today.year &&
        selectedMonth == today.monthValue
    ) {
        minOf(monthLength, today.dayOfMonth)
    } else {
        monthLength
    }

    LaunchedEffect(selectedYear, selectedMonth, includeYear) {
        if (selectedDay > maxSelectableDay) selectedDay = maxSelectableDay
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            val compact = maxHeight < 610.dp
            val outerPadding = if (compact) 12.dp else 18.dp
            val sectionGap = if (compact) 10.dp else 16.dp
            val monthCellHeight = if (compact) 34.dp else 40.dp
            val dayCellHeight = if (compact) 31.dp else 38.dp
            val monthColumns = if (maxWidth < 390.dp) 4 else 4

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .heightIn(max = maxHeight),
                shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
                color = PickerDeepIndigo,
                contentColor = PickerCream,
                border = BorderStroke(2.dp, PickerRaisedViolet),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(outerPadding),
                ) {
                    Text(
                        text = if (includeYear) "Выбери дату рождения" else "Выбери день и месяц",
                        style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                        color = PickerCream,
                    )

                    if (includeYear) {
                        Spacer(modifier = Modifier.height(sectionGap))
                        PickerSectionLabel("ГОД")
                        Spacer(modifier = Modifier.height(7.dp))
                        BirthdayYearStrip(
                            selectedYear = selectedYear,
                            currentYear = today.year,
                            compact = compact,
                            onYearSelected = { year -> selectedYear = year },
                        )
                    }

                    Spacer(modifier = Modifier.height(sectionGap))
                    PickerSectionLabel("МЕСЯЦ")
                    Spacer(modifier = Modifier.height(7.dp))

                    (1..12).chunked(monthColumns).forEachIndexed { rowIndex, monthRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            monthRow.forEach { month ->
                                val selected = month == selectedMonth
                                val enabled = !includeYear ||
                                    selectedYear < today.year ||
                                    month <= today.monthValue
                                TextButton(
                                    onClick = { selectedMonth = month },
                                    enabled = enabled,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(monthCellHeight),
                                    shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                                    contentPadding = PaddingValues(horizontal = 2.dp),
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = if (selected) PickerCoral else PickerCardViolet,
                                        contentColor = if (selected) PickerDarkText else PickerSoftCream,
                                        disabledContentColor = PickerMuted.copy(alpha = 0.35f),
                                    ),
                                ) {
                                    Text(
                                        text = monthShortLabelAdaptive(month, locale),
                                        fontSize = if (compact) 10.sp else 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                        if (rowIndex < 2) Spacer(modifier = Modifier.height(5.dp))
                    }

                    Spacer(modifier = Modifier.height(sectionGap))
                    PickerSectionLabel("ДЕНЬ")
                    Spacer(modifier = Modifier.height(7.dp))

                    (1..monthLength).chunked(7).forEach { dayRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            dayRow.forEach { day ->
                                val selected = day == selectedDay
                                val enabled = day <= maxSelectableDay
                                TextButton(
                                    onClick = { selectedDay = day },
                                    enabled = enabled,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(dayCellHeight),
                                    shape = CutCornerShape(topStart = 5.dp, bottomEnd = 5.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = if (selected) PickerLavender else Color.Transparent,
                                        contentColor = if (selected) PickerDarkText else PickerSoftCream,
                                        disabledContentColor = PickerMuted.copy(alpha = 0.30f),
                                    ),
                                ) {
                                    Text(
                                        text = day.toString(),
                                        fontSize = if (compact) 11.sp else 13.sp,
                                        fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                                    )
                                }
                            }
                            repeat(7 - dayRow.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(if (compact) 10.dp else 16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Отмена", color = PickerMuted)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = {
                                onConfirm(
                                    if (includeYear) selectedYear else null,
                                    selectedMonth,
                                    selectedDay,
                                )
                            },
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PickerCoral,
                                contentColor = PickerDarkText,
                            ),
                        ) {
                            Text("Готово")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PickerSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = PickerMint,
        letterSpacing = 1.sp,
    )
}

@Composable
private fun BirthdayYearStrip(
    selectedYear: Int,
    currentYear: Int,
    compact: Boolean,
    onYearSelected: (Int) -> Unit,
) {
    val years = remember(currentYear) { (MIN_BIRTH_YEAR..currentYear).toList() }
    val selectedIndex = (selectedYear - MIN_BIRTH_YEAR).coerceIn(0, years.lastIndex)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (selectedIndex - 2).coerceAtLeast(0),
    )

    LaunchedEffect(selectedYear) {
        val index = (selectedYear - MIN_BIRTH_YEAR).coerceIn(0, years.lastIndex)
        listState.animateScrollToItem((index - 2).coerceAtLeast(0))
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
    ) {
        items(years.size) { index ->
            val year = years[index]
            val selected = year == selectedYear
            TextButton(
                onClick = { onYearSelected(year) },
                modifier = Modifier
                    .width(if (compact) 62.dp else 70.dp)
                    .height(if (compact) 34.dp else 40.dp),
                shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (selected) PickerMint else PickerCardViolet,
                    contentColor = if (selected) PickerDarkText else PickerSoftCream,
                ),
            ) {
                Text(
                    text = year.toString(),
                    fontSize = if (compact) 11.sp else 13.sp,
                    fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                )
            }
        }
    }
}

internal fun birthdayMonthLength(year: Int?, month: Int): Int {
    val safeMonth = month.coerceIn(1, 12)
    return YearMonth.of(year ?: 2000, safeMonth).lengthOfMonth()
}

private fun monthShortLabelAdaptive(month: Int, locale: Locale): String {
    val raw = Month.of(month)
        .getDisplayName(TextStyle.SHORT, locale)
        .replace(".", "")
    return raw.replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(locale) else char.toString()
    }
}
