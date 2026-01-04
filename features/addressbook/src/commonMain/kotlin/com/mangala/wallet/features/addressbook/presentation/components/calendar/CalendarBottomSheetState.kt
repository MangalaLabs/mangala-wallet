package com.mangala.wallet.features.addressbook.presentation.components.calendar

import com.mangala.wallet.features.addressbook.domain.model.ImportantDate
import com.mangala.wallet.features.addressbook.domain.model.CalendarType

/**
 * Unified state management for calendar bottom sheet
 */
data class CalendarBottomSheetState(
    val isVisible: Boolean = false,
    val editingDate: ImportantDate? = null,
    val calendarType: CalendarType? = null
)