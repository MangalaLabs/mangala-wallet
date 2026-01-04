package com.mangala.wallet.features.addressbook.presentation.components.calendar

import com.mangala.wallet.features.addressbook.domain.model.ImportantDateCategory
import kotlinx.datetime.LocalDate

/**
 * Helper functions for lunar calendar UI
 */

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Get lunar day display name with adaptive sizing for different screen widths
 */
fun getLunarDayDisplayName(day: Int, screenWidth: Dp? = null): String {
    // If screen width is provided and very small, use abbreviated versions
    if (screenWidth != null && screenWidth < 360.dp) {
        return when (day) {
            1 -> "M1"
            2 -> "M2"
            3 -> "M3"
            4 -> "M4"
            5 -> "M5"
            6 -> "M6"
            7 -> "M7"
            8 -> "M8"
            9 -> "M9"
            10 -> "M10"
            15 -> "Rằm"
            30 -> "30"
            else -> day.toString()
        }
    }
    
    // Default full display names
    return getLunarDayDisplayNameDefault(day)
}

/**
 * Default lunar day display name (full version)
 */
fun getLunarDayDisplayNameDefault(day: Int): String {
    return when (day) {
        1 -> "Mồng 1"
        2 -> "Mồng 2"
        3 -> "Mồng 3"
        4 -> "Mồng 4"
        5 -> "Mồng 5"
        6 -> "Mồng 6"
        7 -> "Mồng 7"
        8 -> "Mồng 8"
        9 -> "Mồng 9"
        10 -> "Mồng 10"
        15 -> "Rằm"
        30 -> "30"
        else -> day.toString()
    }
}


fun getLunarDayName(day: Int): String {
    return when (day) {
        1 -> "Mồng 1"
        2 -> "Mồng 2"
        3 -> "Mồng 3"
        4 -> "Mồng 4"
        5 -> "Mồng 5"
        6 -> "Mồng 6"
        7 -> "Mồng 7"
        8 -> "Mồng 8"
        9 -> "Mồng 9"
        10 -> "Mồng 10"
        15 -> "Rằm"
        else -> day.toString()
    }
}

fun getSolarTerm(date: LocalDate): String {
    val solarTerms = listOf(
        "Tiểu hàn", "Đại hàn", "Lập xuân", "Vũ thủy", "Kinh trập", "Xuân phân",
        "Thanh minh", "Cốc vũ", "Lập hạ", "Tiểu mãn", "Mang chủng", "Hạ chí",
        "Tiểu thử", "Đại thử", "Lập thu", "Xử thử", "Bạch lộ", "Thu phân",
        "Hàn lộ", "Sương giáng", "Lập đông", "Tiểu tuyết", "Đại tuyết", "Đông chí"
    )
    
    val termIndex = ((date.monthNumber - 1) * 2) + (if (date.dayOfMonth > 15) 1 else 0)
    return solarTerms[termIndex.rem(24)]
}

fun getCategoryLabel(category: ImportantDateCategory): String {
    return when (category) {
        ImportantDateCategory.BIRTHDAY -> "Sinh nhật"
        ImportantDateCategory.ANNIVERSARY -> "Kỷ niệm"
        ImportantDateCategory.HOLIDAY -> "Ngày lễ"
        ImportantDateCategory.BUSINESS -> "Công việc"
        ImportantDateCategory.OTHER -> "Khác"
    }
}