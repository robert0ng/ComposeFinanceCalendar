package com.robertong.ledgercal.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import com.robertong.ledgercal.CalendarScreen
import com.robertong.ledgercal.theme.CalendarStrings
import java.time.DayOfWeek

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startOfWeek = DayOfWeek.MONDAY
        val calendarDays = getSampleCalendarDays(startOfWeek)
        val range = getSampleRange()

        setContent {
            CalendarScreen(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                dateRange = range,
                calendarDays = calendarDays,
                totalIncome = "999,999",
                totalExpense = "123,456",
                totalAmount = "876,543",
                startOfWeek = startOfWeek,
                strings = CalendarStrings(
                    headerTitle = { y, m -> "$y / $m" }
                ),
                onDayClicked = { day ->
                    // Handle day click
                }
            )
        }
    }
}
