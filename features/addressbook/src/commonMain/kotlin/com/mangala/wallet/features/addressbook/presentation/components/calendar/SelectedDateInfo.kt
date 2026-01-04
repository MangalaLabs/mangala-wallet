package com.mangala.wallet.features.addressbook.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import com.mangala.wallet.features.addressbook.domain.model.CalendarType
import com.mangala.wallet.features.addressbook.domain.model.CalendarContext
import com.mangala.wallet.features.addressbook.utils.AccurateLunarCalendar
import com.mangala.wallet.features.addressbook.utils.EnhancedLunarCalendarUtils
import androidx.compose.animation.*
import androidx.compose.animation.core.*

@Composable
fun SelectedDateInfo(
    selectedDate: LocalDate,
    calendarType: CalendarType,
    calendarContext: CalendarContext? = null,
    modifier: Modifier = Modifier
) {
    // Dynamic colors based on leap month context
    val cardBackgroundColor = if (calendarContext?.isInLeapMonth == true) Color(0xFFFFF5F5) else Color(0xFFF8F9FF)
    val borderColor = if (calendarContext?.isInLeapMonth == true) Color(0xFFFFB3B3) else Color(0xFFE0E5FF)
    val secondaryTextColor = if (calendarContext?.isInLeapMonth == true) Color(0xFFFF6B6B) else Color(0xFF667EEA)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Get lunar date info once at the beginning
            // CRITICAL FIX: Use same AccurateLunarCalendar as calendar grid
            val lunarDate = AccurateLunarCalendar.toLunar(selectedDate)
            
            // DEBUG: Log the lunar date for debugging
            if (selectedDate.toString().contains("2025-06")) {
                println("🔍 SelectedDateInfo lunar calculation for $selectedDate: day ${lunarDate.day}, month ${lunarDate.month}, leap: ${lunarDate.isLeapMonth}")
            }
            
            // Title
            Text(
                text = "Ngày đã chọn",
                fontSize = 11.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            
            // 🔄 FIXED: Dynamic main date display with smooth transition
            AnimatedContent(
                targetState = calendarType,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                }
            ) { currentCalendarType ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Main date - shows selected calendar type
                    Text(
                        text = when (currentCalendarType) {
                            CalendarType.SOLAR -> {
                                // Solar mode: Show solar date as main
                                "${selectedDate.dayOfMonth} tháng ${selectedDate.monthNumber}, ${selectedDate.year}"
                            }
                            CalendarType.LUNAR -> {
                                // Lunar mode: Show lunar date as main
                                "${getLunarDayDisplayName(lunarDate.day)}, ${getLunarMonthName(lunarDate.month)}${if (lunarDate.isLeapMonth) " nhuận" else ""} Âm"
                            }
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700,
                        color = when (currentCalendarType) {
                            CalendarType.SOLAR -> {
                                if (calendarContext?.isInLeapMonth == true) Color(0xFF4CAF50) else Color(0xFF2E7D32)
                            }
                            CalendarType.LUNAR -> {
                                if (calendarContext?.isInLeapMonth == true) Color(0xFF2196F3) else Color(0xFF1976D2)
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Secondary date - shows alternative calendar type
                    Text(
                        text = when (currentCalendarType) {
                            CalendarType.SOLAR -> {
                                // Solar mode: Show lunar date as secondary
                                "Âm lịch: ${getLunarDayDisplayName(lunarDate.day)}, ${getLunarMonthName(lunarDate.month)}${if (lunarDate.isLeapMonth) " nhuận" else ""}"
                            }
                            CalendarType.LUNAR -> {
                                // Lunar mode: Show solar date as secondary
                                "Dương lịch: ${selectedDate.dayOfMonth} tháng ${selectedDate.monthNumber}, ${selectedDate.year}"
                            }
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W500,
                        color = secondaryTextColor.copy(alpha = 0.8f)
                    )
                }
            }
            
            // Leap Month Notice
            AnimatedVisibility(
                visible = lunarDate.isLeapMonth,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            Color(0xFFFFF0F0),
                            RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, Color(0xFFFFB3B3), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📍",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Đây là ngày trong tháng nhuận - chỉ xuất hiện mỗi 2-3 năm",
                            fontSize = 12.sp,
                            color = Color(0xFFCC5555)
                        )
                    }
                }
            }
            
            // Divider
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = borderColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))
            
            // Lunar details grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Năm âm lịch
                    DetailItem(
                        label = "Năm âm lịch",
                        value = lunarDate.yearCycle
                    )
                    
                    // Giờ hoàng đạo
                    DetailItem(
                        label = "Giờ hoàng đạo",
                        value = getGioHoangDao(selectedDate)
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tiết khí
                    DetailItem(
                        label = "Tiết khí",
                        value = EnhancedLunarCalendarUtils.calculateSolarTerm(selectedDate)
                    )
                    
                    // Ngày can chi
                    DetailItem(
                        label = "Ngày can chi",
                        value = EnhancedLunarCalendarUtils.calculateDayCanChi(selectedDate)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF999999),
            modifier = Modifier.padding(bottom = 1.dp)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.W500,
            color = Color(0xFF333333)
        )
    }
}

private fun getLunarMonthName(month: Int): String {
    val months = listOf(
        "tháng Giêng", "tháng 2", "tháng 3", "tháng 4",
        "tháng 5", "tháng 6", "tháng 7", "tháng 8",
        "tháng 9", "tháng 10", "tháng 11", "tháng Chạp"
    )
    return if (month in 1..12) months[month - 1] else "tháng $month"
}

private fun getGioHoangDao(date: LocalDate): String {
    // Simplified - in reality this should be calculated based on Can Chi
    return when (date.dayOfWeek.ordinal) {
        0, 1 -> "Tý, Sửu, Mão, Ngọ"
        2, 3 -> "Dần, Thìn, Thân, Tuất"
        else -> "Tý, Dần, Mão, Ngọ, Thân, Dậu"
    }
}
