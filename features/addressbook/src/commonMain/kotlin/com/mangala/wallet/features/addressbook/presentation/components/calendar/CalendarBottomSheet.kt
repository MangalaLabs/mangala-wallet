package com.mangala.wallet.features.addressbook.presentation.components.calendar

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import com.mangala.wallet.features.addressbook.domain.model.*
import com.mangala.wallet.features.addressbook.utils.AccurateLunarCalendar
import com.benasher44.uuid.uuid4

/**
 * Modern Calendar Bottom Sheet with Lunar/Solar support
 * Follows Material 3 design guidelines with custom Vietnamese calendar features
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (ImportantDate) -> Unit,
    existingDate: ImportantDate? = null
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { true }
    )
    val scope = rememberCoroutineScope()

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = null // Custom drag handle
        ) {
            CalendarContent(
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                },
                onConfirm = { date: ImportantDate ->
                    scope.launch {
                        onDateSelected(date)
                        sheetState.hide()
                        onDismiss()
                    }
                },
                existingDate = existingDate
            )
        }
    }
}

@Composable
internal fun CalendarContent(
    onDismiss: () -> Unit,
    onConfirm: (ImportantDate) -> Unit,
    existingDate: ImportantDate?
) {
    // State management
    var selectedDate by remember {
        mutableStateOf(existingDate?.date ?: Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }
    var calendarType by remember {
        mutableStateOf(existingDate?.calendarType ?: CalendarType.SOLAR)
    }
    var searchText by remember { mutableStateOf("") }
    var showLeapMonthWarning by remember { mutableStateOf(false) }

    // Month/Year state for navigation
    var currentMonth by remember { mutableStateOf(selectedDate.month) }
    var currentYear by remember { mutableStateOf(selectedDate.year) }
    
    // ✅ NEW: Track if we're in a leap month scenario (persist across calendar type changes)
    var isInLeapMonthScenario by remember { mutableStateOf(false) }
    
    // ✅ CRITICAL FIX: Reset states when existingDate changes
    LaunchedEffect(existingDate) {
        calendarType = existingDate?.calendarType ?: CalendarType.SOLAR
        val targetDate = existingDate?.date ?: Clock.System.todayIn(TimeZone.currentSystemDefault())
        selectedDate = targetDate
        currentMonth = targetDate.month
        currentYear = targetDate.year
        isInLeapMonthScenario = false
        searchText = ""
        showLeapMonthWarning = false
    }

    // Calendar context for leap month support
    val calendarContext by remember(currentMonth, currentYear, calendarType) {
        derivedStateOf {
            when (calendarType) {
                CalendarType.LUNAR -> {
                    // For lunar calendar, use the lunar date of the 15th of the displayed month
                    val daysInMonth = when (currentMonth) {
                        kotlinx.datetime.Month.FEBRUARY -> if (currentYear % 4 == 0 && (currentYear % 100 != 0 || currentYear % 400 == 0)) 29 else 28
                        kotlinx.datetime.Month.APRIL, kotlinx.datetime.Month.JUNE,
                        kotlinx.datetime.Month.SEPTEMBER, kotlinx.datetime.Month.NOVEMBER -> 30

                        else -> 31
                    }
                    val midMonthDate = LocalDate(currentYear, currentMonth, minOf(15, daysInMonth))
                    val lunarMidMonth = AccurateLunarCalendar.toLunar(midMonthDate)

                    CalendarContext(
                        month = lunarMidMonth.month,
                        year = lunarMidMonth.year,
                        isLeapMonth = lunarMidMonth.isLeapMonth,
                        hasLeapMonth = AccurateLunarCalendar.getLeapMonth(lunarMidMonth.year) != null,
                        leapMonthNumber = AccurateLunarCalendar.getLeapMonth(lunarMidMonth.year)
                    )
                }

                CalendarType.SOLAR -> {
                    // For solar calendar, preserve leap month characteristics by checking if any day in the current month falls in a leap month
                    val daysInMonth = when (currentMonth) {
                        kotlinx.datetime.Month.FEBRUARY -> if (currentYear % 4 == 0 && (currentYear % 100 != 0 || currentYear % 400 == 0)) 29 else 28
                        kotlinx.datetime.Month.APRIL, kotlinx.datetime.Month.JUNE,
                        kotlinx.datetime.Month.SEPTEMBER, kotlinx.datetime.Month.NOVEMBER -> 30
                        else -> 31
                    }
                    val midMonthDate = LocalDate(currentYear, currentMonth, minOf(15, daysInMonth))
                    val lunarMidMonth = AccurateLunarCalendar.toLunar(midMonthDate)
                    
                    // Check if this solar month contains days from a lunar leap month
                    val firstDayLunar = AccurateLunarCalendar.toLunar(LocalDate(currentYear, currentMonth, 1))
                    val lastDayLunar = AccurateLunarCalendar.toLunar(LocalDate(currentYear, currentMonth, daysInMonth))
                    val hasLeapMonthInThisSolarMonth = firstDayLunar.isLeapMonth || lastDayLunar.isLeapMonth || lunarMidMonth.isLeapMonth
                    
                    CalendarContext(
                        month = currentMonth.number,
                        year = currentYear,
                        isLeapMonth = hasLeapMonthInThisSolarMonth,
                        hasLeapMonth = AccurateLunarCalendar.getLeapMonth(lunarMidMonth.year) != null,
                        leapMonthNumber = AccurateLunarCalendar.getLeapMonth(lunarMidMonth.year)
                    )
                }
            }
        }
    }

    // Show warning when navigating to a leap month and track leap month scenario
    LaunchedEffect(calendarContext.isInLeapMonth) {
        if (calendarContext.isInLeapMonth) {
            isInLeapMonthScenario = true // ✅ Remember that we're in a leap month scenario
            showLeapMonthWarning = true
            // Auto hide after 3 seconds
            kotlinx.coroutines.delay(3000)
            showLeapMonthWarning = false
        } else {
            // ✅ IMPORTANT: Reset leap month scenario when leaving leap month
            isInLeapMonthScenario = false
        }
    }

    // Update month/year when selected date changes
    LaunchedEffect(selectedDate) {
        if (selectedDate.month != currentMonth || selectedDate.year != currentYear) {
            currentMonth = selectedDate.month
            currentYear = selectedDate.year
        }
    }

    // ✅ UI Colors - Dynamic based on leap month scenario (persists across calendar type changes)
    val primaryGradient = if (isInLeapMonthScenario) {
        Brush.linearGradient(
            colors = listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
        )
    }
    // Center content on large screens (tablets)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f) // Removed navigationBarsPadding to avoid double padding
            .imePadding(), // Handle keyboard insets
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp) // Maximum width for tablets
        ) {
        // Header with gradient background (includes drag handle)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryGradient)
        ) {
            Column {
                // Custom Drag Handle inside gradient
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, bottom = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.4f))
                )

                // Header content without background
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Top section with title and actions
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Title with leap month badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 🔄 ENHANCED: Dynamic title based on calendar type
                                Text(
                                    text = when (calendarType) {
                                        CalendarType.SOLAR -> "Ngày Dương Lịch"
                                        CalendarType.LUNAR -> "Ngày Âm Lịch"
                                    },
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.W600,
                                        color = Color.White
                                    )
                                )

                                // Leap Month Badge
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = calendarContext.isInLeapMonth,
                                    enter = fadeIn() + slideInHorizontally(),
                                    exit = fadeOut() + slideOutHorizontally()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color.White.copy(alpha = 0.9f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "⚡",
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Tháng Nhuận",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.W600,
                                                color = Color(0xFFFF6B6B)
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mode Toggle Icon with proper solar/lunar display
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .clickable {
                                            calendarType = if (calendarType == CalendarType.SOLAR)
                                                CalendarType.LUNAR
                                            else
                                                CalendarType.SOLAR
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (calendarType == CalendarType.LUNAR) "🌙" else "☀️",
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .clickable { onDismiss() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Đóng",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
//                                }
                            }
                        }
                    }

                    // Month Navigation inside gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, bottom = 15.dp)
                    ) {
                        MonthNavigation(
                            currentMonth = currentMonth,
                            currentYear = currentYear,
                            onMonthChanged = { month, year ->
                                currentMonth = month
                                currentYear = year
                            },
                            isInHeader = true  // White styling for gradient background
                        )
                    }
                }
            }
        }

        // Leap Month Warning
        androidx.compose.animation.AnimatedVisibility(
            visible = calendarType == CalendarType.LUNAR && calendarContext.isInLeapMonth,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEF3C7)) // Warm yellow background
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Đây là tháng nhuận - một tháng thêm vào trong năm âm lịch",
                        fontSize = 14.sp,
                        color = Color(0xFF92400E), // Dark amber
                        fontWeight = FontWeight.W500
                    )
                }
            }
        }

        // Body content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Smart Input
            SmartDateInput(
                value = searchText,
                onValueChange = { searchText = it },
                onSuggestionSelected = { suggestion ->
                    selectedDate = suggestion.date
                    searchText = ""
                },
                onModeChanged = { calendarType = it },
                modifier = Modifier.padding(vertical = 16.dp)
            )
            // Quick Select Pills
            QuickSelectSection(
                calendarContext = calendarContext,
                onQuickSelect = { type ->
                    when (type) {
                        QuickSelectType.TODAY -> {
                            selectedDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                        }

                        QuickSelectType.MUNG_1 -> {
                            calendarType = CalendarType.LUNAR
                            // Find Mùng 1 in the displayed month
                            val daysInMonth = when (currentMonth) {
                                kotlinx.datetime.Month.FEBRUARY -> if (currentYear % 4 == 0 && (currentYear % 100 != 0 || currentYear % 400 == 0)) 29 else 28
                                kotlinx.datetime.Month.APRIL, kotlinx.datetime.Month.JUNE,
                                kotlinx.datetime.Month.SEPTEMBER, kotlinx.datetime.Month.NOVEMBER -> 30

                                else -> 31
                            }

                            var foundDate: LocalDate? = null

                            // Search through all days in the displayed month
                            for (day in 1..daysInMonth) {
                                val date = LocalDate(currentYear, currentMonth, day)
                                val lunar = AccurateLunarCalendar.toLunar(date)

                                if (lunar.day == 1) {
                                    foundDate = date
                                    break
                                }
                            }

                            // If no Mùng 1 found in this month, find the closest one
                            if (foundDate == null) {
                                // Check a few days before and after the month
                                for (offset in -5..35) {
                                    val date = LocalDate(
                                        currentYear,
                                        currentMonth,
                                        1
                                    ).plus(DatePeriod(days = offset))
                                    val lunar = AccurateLunarCalendar.toLunar(date)

                                    if (lunar.day == 1) {
                                        // Use this date if it's the closest to our displayed month
                                        if (date.month == currentMonth || foundDate == null) {
                                            foundDate = date
                                            if (date.month == currentMonth) break
                                        }
                                    }
                                }
                            }

                            selectedDate = foundDate ?: LocalDate(currentYear, currentMonth, 1)
                        }

                        QuickSelectType.RAM -> {
                            calendarType = CalendarType.LUNAR
                            // Find Rằm (15th of lunar month) in current displayed month
                            val firstDay = LocalDate(currentYear, currentMonth, 1)
                            val lastDay = LocalDate(currentYear, currentMonth, 1)
                                .plus(DatePeriod(months = 1))
                                .minus(DatePeriod(days = 1))

                            var targetDate = firstDay
                            var found = false

                            // Search for day 15 in the current displayed month
                            for (day in 1..lastDay.dayOfMonth) {
                                val date = LocalDate(currentYear, currentMonth, day)
                                val lunar = AccurateLunarCalendar.toLunar(date)
                                if (lunar.day == 15) {
                                    targetDate = date
                                    found = true
                                    break
                                }
                            }

                            // If not found in current month, find the closest Rằm
                            if (!found) {
                                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                                val lunarToday = AccurateLunarCalendar.toLunar(today)

                                if (lunarToday.day < 15) {
                                    // Find this month's Rằm
                                    var date = today
                                    while (AccurateLunarCalendar.toLunar(date).day != 15) {
                                        date = date.plus(DatePeriod(days = 1))
                                    }
                                    targetDate = date
                                } else {
                                    // Find next month's Rằm
                                    var date =
                                        today.plus(DatePeriod(days = 30 - lunarToday.day + 15))
                                    while (AccurateLunarCalendar.toLunar(date).day != 15) {
                                        date = date.plus(DatePeriod(days = 1))
                                    }
                                    targetDate = date
                                }
                            }

                            selectedDate = targetDate
                        }

                        QuickSelectType.END_OF_MONTH -> {
                            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                            val lastDay = LocalDate(today.year, today.month, 1)
                                .plus(DatePeriod(months = 1))
                                .minus(DatePeriod(days = 1))
                            selectedDate = lastDay
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // Weekday Headers
            WeekdayHeaders()

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid
            CalendarDaysGridInternal(
                currentMonth = currentMonth,
                currentYear = currentYear,
                selectedDate = selectedDate,
                calendarType = calendarType,
                calendarContext = calendarContext,
                onDateSelected = { selectedDate = it },
                onMonthNavigate = { month, year ->
                    currentMonth = month
                    currentYear = year
                },
                alwaysShowSixWeeks = false // Set to true if you want consistent height
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Date Info
            SelectedDateInfo(
                selectedDate = selectedDate,
                calendarType = calendarType,
                calendarContext = calendarContext
            )
        }

        // Bottom Actions with proper padding for system navigation bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // Ensure buttons are above navigation bar
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            CalendarBottomActions(
                onCancel = onDismiss,
                onConfirm = {
                    val lunarDate = AccurateLunarCalendar.toLunar(selectedDate)
                    
                    // Create ImportantDate with lunar information

                    // Generate title based on calendar type
                    val dynamicTitle = when (calendarType) {
                        CalendarType.SOLAR -> "Solar"
                        CalendarType.LUNAR -> "Lunar"
                    }

                    val importantDate = ImportantDate(
                        id = existingDate?.id ?: uuid4().toString(),
                        title = dynamicTitle,
                        date = selectedDate,
                        calendarType = calendarType,
                        lunarDate = lunarDate,
                        category = ImportantDateCategory.OTHER,
                        notes = ""
                    )
                    
                    onConfirm(importantDate)
                },
                primaryGradient = primaryGradient
            )
        }
        } // End of Column
    } // End of Box
}

/**
 * Calendar Days Grid with flexible 35 or 42 cells based on month layout
 * @param alwaysShowSixWeeks If true, always show 6 weeks (42 cells) for consistent height
 */
