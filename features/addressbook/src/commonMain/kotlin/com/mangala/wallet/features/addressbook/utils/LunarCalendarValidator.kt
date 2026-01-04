package com.mangala.wallet.features.addressbook.utils

import com.mangala.wallet.features.addressbook.domain.model.LunarDate
import kotlinx.datetime.LocalDate

/**
 * Validator for lunar calendar dates and conversions
 */
object LunarCalendarValidator {
    
    /**
     * Validate if a lunar date is valid
     */
    fun isValidLunarDate(lunarDate: LunarDate): Boolean {
        // Basic range checks
        if (lunarDate.year !in 1900..2100) return false
        if (lunarDate.month !in 1..12) return false
        if (lunarDate.day !in 1..30) return false
        
        // Check if leap month is valid for this year
        if (lunarDate.isLeapMonth) {
            val leapMonth = AccurateLunarCalendar.getLeapMonth(lunarDate.year)
            if (leapMonth != lunarDate.month) return false
        }
        
        // Check if day is valid for this month
        val maxDays = AccurateLunarCalendar.getLunarMonthDays(
            lunarDate.year, 
            lunarDate.month, 
            lunarDate.isLeapMonth
        )
        if (lunarDate.day > maxDays) return false
        
        return true
    }
    
    /**
     * Validate if a solar date is within supported range
     */
    fun isValidSolarDate(solarDate: LocalDate): Boolean {
        return solarDate.year in 1900..2100
    }
    
    /**
     * Handle edge cases for lunar month 30/31
     */
    fun adjustLunarDate(lunarDate: LunarDate): LunarDate {
        val maxDays = AccurateLunarCalendar.getLunarMonthDays(
            lunarDate.year,
            lunarDate.month,
            lunarDate.isLeapMonth
        )
        
        return if (lunarDate.day > maxDays) {
            lunarDate.copy(day = maxDays)
        } else {
            lunarDate
        }
    }
    
    /**
     * Get error message in Vietnamese
     */
    fun getErrorMessage(error: LunarCalendarError): String {
        return when (error) {
            is LunarCalendarError.InvalidYear -> 
                "Năm không hợp lệ. Chỉ hỗ trợ từ năm 1900 đến 2100."
            is LunarCalendarError.InvalidMonth -> 
                "Tháng không hợp lệ. Tháng phải từ 1 đến 12."
            is LunarCalendarError.InvalidDay -> 
                "Ngày không hợp lệ. Ngày phải từ 1 đến ${error.maxDays}."
            is LunarCalendarError.InvalidLeapMonth -> 
                "Tháng nhuận không hợp lệ. Năm ${error.year} không có tháng ${error.month} nhuận."
            is LunarCalendarError.ConversionError -> 
                "Không thể chuyển đổi ngày. Vui lòng kiểm tra lại."
        }
    }
}

/**
 * Sealed class for lunar calendar errors
 */
sealed class LunarCalendarError {
    data class InvalidYear(val year: Int) : LunarCalendarError()
    data class InvalidMonth(val month: Int) : LunarCalendarError()
    data class InvalidDay(val day: Int, val maxDays: Int) : LunarCalendarError()
    data class InvalidLeapMonth(val year: Int, val month: Int) : LunarCalendarError()
    data class ConversionError(val message: String) : LunarCalendarError()
}