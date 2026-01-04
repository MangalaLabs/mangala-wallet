package com.mangala.wallet.features.addressbook.utils

import com.mangala.wallet.features.addressbook.domain.model.LunarDate
import com.mangala.wallet.features.addressbook.domain.model.CalendarContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.minus
import kotlinx.datetime.Month

/**
 * Utility for converting between Vietnamese lunar calendar and Gregorian calendar
 * Enhanced with proper leap month support based on Vietnamese lunar calendar data
 */
object LunarCalendarConverter {
    private val cache = mutableMapOf<LocalDate, LunarDate>()
    private val contextCache = mutableMapOf<Pair<Int, Int>, CalendarContext>()
    
    /**
     * Lunar calendar data with leap month information
     * Based on astronomical calculations for Vietnamese lunar calendar
     */
    private val lunarYearData = mapOf(
        2023 to LunarYearInfo(
            leapMonth = 2,
            months = listOf(
                LunarMonthInfo(1, "2023-01-22", false),
                LunarMonthInfo(2, "2023-02-20", false),
                LunarMonthInfo(2, "2023-03-22", true), // Tháng 2 nhuận
                LunarMonthInfo(3, "2023-04-20", false),
                LunarMonthInfo(4, "2023-05-19", false),
                LunarMonthInfo(5, "2023-06-18", false),
                LunarMonthInfo(6, "2023-07-17", false),
                LunarMonthInfo(7, "2023-08-16", false),
                LunarMonthInfo(8, "2023-09-15", false),
                LunarMonthInfo(9, "2023-10-15", false),
                LunarMonthInfo(10, "2023-11-13", false),
                LunarMonthInfo(11, "2023-12-13", false),
                LunarMonthInfo(12, "2024-01-11", false)
            )
        ),
        2024 to LunarYearInfo(
            leapMonth = null,
            months = listOf(
                LunarMonthInfo(1, "2024-02-10", false),
                LunarMonthInfo(2, "2024-03-10", false),
                LunarMonthInfo(3, "2024-04-09", false),
                LunarMonthInfo(4, "2024-05-08", false),
                LunarMonthInfo(5, "2024-06-06", false),
                LunarMonthInfo(6, "2024-07-06", false),
                LunarMonthInfo(7, "2024-08-04", false),
                LunarMonthInfo(8, "2024-09-03", false),
                LunarMonthInfo(9, "2024-10-03", false),
                LunarMonthInfo(10, "2024-11-01", false),
                LunarMonthInfo(11, "2024-12-01", false),
                LunarMonthInfo(12, "2024-12-31", false)
            )
        ),
        2025 to LunarYearInfo(
            leapMonth = 6,
            months = listOf(
                LunarMonthInfo(1, "2025-01-29", false),
                LunarMonthInfo(2, "2025-02-28", false),
                LunarMonthInfo(3, "2025-03-29", false),
                LunarMonthInfo(4, "2025-04-28", false),
                LunarMonthInfo(5, "2025-05-27", false),
                LunarMonthInfo(6, "2025-06-26", false),
                LunarMonthInfo(6, "2025-07-25", true), // Tháng 6 nhuận
                LunarMonthInfo(7, "2025-08-24", false),
                LunarMonthInfo(8, "2025-09-22", false),
                LunarMonthInfo(9, "2025-10-21", false),
                LunarMonthInfo(10, "2025-11-20", false),
                LunarMonthInfo(11, "2025-12-19", false),
                LunarMonthInfo(12, "2026-01-17", false)
            )
        ),
        2026 to LunarYearInfo(
            leapMonth = null,
            months = listOf(
                LunarMonthInfo(1, "2026-02-17", false),
                LunarMonthInfo(2, "2026-03-19", false),
                LunarMonthInfo(3, "2026-04-17", false),
                LunarMonthInfo(4, "2026-05-16", false),
                LunarMonthInfo(5, "2026-06-15", false),
                LunarMonthInfo(6, "2026-07-14", false),
                LunarMonthInfo(7, "2026-08-13", false),
                LunarMonthInfo(8, "2026-09-11", false),
                LunarMonthInfo(9, "2026-10-11", false),
                LunarMonthInfo(10, "2026-11-09", false),
                LunarMonthInfo(11, "2026-12-09", false),
                LunarMonthInfo(12, "2027-01-08", false)
            )
        )
    )
    
    data class LunarYearInfo(
        val leapMonth: Int?,
        val months: List<LunarMonthInfo>
    )
    
    data class LunarMonthInfo(
        val month: Int,
        val startDate: String,
        val isLeap: Boolean
    )

    /**
     * Convert solar date to Vietnamese lunar date
     */
    fun toSolar(lunarDate: LunarDate): LocalDate {
        return calculateSolarDate(lunarDate)
    }

    /**
     * Convert Vietnamese lunar date to solar date
     */
    fun toLunar(solarDate: LocalDate): LunarDate {
        return cache.getOrPut(solarDate) {
            calculateLunarDate(solarDate)
        }
    }
    
    /**
     * Get calendar context for a specific month/year
     * This provides leap month information for UI styling
     */
    fun getCalendarContext(month: Int, year: Int, isLeapMonth: Boolean = false): CalendarContext {
        return contextCache.getOrPut(Pair(month, year)) {
            val yearData = lunarYearData[year]
            CalendarContext(
                month = month,
                year = year,
                isLeapMonth = isLeapMonth,
                hasLeapMonth = yearData?.leapMonth != null,
                leapMonthNumber = yearData?.leapMonth
            )
        }
    }
    
