package com.mangala.wallet.features.addressbook.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Represents an important date associated with a contact
 * Supports both solar (Gregorian) and lunar (Vietnamese) calendar types
 */
@Serializable
data class ImportantDate(
    val id: String,
    val title: String,
    val date: LocalDate,
    val calendarType: CalendarType = CalendarType.SOLAR,
    val lunarDate: LunarDate? = null,
    val category: ImportantDateCategory = ImportantDateCategory.OTHER,
    val notes: String = ""
)

/**
 * Type of calendar system used for the date
 */
enum class CalendarType {
    SOLAR,  // Gregorian/Western calendar
    LUNAR   // Vietnamese lunar calendar
}

/**
 * Categories for important dates
 */
enum class ImportantDateCategory {
    BIRTHDAY, ANNIVERSARY, HOLIDAY, BUSINESS, OTHER
}

/**
 * Represents a Vietnamese lunar calendar date
 */
@Serializable
data class LunarDate(
    val day: Int,           // 1-30
    val month: Int,         // 1-12
    val year: Int,          // Lunar year
    val isLeapMonth: Boolean = false,
    val yearCycle: String   // "Giáp Tý", "Ất Sửu", etc.
) {
    /**
     * Formatted display name for the lunar date
     */
    val displayName: String
        get() = "${day}/${month}${if (isLeapMonth) " (nhuận)" else ""}/${yearCycle}"
    
    /**
     * Short display name for calendar cells
     */
    val shortDisplayName: String
        get() = "${month}${if (isLeapMonth) "N" else ""}"
}

/**
 * Represents calendar context for a specific month/year
 * Used to determine styling and behavior based on leap month status
 */
@Serializable
data class CalendarContext(
    val month: Int,         // 1-12
    val year: Int,          // Lunar year
    val isLeapMonth: Boolean = false,
    val hasLeapMonth: Boolean = false,  // Year has a leap month
    val leapMonthNumber: Int? = null    // Which month is leap (if any)
) {
    /**
     * Display name for the lunar month
     */
    val lunarMonthDisplayName: String
        get() = when {
            month == 1 -> if (isLeapMonth) "Tháng Giêng Nhuận" else "Tháng Giêng"
            month == 12 -> if (isLeapMonth) "Tháng Chạp Nhuận" else "Tháng Chạp"
            else -> if (isLeapMonth) "Tháng $month Nhuận" else "Tháng $month"
        }
    
    /**
     * Whether this context represents a leap month
     */
    val isInLeapMonth: Boolean
        get() = isLeapMonth
}