@Composable
private fun CalendarDaysGridInternal(
    currentMonth: Month,
    currentYear: Int,
    selectedDate: LocalDate,
    calendarType: CalendarType,
    calendarContext: CalendarContext,
    onDateSelected: (LocalDate) -> Unit,
    onMonthNavigate: ((Month, Int) -> Unit)? = null,
    alwaysShowSixWeeks: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Create calendar days based on calendar type
    val calendarDays = when (calendarType) {
        CalendarType.SOLAR -> generateSolarCalendarDays(currentMonth, currentYear)
        CalendarType.LUNAR -> {
            // CRITICAL FIX: Generate lunar calendar based on SOLAR month range
            // This ensures all solar days in the month are shown with correct lunar info
            generateLunarCalendarForSolarMonth(currentMonth, currentYear, calendarContext)
        }
    }
    
    // Calculate weeks needed
    val weeksNeeded = if (alwaysShowSixWeeks) 6 
    else {
        val totalCells = calendarDays.size
        if (totalCells <= 35) 5 else 6
    }
    
    // Display in grid
    // Get screen configuration for adaptive spacing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    // Adaptive spacing based on screen size
    val columnSpacing = when {
        screenWidth < 360.dp -> 1.dp  // Minimal spacing for tiny screens
        screenWidth < 400.dp -> 2.dp  // Small spacing
        screenWidth < 600.dp -> 3.dp  // Medium spacing
        else -> 4.dp                  // Normal spacing for tablets
    }
    
    Column(modifier = modifier) {
        for (week in 0 until weeksNeeded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(columnSpacing)
            ) {
                for (dayOfWeek in 0..6) {
                    val index = week * 7 + dayOfWeek
                    if (index < calendarDays.size) {
                        val calendarDay = calendarDays[index]

                        DayCellInternal(
                            calendarDay = calendarDay,
                            isSelected = calendarDay.date == selectedDate,
                            calendarType = calendarType,
                            calendarContext = calendarContext,
                            onDateSelected = {
                                if (!calendarDay.isCurrentMonth && onMonthNavigate != null) {
                                    onMonthNavigate(calendarDay.date.month, calendarDay.date.year)
                                }
                                onDateSelected(calendarDay.date)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Empty cell for incomplete weeks
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            if (week < weeksNeeded - 1) {
                Spacer(modifier = Modifier.height(columnSpacing))
            }
        }
    }
}

// Generate solar calendar days (existing logic)
private fun generateSolarCalendarDays(currentMonth: Month, currentYear: Int): List<CalendarDay> {
    val calendarDays = mutableListOf<CalendarDay>()
    
    val firstDayOfMonth = LocalDate(currentYear, currentMonth, 1)
    val lastDayOfMonth = firstDayOfMonth.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
    val daysInMonth = lastDayOfMonth.dayOfMonth
    
    // Calculate the day of week for the first day (0 = Sunday, 6 = Saturday)
    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.ordinal + 1) % 7
    
    // Previous month days to show
    val prevMonth = if (currentMonth.ordinal > 0) Month.entries[currentMonth.ordinal - 1] else Month.DECEMBER
    val prevYear = if (currentMonth == Month.JANUARY) currentYear - 1 else currentYear
    val daysInPrevMonth = if (prevMonth == Month.FEBRUARY && isLeapYear(prevYear)) 29
    else daysInMonth(prevMonth)
    
    // Add previous month days with lunar information
    for (i in firstDayOfWeek downTo 1) {
        val day = daysInPrevMonth - i + 1
        val solarDate = LocalDate(prevYear, prevMonth, day)
        val lunar = AccurateLunarCalendar.toLunar(solarDate)
        
        calendarDays.add(
            CalendarDay(
                date = solarDate,
                isCurrentMonth = false,
                isPreviousMonth = true,
                lunarDay = lunar.day,
                lunarMonth = lunar.month,
                lunarYear = lunar.year,
                isLeapMonth = lunar.isLeapMonth
            )
        )
    }
    
    // Add current month days with lunar information
    for (day in 1..daysInMonth) {
        val solarDate = LocalDate(currentYear, currentMonth, day)
        val lunar = AccurateLunarCalendar.toLunar(solarDate)
        
        calendarDays.add(
            CalendarDay(
                date = solarDate,
                isCurrentMonth = true,
                isPreviousMonth = false,
                lunarDay = lunar.day,
                lunarMonth = lunar.month,
                lunarYear = lunar.year,
                isLeapMonth = lunar.isLeapMonth
            )
        )
    }
    
    // Add next month days to fill the grid
    val nextMonth = if (currentMonth.ordinal < 11) Month.entries[currentMonth.ordinal + 1] else Month.JANUARY
    val nextYear = if (currentMonth == Month.DECEMBER) currentYear + 1 else currentYear
    val currentSize = calendarDays.size
    val targetSize = if (currentSize <= 35) 35 else 42
    val remainingDays = targetSize - currentSize
    
    for (day in 1..remainingDays) {
        val solarDate = LocalDate(nextYear, nextMonth, day)
        val lunar = AccurateLunarCalendar.toLunar(solarDate)
        
        calendarDays.add(
            CalendarDay(
                date = solarDate,
                isCurrentMonth = false,
                isPreviousMonth = false,
                lunarDay = lunar.day,
                lunarMonth = lunar.month,
                lunarYear = lunar.year,
                isLeapMonth = lunar.isLeapMonth
            )
        )
    }
    
    return calendarDays
}

// Generate lunar calendar for solar month (NEW APPROACH)
private fun generateLunarCalendarForSolarMonth(currentMonth: Month, currentYear: Int, calendarContext: CalendarContext): List<CalendarDay> {
    val calendarDays = mutableListOf<CalendarDay>()
    
    
    // Calendar month calculation
    
    // Get the solar month range
    val firstDayOfMonth = LocalDate(currentYear, currentMonth, 1)
    val lastDayOfMonth = firstDayOfMonth.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
    val daysInMonth = lastDayOfMonth.dayOfMonth
    
    // Calculate the day of week for the first day (0 = Sunday, 6 = Saturday)
    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.ordinal + 1) % 7
    
    // Add padding days from previous solar month
    val prevMonth = if (currentMonth.ordinal > 0) Month.entries[currentMonth.ordinal - 1] else Month.DECEMBER
    val prevYear = if (currentMonth == Month.JANUARY) currentYear - 1 else currentYear
    val daysInPrevMonth = if (prevMonth == Month.FEBRUARY && isLeapYear(prevYear)) 29
    else daysInMonth(prevMonth)
    
    for (i in firstDayOfWeek downTo 1) {
        val day = daysInPrevMonth - i + 1
        val solarDate = LocalDate(prevYear, prevMonth, day)
        val lunar = AccurateLunarCalendar.toLunar(solarDate)
        
        calendarDays.add(
            CalendarDay(
                date = solarDate,
                isCurrentMonth = false,
                isPreviousMonth = true,
                lunarDay = lunar.day,
                lunarMonth = lunar.month,
                lunarYear = lunar.year,
                isLeapMonth = lunar.isLeapMonth
            )
        )
    }
    
    // Add current solar month days with their lunar equivalents
    for (day in 1..daysInMonth) {
        val solarDate = LocalDate(currentYear, currentMonth, day)
        val lunar = AccurateLunarCalendar.toLunar(solarDate)
        
        
        calendarDays.add(
            CalendarDay(
                date = solarDate,
                isCurrentMonth = true,
                isPreviousMonth = false,
                lunarDay = lunar.day,
                lunarMonth = lunar.month,
                lunarYear = lunar.year,
                isLeapMonth = lunar.isLeapMonth
            )
        )
    }
    
    // Add next solar month days for padding
    val nextMonth = if (currentMonth.ordinal < 11) Month.entries[currentMonth.ordinal + 1] else Month.JANUARY
    val nextYear = if (currentMonth == Month.DECEMBER) currentYear + 1 else currentYear
    val currentSize = calendarDays.size
    val targetSize = if (currentSize <= 35) 35 else 42
    val remainingDays = targetSize - currentSize
    
    for (day in 1..remainingDays) {
        val solarDate = LocalDate(nextYear, nextMonth, day)
        val lunar = AccurateLunarCalendar.toLunar(solarDate)
        
        calendarDays.add(
            CalendarDay(
                date = solarDate,
                isCurrentMonth = false,
                isPreviousMonth = false,
                lunarDay = lunar.day,
                lunarMonth = lunar.month,
                lunarYear = lunar.year,
                isLeapMonth = lunar.isLeapMonth
            )
        )
    }
    
    return calendarDays
}


/**
 * Individual Day Cell with hover effect
 */
@Composable
private fun DayCellInternal(
    calendarDay: CalendarDay,
    isSelected: Boolean,
    calendarType: CalendarType,
    calendarContext: CalendarContext,
    onDateSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    // Calculate adaptive padding based on screen size
    val cellPadding = when {
        screenWidth < 360.dp -> 1.dp  // Minimal padding for tiny screens
        screenWidth < 400.dp -> 2.dp  // Small padding for small screens  
        screenWidth < 600.dp -> 3.dp  // Medium padding
        else -> 4.dp                  // Normal padding for tablets
    }
    val isSpecialLunarDay = when {
        calendarDay.lunarDay != null -> calendarDay.lunarDay == 1 || calendarDay.lunarDay == 15
        else -> {
            val lunar = AccurateLunarCalendar.toLunar(calendarDay.date)
            lunar.day == 1 || lunar.day == 15
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()

    val scale = animateFloatAsState(
        targetValue = if (isPressed.value) 1.05f else 1f,
        animationSpec = spring(stiffness = 400f)
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale.value)
            .clip(RoundedCornerShape(10.dp))
            .then(
                when {
                    isSelected -> {
                        if (calendarContext.isInLeapMonth) {
                            Modifier.background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53))
                                )
                            )
                        } else {
                            Modifier.background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                )
                            )
                        }
                    }

                    !calendarDay.isCurrentMonth -> Modifier.background(Color(0xFFF0F0F0))
                    else -> {
                        // Special background for leap month days
                        if (calendarContext.isInLeapMonth) {
                            Modifier.background(Color(0xFFFFF5F5))
                        } else {
                            Modifier.background(Color(0xFFFAFAFA))
                        }
                    }
                }
            )
            .border(
                width = 2.dp,
                color = if (isSelected) Color.Transparent
                else if (isPressed.value && calendarContext.isInLeapMonth) Color(0xFFFF6B6B).copy(
                    alpha = 0.3f
                )
                else if (isPressed.value) Color(0xFF667EEA).copy(alpha = 0.3f)
                else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            ).clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onDateSelected() }
            .padding(cellPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = when {
                    screenWidth < 360.dp -> 0.dp   // No space for tiny screens
                    screenWidth < 400.dp -> 1.dp   // Minimal space for small screens
                    else -> 2.dp                   // Small space for normal screens
                },
                alignment = Alignment.CenterVertically
            )
        ) {
            // Main display - Solar or Lunar depending on mode
            Text(
                text = when (calendarType) {
                    CalendarType.SOLAR -> {
                        // Solar mode: show solar day as main
                        calendarDay.date.dayOfMonth.toString()
                    }
                    CalendarType.LUNAR -> {
                        // Lunar mode: show lunar day as main
                        if (calendarDay.lunarDay != null) {
                            
                            // ALWAYS prefer CalendarDay.lunarDay for consistency
                            getLunarDayDisplayName(calendarDay.lunarDay, screenWidth)
                        } else {
                            // Fallback: calculate lunar day but add validation
                            val lunar = AccurateLunarCalendar.toLunar(calendarDay.date)
                            val calculatedDay = lunar.day
                            
                            
                            getLunarDayDisplayName(calculatedDay, screenWidth)
                        }
                    }
                },
                // 🎯 ENHANCED: Dynamic font size based on content and screen width
                fontSize = when (calendarType) {
                    CalendarType.SOLAR -> when {
                        screenWidth < 360.dp -> 13.sp
                        else -> 14.sp
                    }
                    CalendarType.LUNAR -> {
                        val lunarDay = calendarDay.lunarDay ?: AccurateLunarCalendar.toLunar(calendarDay.date).day
                        // Use adaptive font sizing for special lunar labels
                        when {
                            lunarDay in 1..10 -> when {
                                screenWidth < 360.dp -> 10.sp  // Smaller for tiny screens
                                screenWidth < 400.dp -> 11.sp  // Medium screens
                                else -> 12.sp                  // Normal screens
                            }
                            lunarDay == 15 -> when {
                                screenWidth < 360.dp -> 11.sp
                                else -> 12.sp
                            }
                            else -> when {
                                screenWidth < 360.dp -> 13.sp
                                else -> 14.sp
                            }
                        }
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.W500,
                lineHeight = when {
                    screenWidth < 360.dp -> 12.sp  // Tight line height for small screens
                    else -> 14.sp                  // Normal line height
                },
                color = when {
                    isSelected -> Color.White
                    !calendarDay.isCurrentMonth -> Color.Gray.copy(alpha = 0.6f)
                    else -> Color(0xFF333333)
                }
            )

            // 🔄 ENHANCED: Dual-layer subtitle with visual feedback
            Text(
                text = when (calendarType) {
                    CalendarType.SOLAR -> {
                        // Solar mode: show lunar day as subtitle
                        val lunar = if (calendarDay.lunarDay != null) {
                            // ALWAYS prefer CalendarDay.lunarDay for consistency
                            calendarDay.lunarDay
                        } else {
                            // Fallback with validation
                            val calculatedDay = AccurateLunarCalendar.toLunar(calendarDay.date).day
                            
                            
                            calculatedDay
                        }
                        getLunarDayDisplayName(lunar, screenWidth)
                    }
                    CalendarType.LUNAR -> {
                        // Lunar mode: show solar date as subtitle
                        "${calendarDay.date.dayOfMonth}/${calendarDay.date.monthNumber}"
                    }
                },
                fontSize = when {
                    screenWidth < 360.dp -> 8.sp
                    else -> 9.sp
                },
                lineHeight = when {
                    screenWidth < 360.dp -> 8.sp   // Very tight for small screens
                    else -> 10.sp                  // Slightly tight for normal screens  
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                // 🎯 ENHANCED: Color feedback based on calendar type
                color = when {
                    isSelected -> Color.White.copy(alpha = 0.9f)
                    !calendarDay.isCurrentMonth -> Color.Gray.copy(alpha = 0.5f)
                    calendarType == CalendarType.LUNAR -> {
                        // In Lunar mode, solar subtitle gets green tint
                        if (calendarContext.isInLeapMonth) Color(0xFF4CAF50).copy(alpha = 0.8f) 
                        else Color(0xFF2E7D32).copy(alpha = 0.7f)
                    }
                    else -> {
                        // In Solar mode, lunar subtitle gets blue tint  
                        if (calendarContext.isInLeapMonth) Color(0xFF2196F3).copy(alpha = 0.8f)
                        else Color(0xFF1976D2).copy(alpha = 0.7f)
                    }
                }
            )
        }

        // Special lunar day indicator - adaptive size
        if (isSpecialLunarDay && calendarDay.isCurrentMonth) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(when {
                        screenWidth < 360.dp -> 2.dp
                        else -> 3.dp
                    })
                    .size(when {
                        screenWidth < 360.dp -> 4.dp
                        else -> 6.dp
                    })
                    .clip(CircleShape)
                    .background(Color(0xFFFF6B6B))
            )
        }
    }
}

// Helper data class
data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isPreviousMonth: Boolean,
    // Lunar calendar specific fields
    val lunarDay: Int? = null,
    val lunarMonth: Int? = null,
    val lunarYear: Int? = null,
    val isLeapMonth: Boolean = false
)

// Helper functions
private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

private fun daysInMonth(month: Month): Int {
    return when (month) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
        Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31

        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        Month.FEBRUARY -> 28
        else -> {
            30
        }
    }
}