    /**
     * Check if a specific year/month combination is a leap month
     */
    fun isLeapMonth(year: Int, month: Int): Boolean {
        val yearData = lunarYearData[year] ?: return false
        return yearData.leapMonth == month
    }
    
    /**
     * Find the lunar month info for a given solar date
     */
    fun findLunarMonthInfo(solarDate: LocalDate): LunarMonthInfo? {
        val year = solarDate.year
        val yearData = lunarYearData[year] ?: return null
        
        // Find which lunar month this solar date falls into
        for (i in yearData.months.indices) {
            val currentMonth = yearData.months[i]
            val nextMonth = yearData.months.getOrNull(i + 1)
            
            val startDate = LocalDate.parse(currentMonth.startDate)
            val endDate = nextMonth?.let { LocalDate.parse(it.startDate).minus(DatePeriod(days = 1)) } 
                         ?: LocalDate(year + 1, 2, 10) // Approximate end if no next month
            
            if (solarDate >= startDate && solarDate <= endDate) {
                return currentMonth
            }
        }
        
        return null
    }

    /**
     * Calculate Vietnamese year cycle (Can Chi)
     */
    fun calculateYearCycle(lunarYear: Int): String {
        val heavenly = arrayOf("Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý")
        val earthly = arrayOf("Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi")

        val heavenlyIndex = (lunarYear - 4) % 10
        val earthlyIndex = (lunarYear - 4) % 12

        return "${heavenly[heavenlyIndex]} ${earthly[earthlyIndex]}"
    }

    /**
     * Validate if a lunar date is valid
     */
    fun isValidLunarDate(lunarDate: LunarDate): Boolean {
        return lunarDate.day in 1..30 &&
               lunarDate.month in 1..12 &&
               lunarDate.year > 1900
    }
    
    /**
     * Check if a specific year has any leap month
     */
    fun hasLeapMonth(year: Int): Boolean {
        return lunarYearData[year]?.leapMonth != null
    }
    
    /**
     * Get leap month number for a specific year
     */
    fun getLeapMonth(year: Int): Int? {
        return lunarYearData[year]?.leapMonth
    }
    
    /**
     * Get calendar context for current displayed month/year
     * This determines if we're in a leap month and provides styling context
     */
    fun getCalendarContextForDate(solarDate: LocalDate): CalendarContext {
        val lunarDate = toLunar(solarDate)
        return CalendarContext(
            month = lunarDate.month,
            year = lunarDate.year,
            isLeapMonth = lunarDate.isLeapMonth,
            hasLeapMonth = hasLeapMonth(lunarDate.year),
            leapMonthNumber = getLeapMonth(lunarDate.year)
        )
    }

    // Private implementation methods
    private fun calculateSolarDate(lunarDate: LunarDate): LocalDate {
        // Simplified implementation - replace with proper Vietnamese lunar calendar algorithm
        val baseYear = 2025
        val baseSolarNewYear = LocalDate(2025, 1, 29) // Tết Ất Tỵ 2025

        val yearDiff = lunarDate.year - baseYear
        val daysToAdd = ((yearDiff * 365.25).toInt() +
            ((lunarDate.month - 1) * 29.5).toInt() +
            lunarDate.day - 1)
        
        // Simple approach using epoch days calculation
        val estimatedSolar = try {
            val baseEpochDays = baseSolarNewYear.toEpochDays()
            val targetEpochDays = baseEpochDays + daysToAdd
            kotlinx.datetime.LocalDate.fromEpochDays(targetEpochDays)
        } catch (e: Exception) {
            // Fallback to base date if calculation fails
            baseSolarNewYear
        }

        return estimatedSolar
    }

    private fun calculateLunarDate(solarDate: LocalDate): LunarDate {
        // Enhanced implementation using lunar calendar data
        val monthInfo = findLunarMonthInfo(solarDate)
        
        if (monthInfo != null) {
            val startDate = LocalDate.parse(monthInfo.startDate)
            val daysDiff = solarDate.toEpochDays() - startDate.toEpochDays()
            val lunarDay = (daysDiff + 1).toInt().coerceIn(1, 30)
            
            return LunarDate(
                day = lunarDay,
                month = monthInfo.month,
                year = solarDate.year,
                isLeapMonth = monthInfo.isLeap,
                yearCycle = calculateYearCycle(solarDate.year)
            )
        }
        
        // Fallback to simplified calculation
        val baseYear = 2025
        val baseSolarNewYear = LocalDate(2025, 1, 29) // Tết Ất Tỵ 2025

        val daysDiff = solarDate.toEpochDays() - baseSolarNewYear.toEpochDays()
        val lunarYear = baseYear + (daysDiff / 365.25).toInt()
        val remainingDays = daysDiff % 365.25
        val lunarMonth = ((remainingDays / 29.5).toInt() % 12) + 1
        val lunarDay = (remainingDays % 29.5).toInt() + 1

        return LunarDate(
            day = lunarDay.coerceIn(1, 30),
            month = lunarMonth.coerceIn(1, 12),
            year = lunarYear,
            isLeapMonth = false,
            yearCycle = calculateYearCycle(lunarYear)
        )
    }
}
