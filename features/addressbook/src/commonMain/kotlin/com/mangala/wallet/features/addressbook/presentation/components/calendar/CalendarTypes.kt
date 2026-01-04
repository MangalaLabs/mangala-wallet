package com.mangala.wallet.features.addressbook.presentation.components.calendar

// Shared data classes for calendar components
data class DateSuggestion(
    val date: kotlinx.datetime.LocalDate,
    val displayText: String,
    val subText: String = "",
    val calendarType: com.mangala.wallet.features.addressbook.domain.model.CalendarType,
    val isLunarKeyword: Boolean = false
)

enum class QuickSelectType {
    TODAY, MUNG_1, RAM, END_OF_MONTH
}
