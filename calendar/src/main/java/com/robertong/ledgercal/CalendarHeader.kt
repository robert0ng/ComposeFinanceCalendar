@file:OptIn(ExperimentalLayoutApi::class)

package com.robertong.ledgercal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robertong.ledgercal.theme.CalendarColors
import com.robertong.ledgercal.theme.CalendarStrings
import com.robertong.ledgercal.util.HeightSpacer
import com.robertong.ledgercal.util.WidthSpacer
import com.robertong.ledgercal.util.toLocalDate

/**
 * Displays a calendar header with year/month title and income/expense legend.
 *
 * @param range Epoch millis range representing the displayed month.
 * @param colors Color configuration. Defaults to [CalendarColors].
 * @param strings String configuration. Defaults to [CalendarStrings].
 * @param icon Optional composable slot for a leading icon.
 */
@Composable
fun CalendarHeader(
    range: LongRange,
    colors: CalendarColors = CalendarColors(),
    strings: CalendarStrings = CalendarStrings(),
    icon: (@Composable () -> Unit)? = null
) {
    val monthTitle = range.first.toLocalDate()

    Column(
        Modifier
            .fillMaxWidth()
            .background(colors.backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.invoke()

            if (icon != null) {
                WidthSpacer(width = 6.dp)
            }

            Text(
                text = strings.headerTitle(monthTitle.year, monthTitle.monthValue),
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                color = colors.textColor
            )
        }

        HeightSpacer(height = 4.dp)

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = strings.allTransactionsLabel,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = colors.textColor
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(modifier = Modifier.size(12.dp), onDraw = {
                    drawCircle(color = colors.incomeColor)
                })

                WidthSpacer(width = 4.dp)

                Text(
                    text = strings.incomeLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = colors.incomeColor
                )

                WidthSpacer(width = 6.dp)

                Canvas(modifier = Modifier.size(12.dp), onDraw = {
                    drawCircle(color = colors.expenseColor)
                })

                WidthSpacer(width = 4.dp)

                Text(
                    text = strings.expenseLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = colors.expenseColor
                )
            }
        }
    }
}
