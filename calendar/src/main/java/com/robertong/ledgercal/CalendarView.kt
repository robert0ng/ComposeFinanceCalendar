package com.robertong.ledgercal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.tooling.preview.Preview
import com.robertong.ledgercal.model.CalendarDay
import com.robertong.ledgercal.theme.CalendarColors
import com.robertong.ledgercal.theme.CalendarStrings
import com.robertong.ledgercal.util.AutoSizeText
import com.robertong.ledgercal.util.HeightSpacer
import com.robertong.ledgercal.util.bounceClick
import com.robertong.ledgercal.util.toCalendarDays
import com.robertong.ledgercal.util.toLong
import com.robertong.ledgercal.util.withComma
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

/**
 * Renders a monthly calendar grid with 7 columns.
 *
 * Each day cell displays the day number and optional income/expense amounts.
 * Today's date is highlighted with a colored circle.
 *
 * @param calendarDays List of [CalendarDay] cells (including padding cells where [CalendarDay.isInRange] is false).
 * @param startOfWeek The day the week starts on. Used to reorder weekday headers.
 * @param colors Color configuration.
 * @param strings String configuration (provides weekday names).
 * @param onCalendarDayClicked Callback when a day cell is tapped.
 */
@Composable
fun CalendarView(
    calendarDays: List<CalendarDay>,
    startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    colors: CalendarColors = CalendarColors(),
    strings: CalendarStrings = CalendarStrings(),
    onCalendarDayClicked: (CalendarDay) -> Unit = {}
) {
    val weekdays = strings.weekdayNames

    val alteredNames = if (startOfWeek == DayOfWeek.MONDAY) {
        weekdays
    } else {
        val offset = startOfWeek.value - 1
        weekdays.subList(offset, weekdays.size) + weekdays.subList(0, offset)
    }

    val bottomRoundedShape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)

    Column(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .clip(bottomRoundedShape)
            .background(colors.backgroundColor)
    ) {
        // Weekday headers row
        Row(modifier = Modifier.fillMaxWidth()) {
            alteredNames.forEach { weekday ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    WeekDayCell(weekday = weekday, colors = colors)
                }
            }
        }

        // Calendar day grid — 7 items per row
        calendarDays.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { calendarDay ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        CalendarDayCell(
                            calendarDay = calendarDay,
                            colors = colors,
                            onCalendarDayClicked = onCalendarDayClicked
                        )
                    }
                }
                // Pad remaining slots if last week is incomplete
                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// region Previews

private fun previewMonthDays(
    year: Int,
    month: Month,
    startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    withData: Boolean = true
): List<CalendarDay> {
    val ym = YearMonth.of(year, month)
    val range = ym.atDay(1).toLong()..ym.atEndOfMonth().toLong()

    val dayData = if (withData) {
        val map = mutableMapOf<LocalDate, Pair<String, String>>()
        var date = ym.atDay(1)
        while (!date.isAfter(ym.atEndOfMonth())) {
            val income = if (date.dayOfMonth % 3 == 0) {
                (100..9999).random().toString().withComma()
            } else ""
            val expense = if (date.dayOfMonth % 4 == 0) {
                (100..9999).random().toString().withComma()
            } else ""
            map[date] = Pair(income, expense)
            date = date.plusDays(1)
        }
        map
    } else emptyMap()

    return toCalendarDays(range = range, startOfWeek = startOfWeek, dayData = dayData)
}

@Composable
private fun YearGrid(
    year: Int = 2026,
    startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    strings: CalendarStrings = CalendarStrings(),
    withData: Boolean = true
) {
    Column(
        modifier = Modifier
            .background(CalendarColors().surfaceColor)
            .padding(4.dp)
    ) {
        Month.entries.chunked(3).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { month ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "${month.name.take(3)} $year",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                        )
                        CalendarView(
                            calendarDays = previewMonthDays(year, month, startOfWeek, withData),
                            startOfWeek = startOfWeek,
                            strings = strings,
                            onCalendarDayClicked = {}
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Full Year 2026 (3-col)",
    widthDp = 900,
    heightDp = 2000,
    showBackground = true
)
@Composable
private fun CalendarViewFullYearPreview() {
    YearGrid()
}

@Preview(
    name = "Full Year - Sunday Start (3-col)",
    widthDp = 900,
    heightDp = 2000,
    showBackground = true
)
@Composable
private fun CalendarViewFullYearSundayPreview() {
    YearGrid(
        startOfWeek = DayOfWeek.SUNDAY,
        strings = CalendarStrings(
            weekdayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        )
    )
}

@Preview(
    name = "Full Year - Empty (3-col)",
    widthDp = 900,
    heightDp = 2000,
    showBackground = true
)
@Composable
private fun CalendarViewFullYearEmptyPreview() {
    YearGrid(withData = false)
}

// endregion

@Composable
private fun WeekDayCell(
    weekday: String,
    colors: CalendarColors
) {
    Text(
        modifier = Modifier.padding(vertical = 8.dp),
        text = weekday,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        color = colors.secondaryTextColor,
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}

@Composable
private fun CalendarDayCell(
    calendarDay: CalendarDay,
    colors: CalendarColors,
    onCalendarDayClicked: (CalendarDay) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(modifier = Modifier.fillMaxWidth(), color = colors.dividerColor, thickness = 1.dp)

        if (calendarDay.isInRange) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 7.dp)
                    .bounceClick(clickAction = {
                        onCalendarDayClicked(calendarDay)
                    })
            ) {
                Box(
                    modifier = Modifier.requiredSize(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (calendarDay.isToday) {
                        Canvas(modifier = Modifier.size(28.dp), onDraw = {
                            drawCircle(color = colors.todayHighlightColor)
                        })
                    }

                    Text(
                        text = "${calendarDay.date.dayOfMonth}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textColor,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }

                HeightSpacer(height = 4.dp)

                val incomeContent = calendarDay.income.takeIf { it != "0" && it.isNotEmpty() } ?: ""
                val expenseContent = calendarDay.expense.takeIf { it != "0" && it.isNotEmpty() } ?: ""

                AutoSizeText(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    maxLines = if (expenseContent.isNotEmpty()) 2 else 1,
                    minTextSize = TextUnit(3f, TextUnitType.Sp),
                    maxTextSize = TextUnit(10f, TextUnitType.Sp),
                    stepGranularityTextSize = TextUnit(0.2f, TextUnitType.Sp),
                    alignment = Alignment.Center,
                    text = buildAnnotatedString {
                        withStyle(
                            style = ParagraphStyle(
                                lineHeight = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        ) {
                            withStyle(
                                style = SpanStyle(
                                    color = colors.incomeColor,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("${incomeContent}${if (expenseContent.isNotEmpty()) "\n" else ""}")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = colors.expenseColor,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(expenseContent)
                            }
                        }
                    }
                )
            }
        }
    }
}
