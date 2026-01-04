package com.mangala.wallet.features.addressbook.presentation.components.calendar

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import com.mangala.wallet.features.addressbook.domain.model.CalendarType
import com.mangala.wallet.features.addressbook.utils.LunarCalendarConverter

/**
 * Smart date input with Vietnamese lunar date recognition
 * Supports natural language input like "Mùng 1", "Rằm", "15/6"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartDateInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSuggestionSelected: (DateSuggestion) -> Unit,
    onModeChanged: (CalendarType) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var suggestions by remember { mutableStateOf<List<DateSuggestion>>(emptyList()) }
    var isFocused by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    Column(modifier = modifier) {
        // Input Field with hint
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    onValueChange(newValue)
                    
                    // Cancel previous search
                    searchJob?.cancel()
                    
                    // Debounce search
                    searchJob = scope.launch {
                        delay(300)
                        suggestions = generateSuggestions(newValue)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                placeholder = {
                    Text(
                        "Nhập ngày (VD: 15/6, Mùng 1, Rằm)",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF6366F1)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFFE0E5FF),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFFAFAFA)
                ),
                singleLine = true
            )
            
            // Hint text on the right
            if (value.isEmpty()) {
                Text(
                    "Tìm nhanh",
                    fontSize = 11.sp,
                    color = Color(0xFF999999),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 14.dp)
                )
            }
        }
        
        // Suggestions Dropdown
        AnimatedVisibility(
            visible = suggestions.isNotEmpty() && isFocused,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    suggestions.forEach { suggestion ->
                        SuggestionItem(
                            suggestion = suggestion,
                            onClick = {
                                onSuggestionSelected(suggestion)
                                onValueChange(suggestion.displayText)
                                suggestions = emptyList()
                                
                                // Auto-switch calendar mode based on suggestion
                                if (suggestion.isLunarKeyword) {
                                    onModeChanged(CalendarType.LUNAR)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: DateSuggestion,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = suggestion.displayText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            if (suggestion.subText.isNotEmpty()) {
                Text(
                    text = suggestion.subText,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        
        // Calendar type badge
        Box(
            modifier = Modifier
                .background(
                    color = if (suggestion.calendarType == CalendarType.LUNAR) 
                        Color(0xFFFFF7ED) else Color(0xFFF0F5FF),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (suggestion.calendarType == CalendarType.LUNAR) "Âm lịch" else "Dương lịch",
                fontSize = 11.sp,
                color = if (suggestion.calendarType == CalendarType.LUNAR) 
                    Color(0xFFEA580C) else Color(0xFF6366F1),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Generate smart suggestions based on input
 */
