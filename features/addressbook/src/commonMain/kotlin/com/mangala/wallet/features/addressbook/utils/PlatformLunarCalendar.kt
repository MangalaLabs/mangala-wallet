package com.mangala.wallet.features.addressbook.utils

import com.mangala.wallet.features.addressbook.domain.model.LunarDate
import kotlinx.datetime.LocalDate

/**
 * Platform-specific lunar calendar implementation interface
 * Now uses accurate astronomical algorithm for all platforms
 */
expect class PlatformLunarCalendar() {
    /**
     * Convert solar date to lunar date
     * @param date Solar date to convert
     * @return Lunar date with year cycle information
     */
    fun solarToLunar(date: LocalDate): LunarDate
    
    /**
     * Convert lunar date to solar date
     * @param lunar Lunar date to convert
     * @return Solar date
     */
    fun lunarToSolar(lunar: LunarDate): LocalDate
    
    /**
     * Check if a specific year has a leap month
     * @param year Lunar year to check
     * @return Leap month number (1-12) or null if no leap month
     */
    fun getLeapMonth(year: Int): Int?
    
    /**
     * Calculate the number of days in a lunar month
     * @param year Lunar year
     * @param month Lunar month (1-12)
     * @param isLeapMonth Whether this is a leap month
     * @return Number of days (29 or 30)
     */
    fun getLunarMonthDays(year: Int, month: Int, isLeapMonth: Boolean): Int
    
    /**
     * Get solar terms (24 tiết khí) for a given date
     * @param date Solar date
     * @return Solar term name in Vietnamese or null if not a solar term date
     */
    fun getSolarTerm(date: LocalDate): String?
    
    /**
     * Validate if a lunar date is valid
     * @param lunar Lunar date to validate
     * @return true if valid, false otherwise
     */
    fun isValidLunarDate(lunar: LunarDate): Boolean
    
    /**
     * Get supported year range for this implementation
     * @return Pair of (startYear, endYear)
     */
    fun getSupportedYearRange(): Pair<Int, Int>
}