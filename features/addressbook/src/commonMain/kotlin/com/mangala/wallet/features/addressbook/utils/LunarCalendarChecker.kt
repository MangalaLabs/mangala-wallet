package com.mangala.wallet.features.addressbook.utils

import kotlinx.datetime.LocalDate

/**
 * Utility to check lunar calendar accuracy
 */
object LunarCalendarChecker {
    
    fun checkAccuracy(): String {
        val result = StringBuilder()
        
        // Known accurate dates from Vietnamese calendar
        val testDates = listOf(
            // Tết 2024 - Giáp Thìn
            TestDate(
                solar = LocalDate(2024, 2, 10),
                expectedLunar = "1/1/2024",
                description = "Tết Giáp Thìn 2024"
            ),
            // Rằm tháng Giêng 2024
            TestDate(
                solar = LocalDate(2024, 2, 24),
                expectedLunar = "15/1/2024",
                description = "Rằm tháng Giêng 2024"
            ),
            // Phật Đản 2024 (8/4 âm lịch)
            TestDate(
                solar = LocalDate(2024, 5, 15),
                expectedLunar = "8/4/2024",
                description = "Phật Đản 2024"
            ),
            // Tết Trung Thu 2024
            TestDate(
                solar = LocalDate(2024, 9, 17),
                expectedLunar = "15/8/2024",
                description = "Tết Trung Thu 2024"
            ),
            // Tết 2025 - Ất Tỵ
            TestDate(
                solar = LocalDate(2025, 1, 29),
                expectedLunar = "1/1/2025",
                description = "Tết Ất Tỵ 2025"
            ),
            // Today's date
            TestDate(
                solar = LocalDate(2025, 1, 6),
                expectedLunar = "7/12/2024",
                description = "Hôm nay 6/1/2025"
            )
        )
        
        result.appendLine("=== KIỂM TRA ĐỘ CHÍNH XÁC LỊCH ÂM ===\n")
        
        testDates.forEach { test ->
            val lunar = LunarCalendarConverter.toLunar(test.solar)
            val actualLunar = "${lunar.day}/${lunar.month}/${lunar.year}"
            val isCorrect = actualLunar == test.expectedLunar
            
            // Test direct calculation to debug
            val directLunar = AccurateLunarCalendar.convertSolar2Lunar(
                test.solar.dayOfMonth,
                test.solar.monthNumber,
                test.solar.year,
                7.0 // Vietnam timezone
            )
            val directLunarStr = "${directLunar.day}/${directLunar.month}/${directLunar.year}"
            
            result.appendLine("${test.description}:")
            result.appendLine("  Dương lịch: ${test.solar}")
            result.appendLine("  Âm lịch mong đợi: ${test.expectedLunar}")
            result.appendLine("  Âm lịch tính được: $actualLunar")
            result.appendLine("  Direct calculation: $directLunarStr")
            result.appendLine("  Năm can chi: ${lunar.yearCycle}")
            result.appendLine("  Kết quả: ${if (isCorrect) "✅ ĐÚNG" else "❌ SAI"}")
            
            if (lunar.isLeapMonth) {
                result.appendLine("  ⚠️ Tháng nhuận")
            }
            
            // Check holidays on this date
            val holidays = VietnameseHolidays.getHolidaysForDate(test.solar)
            if (holidays.isNotEmpty()) {
                result.appendLine("  🎉 Ngày lễ:")
                holidays.forEach { holiday ->
                    result.appendLine("     - ${holiday.name}")
                }
            }
            
            result.appendLine()
        }
        
        // Check leap months
        result.appendLine("\n=== KIỂM TRA THÁNG NHUẬN ===")
        val yearsToCheck = listOf(2023, 2024, 2025, 2026)
        yearsToCheck.forEach { year ->
            val leapMonth = AccurateLunarCalendar.getLeapMonth(year)
            result.appendLine("Năm $year: ${leapMonth?.let { "Tháng $it nhuận" } ?: "Không có tháng nhuận"}")
        }
        
        return result.toString()
    }
    
    data class TestDate(
        val solar: LocalDate,
        val expectedLunar: String,
        val description: String
    )
}