package com.robertong.ledgercal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robertong.ledgercal.model.CalendarDay
import com.robertong.ledgercal.theme.CalendarColors
import com.robertong.ledgercal.theme.CalendarStrings
import com.robertong.ledgercal.util.toCalendarDays
import com.robertong.ledgercal.util.toLong
import com.robertong.ledgercal.util.withComma
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.pow

/**
 * Full-page finance calendar screen composing [CalendarHeader], [CalendarView], and [TotalSummaryView].
 *
 * @param dateRange Epoch millis range for the displayed month.
 * @param calendarDays Pre-computed list of [CalendarDay] cells (use [com.robertong.ledgercal.util.toCalendarDays] to generate).
 * @param totalIncome Formatted total income string.
 * @param totalExpense Formatted total expense string.
 * @param totalAmount Formatted net total string.
 * @param startOfWeek The day the week starts on. Defaults to Monday.
 * @param colors Color configuration for all sub-components.
 * @param strings String configuration for all sub-components.
 * @param headerIcon Optional icon composable for the header.
 * @param summaryIcon Optional icon composable for the summary card.
 * @param onDayClicked Callback when a calendar day is tapped.
 */
@Composable
fun CalendarScreen(
    dateRange: LongRange,
    calendarDays: List<CalendarDay>,
    totalIncome: String,
    totalExpense: String,
    totalAmount: String,
    modifier: Modifier = Modifier,
    startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    colors: CalendarColors = CalendarColors(),
    strings: CalendarStrings = CalendarStrings(),
    headerIcon: (@Composable () -> Unit)? = null,
    summaryIcon: (@Composable () -> Unit)? = null,
    onDayClicked: (CalendarDay) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceColor)
    ) {
        CalendarHeader(
            range = dateRange,
            colors = colors,
            strings = strings,
            icon = headerIcon
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp)
        ) {
            CalendarView(
                calendarDays = calendarDays,
                startOfWeek = startOfWeek,
                colors = colors,
                strings = strings,
                onCalendarDayClicked = onDayClicked
            )

            TotalSummaryView(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                totalAmount = totalAmount,
                colors = colors,
                strings = strings,
                icon = summaryIcon
            )
        }
    }
}

// region Previews

private fun previewRange(): LongRange {
    val now = YearMonth.now()
    return now.atDay(1).toLong()..now.atEndOfMonth().toLong()
}

private fun previewCalendarDays(
    startOfWeek: DayOfWeek = DayOfWeek.MONDAY
): List<CalendarDay> {
    val now = YearMonth.now()
    val startDate = now.atDay(1)
    val endDate = now.atEndOfMonth()

    val dayData = mutableMapOf<LocalDate, Pair<String, String>>()
    var date = startDate
    while (!date.isAfter(endDate)) {
        val income = if ((date.dayOfMonth % 3) == 0) {
            (1000..99999).random().toString().withComma()
        } else ""
        val expense = if ((date.dayOfMonth % 4) == 0) {
            (100..50000).random().toString().withComma()
        } else ""
        dayData[date] = Pair(income, expense)
        date = date.plusDays(1)
    }

    return toCalendarDays(
        range = previewRange(),
        startOfWeek = startOfWeek,
        dayData = dayData
    )
}

@Preview(device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
private fun CalendarScreenPreview() {
    CalendarScreen(
        dateRange = previewRange(),
        calendarDays = previewCalendarDays(),
        totalIncome = "999,999",
        totalExpense = "123,456",
        totalAmount = "876,543",
        onDayClicked = {}
    )
}

@Preview(device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
private fun CalendarScreenSundayStartPreview() {
    CalendarScreen(
        dateRange = previewRange(),
        calendarDays = previewCalendarDays(startOfWeek = DayOfWeek.SUNDAY),
        totalIncome = "500,000",
        totalExpense = "750,000",
        totalAmount = "-250,000",
        startOfWeek = DayOfWeek.SUNDAY,
        onDayClicked = {}
    )
}

@Preview(device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
private fun CalendarScreenEmptyPreview() {
    val range = previewRange()
    CalendarScreen(
        dateRange = range,
        calendarDays = toCalendarDays(range = range),
        totalIncome = "0",
        totalExpense = "0",
        totalAmount = "0",
        onDayClicked = {}
    )
}

@Preview(device = "id:2.7in QVGA")
@Preview(device = "id:Nexus One")
@Preview(device = "id:pixel_7a")
@Composable
private fun CalendarScreenMultiDevicePreview() {
    CalendarScreen(
        dateRange = previewRange(),
        calendarDays = previewCalendarDays(),
        totalIncome = "36,000",
        totalExpense = "18,600",
        totalAmount = "17,400",
        onDayClicked = {}
    )
}

// endregion
