package com.robertong.ledgercal.theme

/**
 * String configuration for the finance calendar components.
 *
 * Override these to localize the calendar into any language.
 */
data class CalendarStrings(
    /** Header title formatter. Receives (year, month). */
    val headerTitle: (year: Int, month: Int) -> String = { y, m -> "$y / $m" },
    /** Label for income */
    val incomeLabel: String = "Income",
    /** Label for expense */
    val expenseLabel: String = "Expense",
    /** Label shown above the calendar legend */
    val allTransactionsLabel: String = "All Transactions",
    /** Title for the total summary card */
    val summaryTitle: String = "Income & Expense Summary",
    /** Label for monthly net total */
    val monthlyTotalLabel: String = "Monthly Total",
    /** Weekday names starting from Monday. Must contain exactly 7 items. */
    val weekdayNames: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
)
