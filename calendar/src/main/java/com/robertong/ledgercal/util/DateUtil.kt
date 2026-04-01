package com.robertong.ledgercal.util

import com.robertong.ledgercal.model.CalendarDay
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import kotlin.math.abs

private const val DAYS_PER_WEEK = 7

fun Long.toLocalDate(
    zoneId: ZoneId = ZoneId.systemDefault()
): LocalDate = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()

fun LocalDate.toLong(
    zoneId: ZoneId = ZoneId.systemDefault()
): Long = atStartOfDay(zoneId).toInstant().toEpochMilli()

fun getDaysInBetween(range: LongRange): Int {
    val startDate = range.first.toLocalDate()
    val endDate = range.last.toLocalDate()
    return Period.between(startDate, endDate).days
}

/**
 * Converts a date range and per-day income/expense data into a grid of [CalendarDay] cells,
 * padded to align with the specified [startOfWeek].
 *
 * @param range Epoch millis range representing the first and last day of the month.
 * @param startOfWeek The day the calendar week starts on.
 * @param dayData A map of [LocalDate] to pairs of (income, expense) formatted strings.
 */
fun toCalendarDays(
    range: LongRange,
    startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    dayData: Map<LocalDate, Pair<String, String>> = emptyMap()
): List<CalendarDay> {
    val startDate = range.first.toLocalDate()
    val daysInBetween = getDaysInBetween(range)

    val weekdayDiff = startDate.dayOfWeek.value - startOfWeek.value
    val prefixDayCount = if (weekdayDiff >= 0) weekdayDiff else DAYS_PER_WEEK - abs(weekdayDiff)

    val totalDays = (prefixDayCount + daysInBetween) + 1
    val appendixDayCount = if (totalDays % DAYS_PER_WEEK != 0) {
        DAYS_PER_WEEK - (totalDays % DAYS_PER_WEEK)
    } else {
        0
    }

    return buildList {
        // Prefix padding (days before month start)
        repeat(prefixDayCount) {
            add(CalendarDay(date = LocalDate.MIN, isInRange = false))
        }

        // Actual days in the month
        for (i in 0L..daysInBetween.toLong()) {
            val theDate = startDate.plusDays(i)
            val data = dayData[theDate]
            add(
                CalendarDay(
                    date = theDate,
                    income = data?.first ?: "",
                    expense = data?.second ?: ""
                )
            )
        }

        // Suffix padding (days after month end)
        repeat(appendixDayCount) {
            add(CalendarDay(date = LocalDate.MIN, isInRange = false))
        }
    }
}
