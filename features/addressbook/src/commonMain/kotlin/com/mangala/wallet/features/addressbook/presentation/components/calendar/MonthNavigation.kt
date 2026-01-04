package com.mangala.wallet.features.addressbook.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import kotlinx.datetime.Month
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import com.mangala.wallet.features.addressbook.utils.AccurateLunarCalendar
import kotlinx.datetime.LocalDate

@Composable
fun MonthNavigation(
    currentMonth: Month,
    currentYear: Int,
    onMonthChanged: (Month, Int) -> Unit,
    isInHeader: Boolean = false // Flag to determine styling
) {
    var showYearPicker by remember { mutableStateOf(false) }
    
    // Define colors based on context
    val backgroundColor = if (isInHeader) Color.White.copy(alpha = 0.1f) else Color(0xFFF8F8F8)
    val textColor = if (isInHeader) Color.White else Color(0xFF333333)
    val iconColor = if (isInHeader) Color.White else Color(0xFF666666)
    val lunarTextColor = if (isInHeader) Color.White.copy(alpha = 0.9f) else Color(0xFF6366F1)

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Month Button
                IconButton(
                    onClick = {
                        if (currentMonth.ordinal > 0) {
                            onMonthChanged(Month.entries[currentMonth.ordinal - 1], currentYear)
                        } else {
                            onMonthChanged(Month.DECEMBER, currentYear - 1)
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (isInHeader) {
                                Modifier
                            } else {
                                Modifier.background(Color.White)
                            }
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Tháng trước",
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Month and Year Display (Clickable) - Fixed width to prevent overflow
                Box(
                    modifier = Modifier
                        .widthIn(min = 140.dp, max = 200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showYearPicker = true }
                        .then(
                            if (!isInHeader) {
                                Modifier.background(Color.White.copy(alpha = 0.5f))
                            } else {
                                Modifier
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Tháng ${currentMonth.ordinal + 1},",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W600,
                                color = textColor,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentYear.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W600,
                                color = textColor,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.padding(bottom = 1.dp),
                                maxLines = 1
                            )
                        }

                        // Lunar month display
                        Text(
                            text = getLunarMonthName(currentMonth, currentYear),
                            fontSize = 13.sp,
                            color = lunarTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Next Month Button
                IconButton(
                    onClick = {
                        if (currentMonth.ordinal < 11) {
                            onMonthChanged(Month.entries[currentMonth.ordinal + 1], currentYear)
                        } else {
                            onMonthChanged(Month.JANUARY, currentYear + 1)
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (isInHeader) {
                                Modifier
                            } else {
                                Modifier.background(Color.White)
                            }
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Tháng sau",
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Year Picker Dropdown - Positioned below the navigation bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1000f)
        ) {
            if (showYearPicker) {
                YearPickerDropdown(
                    currentYear = currentYear,
                    onYearSelected = { year ->
                        onMonthChanged(currentMonth, year)
                        showYearPicker = false
                    },
                    onDismiss = { showYearPicker = false }
                )
            }
        }
    }
}

@Composable
private fun YearPickerDropdown(
    currentYear: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var yearRangeStart by remember { mutableStateOf(currentYear - (currentYear % 12)) }
    val currentActualYear = Clock.System.todayIn(TimeZone.currentSystemDefault()).year

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .width(280.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp,
                pressedElevation = 10.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Header with range navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$yearRangeStart - ${yearRangeStart + 11}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        color = Color(0xFF666666)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Previous range
                        IconButton(
                            onClick = { yearRangeStart -= 12 },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF0F0F0))
                        ) {
                            Icon(
                                Icons.Default.ChevronLeft,
                                contentDescription = "Range trước",
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF666666)
                            )
                        }

                        // Next range
                        IconButton(
                            onClick = { yearRangeStart += 12 },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF0F0F0))
                        ) {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Range sau",
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF666666)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Year grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (row in 0..2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (col in 0..3) {
                                val year = yearRangeStart + (row * 4) + col
                                YearItem(
                                    year = year,
                                    isSelected = year == currentYear,
                                    isCurrentYear = year == currentActualYear,
                                    onClick = { onYearSelected(year) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.YearItem(
    year: Int,
    isSelected: Boolean,
    isCurrentYear: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .then(
                when {
                    isSelected -> Modifier.background(Color(0xFF667EEA))
                    else -> Modifier.border(
                        width = if (isCurrentYear) 1.dp else 0.dp,
                        color = if (isCurrentYear) Color(0xFF667EEA) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = year.toString(),
            fontSize = 14.sp,
            fontWeight = if (isSelected || isCurrentYear) FontWeight.W500 else FontWeight.Normal,
            color = when {
                isSelected -> Color.White
                else -> Color(0xFF333333)
            }
        )
    }
}

private fun getLunarMonthName(month: Month, year: Int): String {
    val lunarMonths = listOf(
        "Tháng Giêng", "Tháng 2", "Tháng 3", "Tháng 4",
        "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8",
        "Tháng 9", "Tháng 10", "Tháng 11", "Tháng Chạp"
    )
    
    try {
        // Get the number of days in this solar month
        val daysInMonth = when (month) {
            Month.FEBRUARY -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            else -> 31
        }
        
        // Sample a few dates from the solar month to determine lunar month distribution
        val sampleDates = listOf(1, 15, daysInMonth)
        val lunarMonthCounts = mutableMapOf<Int, Int>()
        val lunarYearCounts = mutableMapOf<Int, Int>()
        val leapMonthInfo = mutableSetOf<String>()
        
        for (day in sampleDates) {
            val solarDate = LocalDate(year, month, day)
            val lunar = AccurateLunarCalendar.toLunar(solarDate)
            
            lunarMonthCounts[lunar.month] = (lunarMonthCounts[lunar.month] ?: 0) + 1
            lunarYearCounts[lunar.year] = (lunarYearCounts[lunar.year] ?: 0) + 1
            
            if (lunar.isLeapMonth) {
                leapMonthInfo.add("${lunar.month} (nhuận)")
            }
        }
        
        // Determine the most common lunar month
        val primaryLunarMonth = lunarMonthCounts.maxByOrNull { it.value }?.key ?: 1
        
        // Create the display string
        val monthName = lunarMonths.getOrNull(primaryLunarMonth - 1) ?: "Tháng $primaryLunarMonth"
        
        // If multiple lunar months are present, indicate it
        return if (lunarMonthCounts.size > 1) {
            val months = lunarMonthCounts.keys.sorted()
            val monthNames = months.map { 
                val name = lunarMonths.getOrNull(it - 1) ?: "Tháng $it"
                if (leapMonthInfo.contains("$it (nhuận)")) "$name (nhuận)" else name
            }
            "${monthNames.joinToString("-")} Âm"
        } else {
            val leapSuffix = if (leapMonthInfo.isNotEmpty()) " (nhuận)" else ""
            "$monthName Âm$leapSuffix"
        }
        
    } catch (e: Exception) {
        println("ERROR in getLunarMonthName: ${e.message}")
        // Fallback to simplified calculation
        val lunarMonthIndex = (month.ordinal + 11) % 12
        return "${lunarMonths[lunarMonthIndex]} Âm"
    }
}
