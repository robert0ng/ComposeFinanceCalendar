package com.robertong.ledgercal.theme

import androidx.compose.ui.graphics.Color

/**
 * Color configuration for the finance calendar components.
 *
 * All colors have sensible defaults and can be overridden individually.
 */
data class CalendarColors(
    /** Color for income amounts and indicators */
    val incomeColor: Color = Color(0xFF238CBE),
    /** Color for expense amounts and indicators */
    val expenseColor: Color = Color(0xFFD4474E),
    /** Background color for today's date circle */
    val todayHighlightColor: Color = Color(0xFFFEE500),
    /** Primary text color */
    val textColor: Color = Color(0xFF1F1F1F),
    /** Secondary/label text color */
    val secondaryTextColor: Color = Color(0xFF666666),
    /** Divider line color */
    val dividerColor: Color = Color(0xFFEBEDEE),
    /** Card and cell background color */
    val backgroundColor: Color = Color.White,
    /** Screen surface/page background color */
    val surfaceColor: Color = Color(0xFFEFF3F5),
    /** Selected tag background color */
    val selectedTagColor: Color = Color(0xFFEEEEEE),
    /** Tag border color */
    val tagBorderColor: Color = Color(0xFFE0E0E0),
    /** Unselected tag text color */
    val unselectedTagTextColor: Color = Color(0xFF989898),
    /** Selected tag text color */
    val selectedTagTextColor: Color = Color(0xFF4F4F4F)
)
