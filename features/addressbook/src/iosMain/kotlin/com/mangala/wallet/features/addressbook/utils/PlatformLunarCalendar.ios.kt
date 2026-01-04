package com.mangala.wallet.features.addressbook.utils

import com.mangala.wallet.features.addressbook.domain.model.LunarDate
import kotlinx.datetime.*

/**
 * iOS implementation using a simplified lunar calendar algorithm
 * Since iOS NSCalendar Chinese calendar support has limitations in KMP
 */
actual class PlatformLunarCalendar {
    
    actual fun solarToLunar(date: LocalDate): LunarDate {
        return AccurateLunarCalendar.convertSolar2Lunar(
            dd = date.dayOfMonth,
            mm = date.monthNumber,
            yy = date.year,
            timeZone = 7.0 // Vietnam timezone
        )
    }
    
    actual fun lunarToSolar(lunar: LunarDate): LocalDate {
        return AccurateLunarCalendar.convertLunar2Solar(
            lunarDay = lunar.day,
            lunarMonth = lunar.month,
            lunarYear = lunar.year,
            lunarLeap = lunar.isLeapMonth,
            timeZone = 7.0 // Vietnam timezone
        )
    }
    
    actual fun getLeapMonth(year: Int): Int? {
        return AccurateLunarCalendar.getLeapMonth(year, 7.0)
    }
    
    actual fun getLunarMonthDays(year: Int, month: Int, isLeapMonth: Boolean): Int {
        return AccurateLunarCalendar.getLunarMonthDays(year, month, isLeapMonth, 7.0)
    }
    
    actual fun getSolarTerm(date: LocalDate): String? {
        // Solar terms occur approximately every 15 days
        val dayOfYear = date.dayOfYear
        
        return when (dayOfYear) {
            in 5..19 -> "Tiểu hàn"
            in 20..34 -> "Đại hàn"
            in 35..49 -> "Lập xuân"
            in 50..64 -> "Vũ thủy"
            in 65..79 -> "Kinh trập"
            in 80..95 -> "Xuân phân"
            in 96..110 -> "Thanh minh"
            in 111..125 -> "Cốc vũ"
            in 126..140 -> "Lập hạ"
            in 141..155 -> "Tiểu mãn"
            in 156..171 -> "Mang chủng"
            in 172..187 -> "Hạ chí"
            in 188..202 -> "Tiểu thử"
            in 203..217 -> "Đại thử"
            in 218..232 -> "Lập thu"
            in 233..247 -> "Xử thử"
            in 248..262 -> "Bạch lộ"
            in 263..277 -> "Thu phân"
            in 278..292 -> "Hàn lộ"
            in 293..307 -> "Sương giáng"
            in 308..322 -> "Lập đông"
            in 323..337 -> "Tiểu tuyết"
            in 338..352 -> "Đại tuyết"
            in 353..365 -> "Đông chí"
            else -> null
        }
    }
    
    actual fun isValidLunarDate(lunar: LunarDate): Boolean {
        // Basic validation
        if (lunar.day !in 1..30 || lunar.month !in 1..12) {
            return false
        }
        
        // Check if the day is valid for this month
        val maxDays = getLunarMonthDays(lunar.year, lunar.month, lunar.isLeapMonth)
        if (lunar.day > maxDays) {
            return false
        }
        
        // Check if leap month is valid for this year
        if (lunar.isLeapMonth) {
            val leapMonth = getLeapMonth(lunar.year)
            if (leapMonth != lunar.month) {
                return false
            }
        }
        
        return true
    }
    
    actual fun getSupportedYearRange(): Pair<Int, Int> {
        return Pair(1900, 2100)
    }
}