package com.robertong.ledgercal.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun String.withComma(): String {
    return if (this.contains(".")) {
        addComma(this.substringBefore(".")).plus(".").plus(this.substringAfter("."))
    } else {
        addComma(this)
    }
}

fun String.withoutComma(): String = this.replace(",", "")

fun String.without(string: String): String = this.replace(string, "")

fun String.toBigDecimalSafely(): BigDecimal {
    return try {
        BigDecimal(this.trim().withoutComma())
    } catch (e: NumberFormatException) {
        BigDecimal.ZERO
    }
}

fun emptyString(): String = ""

fun oneZero(): String = "0"

fun spaceString(count: Int): String = " ".repeat(count)

fun dashString(count: Int): String = "-".repeat(count)

private fun addComma(numberString: String): String {
    return try {
        val number = BigDecimal(numberString.trim())
        val isNegative = number < BigDecimal.ZERO
        val absFormatted = NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = 0
            isGroupingUsed = true
        }.format(number.abs())
        if (isNegative) "-$absFormatted" else absFormatted
    } catch (e: Exception) {
        numberString
    }
}
