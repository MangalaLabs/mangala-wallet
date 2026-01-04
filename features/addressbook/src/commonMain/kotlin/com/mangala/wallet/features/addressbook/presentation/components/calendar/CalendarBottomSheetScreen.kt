package com.mangala.wallet.features.addressbook.presentation.components.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.serialization.Serializable
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.mangala.wallet.features.addressbook.domain.model.ImportantDate
import com.mangala.wallet.features.addressbook.domain.model.CalendarType
import com.mangala.wallet.features.addressbook.domain.model.ImportantDateCategory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// Global event channel for calendar selection
object CalendarSelectionChannel {
    private val _events = Channel<CalendarEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    suspend fun sendEvent(event: CalendarEvent) {
        _events.send(event)
    }
}

sealed class CalendarEvent {
    data class DateSelected(val date: ImportantDate, val screenId: String) : CalendarEvent()
    data class Dismissed(val screenId: String) : CalendarEvent()
}

/**
 * Screen wrapper for CalendarBottomSheet to work with MangalaBottomSheetNavigator
 */
@Serializable
data class CalendarBottomSheetScreen(
    val screenId: String,
    // Don't pass ImportantDate object, just pass primitives
    val existingDateId: String? = null,
    val existingDateTitle: String? = null,
    val existingDateDay: Int? = null,
    val existingDateMonth: Int? = null,
    val existingDateYear: Int? = null
) : Screen {
    
    @Composable
    override fun Content() {
        com.mangala.wallet.ui.LocalBottomNavigationVisibility.current.value = false
        
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val channel = remember { CalendarSelectionChannel }
        val scope = rememberCoroutineScope()
        
        // Reconstruct existingDate from primitives if available
        val existingDate = if (existingDateTitle != null && existingDateDay != null && existingDateMonth != null) {
            // Create a temporary ImportantDate for display only
            ImportantDate(
                id = existingDateId ?: "",
                title = existingDateTitle,
                // Use current year as default if existingDateYear is null
                date = kotlinx.datetime.LocalDate(
                    existingDateYear ?: Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).year,
                    existingDateMonth,
                    existingDateDay
                ),
                calendarType = CalendarType.SOLAR,
                lunarDate = null,
                category = ImportantDateCategory.OTHER,
                notes = ""
            )
        } else null
        
        // Reuse the existing CalendarContent
        CalendarBottomSheetContent(
            onDateSelected = { date ->
                // First hide the bottom sheet, then send event
                bottomSheetNavigator.hide()
                scope.launch {
                    channel.sendEvent(CalendarEvent.DateSelected(date, screenId))
                }
            },
            onDismiss = {
                // First hide the bottom sheet, then send event
                bottomSheetNavigator.hide()
                scope.launch {
                    channel.sendEvent(CalendarEvent.Dismissed(screenId))
                }
            },
            existingDate = existingDate
        )
    }
}

/**
 * Standalone composable for Calendar Bottom Sheet content
 */
@Composable
fun CalendarBottomSheetContent(
    onDateSelected: (ImportantDate) -> Unit,
    onDismiss: (() -> Unit)? = null,
    existingDate: ImportantDate?
) {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    
    CalendarContent(
        onDismiss = {
            onDismiss?.invoke() ?: bottomSheetNavigator.hide()
        },
        onConfirm = { date: ImportantDate ->
            onDateSelected(date)
        },
        existingDate = existingDate
    )
}