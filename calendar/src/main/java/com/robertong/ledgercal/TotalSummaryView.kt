package com.robertong.ledgercal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robertong.ledgercal.theme.CalendarColors
import com.robertong.ledgercal.theme.CalendarStrings
import com.robertong.ledgercal.util.HeightSpacer
import com.robertong.ledgercal.util.WidthSpacer
import com.robertong.ledgercal.util.toBigDecimalSafely
import com.robertong.ledgercal.util.withoutComma
import java.math.BigDecimal

/**
 * Card showing total income, total expense, and the net monthly total.
 *
 * Amounts are color-coded: positive income is shown in [CalendarColors.incomeColor],
 * positive expense in [CalendarColors.expenseColor], and the net total changes color
 * based on its sign.
 *
 * @param totalIncome Formatted income string (e.g., "999,999").
 * @param totalExpense Formatted expense string (e.g., "123,456").
 * @param totalAmount Formatted net total string (e.g., "876,543" or "-876,543").
 * @param colors Color configuration.
 * @param strings String configuration.
 * @param icon Optional composable slot for a leading icon in the title row.
 */
@Composable
fun TotalSummaryView(
    totalIncome: String,
    totalExpense: String,
    totalAmount: String,
    colors: CalendarColors = CalendarColors(),
    strings: CalendarStrings = CalendarStrings(),
    icon: (@Composable () -> Unit)? = null
) {
    val roundShape = RoundedCornerShape(corner = CornerSize(12.dp))

    Box(Modifier.background(colors.surfaceColor)) {
        Card(
            modifier = Modifier
                .clip(roundShape)
                .padding(16.dp),
            backgroundColor = Color.White,
            shape = roundShape,
            elevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    icon?.invoke()

                    if (icon != null) {
                        WidthSpacer(width = 6.dp)
                    }

                    Text(
                        text = strings.summaryTitle,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = colors.textColor
                    )
                }

                HeightSpacer(height = 12.dp)

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Income column
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.incomeLabel,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = colors.textColor
                        )

                        HeightSpacer(height = 6.dp)

                        val totalIncomeInBigDecimal =
                            totalIncome.withoutComma().toBigDecimalSafely()
                        val totalIncomeCompared = totalIncomeInBigDecimal.compareTo(BigDecimal.ZERO)

                        Text(
                            text = totalIncome,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = when (totalIncomeCompared) {
                                0 -> colors.textColor
                                1 -> colors.incomeColor
                                else -> colors.textColor
                            }
                        )

                        HeightSpacer(height = 8.dp)
                    }

                    Spacer(
                        modifier = Modifier
                            .height(56.dp)
                            .width(1.dp)
                            .background(colors.dividerColor)
                    )

                    WidthSpacer(width = 12.dp)

                    // Expense column
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.expenseLabel,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = colors.textColor
                        )

                        HeightSpacer(height = 6.dp)

                        val totalExpenseInBigDecimal =
                            totalExpense.withoutComma().toBigDecimalSafely()
                        val totalExpenseCompared =
                            totalExpenseInBigDecimal.compareTo(BigDecimal.ZERO)

                        Text(
                            text = totalExpense,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = when (totalExpenseCompared) {
                                0 -> colors.textColor
                                1 -> colors.expenseColor
                                else -> colors.textColor
                            }
                        )

                        HeightSpacer(height = 8.dp)
                    }
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.dividerColor)
                )

                HeightSpacer(height = 18.dp)

                // Monthly total row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = strings.monthlyTotalLabel,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = colors.textColor
                    )

                    val totalAmountInBigDecimal =
                        totalAmount.withoutComma().toBigDecimalSafely()
                    val totalAmountCompared = totalAmountInBigDecimal.compareTo(BigDecimal.ZERO)

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = ParagraphStyle(lineHeight = 31.sp)
                            ) {
                                withStyle(
                                    style = SpanStyle(
                                        color = when (totalAmountCompared) {
                                            0 -> colors.textColor
                                            1 -> colors.incomeColor
                                            else -> colors.expenseColor
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                ) {
                                    append(
                                        when (totalAmountCompared) {
                                            0 -> ""
                                            1 -> "\uFF0B "
                                            else -> "\uFF0D "
                                        }
                                    )
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = when (totalAmountCompared) {
                                            0 -> colors.textColor
                                            1 -> colors.incomeColor
                                            else -> colors.expenseColor
                                        },
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 22.sp
                                    )
                                ) {
                                    append(
                                        when (totalAmountCompared) {
                                            0 -> totalAmount
                                            1 -> totalAmount
                                            else -> totalAmount.takeLast(
                                                (totalAmount.length - 1).coerceAtLeast(0)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
