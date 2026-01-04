package com.mangala.wallet.features.addressbook.domain.model

import kotlinx.datetime.LocalDate

/**
 * Represents a holiday in the Vietnamese calendar
 */
data class LunarHoliday(
    val name: String,
    val nameFull: String,
    val description: String? = null,
    val lunarMonth: Int,
    val lunarDay: Int,
    val isLeapMonth: Boolean = false,
    val type: HolidayType,
    val importance: HolidayImportance
)

enum class HolidayType {
    TRADITIONAL,     // Traditional Vietnamese holidays
    RELIGIOUS,       // Buddhist/religious holidays
    AGRICULTURAL,    // Agricultural/seasonal holidays
    COMMEMORATIVE    // Commemorative days
}

enum class HolidayImportance {
    MAJOR,     // National holidays (Tết, Mid-Autumn Festival, etc.)
    MODERATE,  // Important but not national holidays
    MINOR      // Lesser known or regional holidays
}

/**
 * Data class representing a specific holiday occurrence with solar date
 */
data class HolidayOccurrence(
    val holiday: LunarHoliday,
    val solarDate: LocalDate,
    val lunarDate: LunarDate
)