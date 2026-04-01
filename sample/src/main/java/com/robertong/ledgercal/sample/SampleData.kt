package com.robertong.ledgercal.sample

import com.robertong.ledgercal.model.CalendarDay
import com.robertong.ledgercal.util.toCalendarDays
import com.robertong.ledgercal.util.toLong
import com.robertong.ledgercal.util.withComma
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import kotlin.math.pow

fun getSampleRange(): LongRange {
    val now = YearMonth.now()
    val start = now.atDay(1)
    val end = now.atEndOfMonth()
    return (start.toLong()..end.toLong())
}

fun getSampleCalendarDays(
    startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    generateData: Boolean = true
): List<CalendarDay> {
    val range = getSampleRange()
    val now = YearMonth.now()
    val startDate = now.atDay(1)
    val endDate = now.atEndOfMonth()

    val dayData = if (generateData) {
        val map = mutableMapOf<LocalDate, Pair<String, String>>()
        var date = startDate
        while (!date.isAfter(endDate)) {
            val income = if ((0..10).random() > 5) {
                (0..10.0.pow((1..6).random()).toInt()).random().toString().withComma()
            } else ""

            val expense = if ((0..10).random() > 5) {
                (0..10.0.pow((1..6).random()).toInt()).random().toString().withComma()
            } else ""

            map[date] = Pair(income, expense)
            date = date.plusDays(1)
        }
        map
    } else {
        emptyMap()
    }

    return toCalendarDays(
        range = range,
        startOfWeek = startOfWeek,
        dayData = dayData
    )
}