private fun generateSuggestions(input: String): List<DateSuggestion> {
    if (input.isBlank()) return emptyList()
    
    val suggestions = mutableListOf<DateSuggestion>()
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val lowerInput = input.lowercase().trim()
    
    // Check for lunar keywords
    when {
        lowerInput.contains("mùng 1") || lowerInput.contains("mung 1") || lowerInput == "1" -> {
            // Find Mùng 1 in current month
            val firstOfMonth = LocalDate(today.year, today.month, 1)
            val lastOfMonth = firstOfMonth.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
            
            for (day in 1..lastOfMonth.dayOfMonth) {
                val date = LocalDate(today.year, today.month, day)
                val lunar = LunarCalendarConverter.toLunar(date)
                if (lunar.day == 1) {
                    suggestions.add(
                        DateSuggestion(
                            date = date,
                            displayText = "Mùng 1 tháng này",
                            subText = "Ngày ${date.dayOfMonth}/${date.monthNumber}/${date.year}",
                            calendarType = CalendarType.LUNAR,
                            isLunarKeyword = true
                        )
                    )
                    break
                }
            }
            
            // Also suggest next month's Mùng 1
            val nextMonth = today.plus(DatePeriod(months = 1))
            for (day in 1..10) {
                val date = LocalDate(nextMonth.year, nextMonth.month, day)
                val lunar = LunarCalendarConverter.toLunar(date)
                if (lunar.day == 1) {
                    suggestions.add(
                        DateSuggestion(
                            date = date,
                            displayText = "Mùng 1 tháng sau",
                            subText = "Ngày ${date.dayOfMonth}/${date.monthNumber}/${date.year}",
                            calendarType = CalendarType.LUNAR,
                            isLunarKeyword = true
                        )
                    )
                    break
                }
            }
        }
        
        lowerInput.contains("rằm") || lowerInput.contains("ram") || lowerInput == "15" -> {
            // Find Rằm in current month
            val firstOfMonth = LocalDate(today.year, today.month, 1)
            val lastOfMonth = firstOfMonth.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
            
            for (day in 1..lastOfMonth.dayOfMonth) {
                val date = LocalDate(today.year, today.month, day)
                val lunar = LunarCalendarConverter.toLunar(date)
                if (lunar.day == 15) {
                    suggestions.add(
                        DateSuggestion(
                            date = date,
                            displayText = "Rằm tháng này",
                            subText = "Ngày ${date.dayOfMonth}/${date.monthNumber}/${date.year}",
                            calendarType = CalendarType.LUNAR,
                            isLunarKeyword = true
                        )
                    )
                    break
                }
            }
        }
        
        lowerInput.contains("cuối tháng") || lowerInput.contains("cuoi thang") -> {
            val lastDay = LocalDate(today.year, today.month, 1)
                .plus(DatePeriod(months = 1))
                .minus(DatePeriod(days = 1))
            suggestions.add(
                DateSuggestion(
                    date = lastDay,
                    displayText = "Cuối tháng ${today.monthNumber}",
                    subText = "${lastDay.dayOfMonth}/${lastDay.monthNumber}/${lastDay.year}",
                    calendarType = CalendarType.SOLAR
                )
            )
        }
        
        lowerInput.contains("hôm nay") || lowerInput.contains("hom nay") -> {
            suggestions.add(
                DateSuggestion(
                    date = today,
                    displayText = "Hôm nay",
                    subText = "${today.dayOfMonth}/${today.monthNumber}/${today.year}",
                    calendarType = CalendarType.SOLAR
                )
            )
        }
        
        lowerInput.contains("ngày mai") || lowerInput.contains("ngay mai") -> {
            val tomorrow = today.plus(DatePeriod(days = 1))
            suggestions.add(
                DateSuggestion(
                    date = tomorrow,
                    displayText = "Ngày mai",
                    subText = "${tomorrow.dayOfMonth}/${tomorrow.monthNumber}/${tomorrow.year}",
                    calendarType = CalendarType.SOLAR
                )
            )
        }
        
        // Date pattern matching (DD/MM or DD-MM or DD.MM)
        input.matches(Regex("\\d{1,2}[/\\-.]\\d{1,2}")) -> {
            val parts = input.split("/", "-", ".")
            if (parts.size == 2) {
                try {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt()
                    if (day in 1..31 && month in 1..12) {
                        val date = LocalDate(today.year, month, day)
                        suggestions.add(
                            DateSuggestion(
                                date = date,
                                displayText = "$day/$month/${today.year}",
                                calendarType = CalendarType.SOLAR
                            )
                        )
                        
                        // Also suggest for next year if the date has passed
                        if (date < today) {
                            val nextYearDate = LocalDate(today.year + 1, month, day)
                            suggestions.add(
                                DateSuggestion(
                                    date = nextYearDate,
                                    displayText = "$day/$month/${today.year + 1}",
                                    subText = "Năm sau",
                                    calendarType = CalendarType.SOLAR
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Invalid date
                }
            }
        }
        
        // Full date pattern (DD/MM/YYYY)
        input.matches(Regex("\\d{1,2}[/\\-.]\\d{1,2}[/\\-.]\\d{2,4}")) -> {
            val parts = input.split("/", "-", ".")
            if (parts.size == 3) {
                try {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt()
                    val year = if (parts[2].length == 2) 2000 + parts[2].toInt() else parts[2].toInt()
                    if (day in 1..31 && month in 1..12 && year > 1900) {
                        val date = LocalDate(year, month, day)
                        suggestions.add(
                            DateSuggestion(
                                date = date,
                                displayText = "$day/$month/$year",
                                calendarType = CalendarType.SOLAR
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Invalid date
                }
            }
        }
    }
    
    return suggestions.take(5) // Limit to 5 suggestions
}
