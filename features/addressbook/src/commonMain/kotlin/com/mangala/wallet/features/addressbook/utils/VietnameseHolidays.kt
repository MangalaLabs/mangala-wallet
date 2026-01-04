package com.mangala.wallet.features.addressbook.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import com.mangala.wallet.features.addressbook.domain.model.LunarDate

/**
 * Vietnamese Holiday Calendar
 * Defines important Vietnamese holidays and special dates
 */
object VietnameseHolidays {
    
    /**
     * Holiday information
     */
    data class Holiday(
        val name: String,
        val nameEn: String,
        val isLunar: Boolean,
        val lunarDay: Int? = null,
        val lunarMonth: Int? = null,
        val solarDay: Int? = null,
        val solarMonth: Int? = null,
        val isPublicHoliday: Boolean = false,
        val emoji: String = "🎉",
        val description: String = ""
    )
    
    /**
     * List of Vietnamese holidays
     */
    val holidays = listOf(
        // Solar holidays
        Holiday(
            name = "Tết Dương lịch",
            nameEn = "New Year's Day",
            isLunar = false,
            solarDay = 1,
            solarMonth = 1,
            isPublicHoliday = true,
            emoji = "🎊",
            description = "Năm mới Dương lịch"
        ),
        Holiday(
            name = "Ngày Quốc tế Phụ nữ",
            nameEn = "International Women's Day",
            isLunar = false,
            solarDay = 8,
            solarMonth = 3,
            emoji = "🌸",
            description = "Ngày tôn vinh phụ nữ"
        ),
        Holiday(
            name = "Ngày Giải phóng miền Nam",
            nameEn = "Reunification Day",
            isLunar = false,
            solarDay = 30,
            solarMonth = 4,
            isPublicHoliday = true,
            emoji = "🇻🇳",
            description = "Ngày thống nhất đất nước"
        ),
        Holiday(
            name = "Ngày Quốc tế Lao động",
            nameEn = "International Labor Day",
            isLunar = false,
            solarDay = 1,
            solarMonth = 5,
            isPublicHoliday = true,
            emoji = "💪",
            description = "Ngày tôn vinh người lao động"
        ),
        Holiday(
            name = "Ngày Quốc khánh",
            nameEn = "National Day",
            isLunar = false,
            solarDay = 2,
            solarMonth = 9,
            isPublicHoliday = true,
            emoji = "🎌",
            description = "Ngày độc lập của Việt Nam"
        ),
        Holiday(
            name = "Ngày Phụ nữ Việt Nam",
            nameEn = "Vietnamese Women's Day",
            isLunar = false,
            solarDay = 20,
            solarMonth = 10,
            emoji = "💐",
            description = "Ngày tôn vinh phụ nữ Việt Nam"
        ),
        Holiday(
            name = "Ngày Nhà giáo Việt Nam",
            nameEn = "Vietnamese Teacher's Day",
            isLunar = false,
            solarDay = 20,
            solarMonth = 11,
            emoji = "📚",
            description = "Ngày tôn vinh các thầy cô giáo"
        ),
        Holiday(
            name = "Giáng sinh",
            nameEn = "Christmas",            isLunar = false,
            solarDay = 25,
            solarMonth = 12,
            emoji = "🎄",
            description = "Lễ Giáng sinh"
        ),
        
        // Lunar holidays
        Holiday(
            name = "Tết Nguyên đán",
            nameEn = "Lunar New Year",
            isLunar = true,
            lunarDay = 1,
            lunarMonth = 1,
            isPublicHoliday = true,
            emoji = "🧧",
            description = "Tết cổ truyền Việt Nam"
        ),
        Holiday(
            name = "Mùng 2 Tết",
            nameEn = "2nd Day of Tết",
            isLunar = true,
            lunarDay = 2,
            lunarMonth = 1,
            isPublicHoliday = true,
            emoji = "🎊",
            description = "Ngày thứ 2 của Tết"
        ),
        Holiday(
            name = "Mùng 3 Tết",
            nameEn = "3rd Day of Tết",
            isLunar = true,
            lunarDay = 3,
            lunarMonth = 1,
            isPublicHoliday = true,
            emoji = "🎉",
            description = "Ngày thứ 3 của Tết"
        ),
        Holiday(
            name = "Tết Thượng Nguyên",
            nameEn = "Lantern Festival",
            isLunar = true,
            lunarDay = 15,
            lunarMonth = 1,
            emoji = "🏮",
            description = "Rằm tháng Giêng"
        ),
        Holiday(
            name = "Giỗ Tổ Hùng Vương",            nameEn = "Hung Kings' Festival",
            isLunar = true,
            lunarDay = 10,
            lunarMonth = 3,
            isPublicHoliday = true,
            emoji = "👑",
            description = "Ngày giỗ tổ Hùng Vương"
        ),
        Holiday(
            name = "Tết Đoan Ngọ",
            nameEn = "Dragon Boat Festival",
            isLunar = true,
            lunarDay = 5,
            lunarMonth = 5,
            emoji = "🐉",
            description = "Tết giết sâu bọ"
        ),
        Holiday(
            name = "Vu Lan",
            nameEn = "Ghost Festival",
            isLunar = true,
            lunarDay = 15,
            lunarMonth = 7,
            emoji = "🙏",
            description = "Lễ Vu Lan báo hiếu"
        ),
        Holiday(
            name = "Tết Trung Thu",
            nameEn = "Mid-Autumn Festival",
            isLunar = true,
            lunarDay = 15,
            lunarMonth = 8,
            emoji = "🥮",
            description = "Tết Trung Thu - Tết thiếu nhi"
        ),
        Holiday(
            name = "Tết Ông Táo",
            nameEn = "Kitchen God Day",
            isLunar = true,
            lunarDay = 23,
            lunarMonth = 12,
            emoji = "🔥",
            description = "Ngày ông Táo chầu trời"
        )
    )
    
    /**
     * Get holidays for a specific solar date
     */    fun getHolidaysForDate(date: LocalDate): List<Holiday> {
        val result = mutableListOf<Holiday>()
        
        // Check solar holidays
        result.addAll(holidays.filter { holiday ->
            !holiday.isLunar && 
            holiday.solarDay == date.dayOfMonth && 
            holiday.solarMonth == date.monthNumber
        })
        
        // Check lunar holidays
        try {
            val lunarDate = VietnameseLunarCalendar.toLunar(date)
            result.addAll(holidays.filter { holiday ->
                holiday.isLunar && 
                holiday.lunarDay == lunarDate.day && 
                holiday.lunarMonth == lunarDate.month
            })
        } catch (e: Exception) {
            // Ignore conversion errors
        }
        
        return result
    }
    
    /**
     * Get upcoming holidays within next N days
     */
    fun getUpcomingHolidays(fromDate: LocalDate, days: Int = 30): List<Pair<LocalDate, Holiday>> {
        val result = mutableListOf<Pair<LocalDate, Holiday>>()
        
        for (i in 0 until days) {
            val checkDate = fromDate.plus(DatePeriod(days = i))
            val holidaysOnDate = getHolidaysForDate(checkDate)
            
            holidaysOnDate.forEach { holiday ->
                result.add(checkDate to holiday)
            }
        }
        
        return result.sortedBy { it.first }
    }
    
    /**
     * Check if a date is a public holiday
     */
    fun isPublicHoliday(date: LocalDate): Boolean {
        return getHolidaysForDate(date).any { it.isPublicHoliday }
    }
}