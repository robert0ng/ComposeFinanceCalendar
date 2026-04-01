package com.robertong.ledgercal.model

import java.time.LocalDate

/**
 * Represents a single day in the calendar grid.
 *
 * @param date The date of this calendar cell.
 * @param income Formatted income amount string (e.g., "36,000"). Empty string means no income.
 * @param expense Formatted expense amount string (e.g., "18,600"). Empty string means no expense.
 * @param isToday Whether this day is today. Defaults to comparing [date] with [LocalDate.now].
 * @param isInRange Whether this day is within the displayed month range.
 *                  Days outside the range (padding cells) are not rendered.
 */
data class CalendarDay(
    val date: LocalDate,
    val income: String = "",
    val expense: String = "",
    val isToday: Boolean = (date == LocalDate.now()),
    val isInRange: Boolean = true
)
