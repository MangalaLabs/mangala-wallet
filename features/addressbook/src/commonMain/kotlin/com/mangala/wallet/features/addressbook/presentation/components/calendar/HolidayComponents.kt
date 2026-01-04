package com.mangala.wallet.features.addressbook.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import com.mangala.wallet.features.addressbook.utils.VietnameseHolidays

/**
 * Component to display holiday indicator on calendar days
 */
@Composable
fun HolidayIndicator(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    val holidays = remember(date) {
        VietnameseHolidays.getHolidaysForDate(date)
    }
    
    if (holidays.isNotEmpty()) {
        Box(
            modifier = modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (holidays.any { it.isPublicHoliday }) 
                        Color(0xFFFF6B6B) 
                    else 
                        Color(0xFF667EEA)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = holidays.first().emoji,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Holiday banner to show at the top of calendar
 */
@Composable
fun HolidayBanner(
    selectedDate: LocalDate,
    modifier: Modifier = Modifier
) {
    val holidays = remember(selectedDate) {
        VietnameseHolidays.getHolidaysForDate(selectedDate)
    }
    
    if (holidays.isNotEmpty()) {
        val holiday = holidays.first()
        
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (holiday.isPublicHoliday)
                        Color(0xFFFFF0F0)
                    else
                        Color(0xFFF0F5FF)
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = holiday.emoji,
                    fontSize = 20.sp
                )
                
                Column {
                    Text(
                        text = holiday.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (holiday.isPublicHoliday) 
                            Color(0xFFDC2626) 
                        else 
                            Color(0xFF4F46E5)
                    )
                    
                    if (holiday.description.isNotEmpty()) {
                        Text(
                            text = holiday.description,
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
                
                if (holiday.isPublicHoliday) {
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFDC2626))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Ngày lễ",
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Component to show upcoming holidays
 */
@Composable
fun UpcomingHolidays(
    fromDate: LocalDate,
    modifier: Modifier = Modifier
) {
    val upcomingHolidays = remember(fromDate) {
        VietnameseHolidays.getUpcomingHolidays(fromDate, days = 30)
            .take(5) // Show only next 5 holidays
    }
    
    if (upcomingHolidays.isNotEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Ngày lễ sắp tới",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            
            upcomingHolidays.forEach { (date, holiday) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF9FAFB))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = holiday.emoji,
                        fontSize = 16.sp
                    )
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = holiday.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )
                        
                        Text(
                            text = "${date.dayOfMonth}/${date.monthNumber}",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                    
                    if (holiday.isPublicHoliday) {
                        Text(
                            text = "Nghỉ lễ",
                            fontSize = 11.sp,
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}