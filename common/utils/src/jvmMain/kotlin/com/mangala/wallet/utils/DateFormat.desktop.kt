package com.mangala.wallet.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toJavaLocalTime
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.util.Date
import java.util.Locale

// Can use kotlinx.datetime built in format and remove this function
// once this is complete https://github.com/Kotlin/kotlinx-datetime/discussions/253
actual fun LocalDateTime.format(timeZone: TimeZone, format: String): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern(format)
    return formatter.format(this.toJavaLocalDateTime())
}

actual fun LocalDateTime.formatDate(timeZone: TimeZone, style: FormatStyle): String {
    val dateFormat = DateFormat.getDateInstance(style.toDateFormatFormatStyle(), Locale.getDefault())
    val instant = this.toInstant(timeZone).toJavaInstant()
    val date: Date = Date.from(instant)
    return dateFormat.format(date)
}

actual fun LocalDateTime.formatTime(timeZone: TimeZone, style: FormatStyle): String {
    val dateFormat = DateFormat.getTimeInstance(style.toDateFormatFormatStyle(), Locale.getDefault())
    val instant = this.toInstant(timeZone).toJavaInstant()
    val date: Date = Date.from(instant)
    return dateFormat.format(date)
}

actual fun LocalDateTime.formatDateTime(timeZone: TimeZone, dateStyle: FormatStyle, timeStyle: FormatStyle): String {
    val dateFormat = DateFormat.getDateTimeInstance(dateStyle.toDateFormatFormatStyle(), timeStyle.toDateFormatFormatStyle(), Locale.getDefault())
    val instant = this.toInstant(timeZone).toJavaInstant()
    val date: Date = Date.from(instant)
    return dateFormat.format(date)
}

private fun FormatStyle.toDateFormatFormatStyle(): Int {
    return when(this) {
        FormatStyle.SHORT -> DateFormat.SHORT
        FormatStyle.MEDIUM -> DateFormat.MEDIUM
        FormatStyle.LONG -> DateFormat.LONG
        FormatStyle.FULL -> DateFormat.FULL
    }
}

actual fun Int.getShortMonthName(): String {
    val months = DateFormatSymbols(Locale.getDefault()).months
    return months[this - 1]
}
