package com.mangala.wallet.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

// Can use kotlinx.datetime built in format and remove this function
// once this is complete https://github.com/Kotlin/kotlinx-datetime/discussions/253
expect fun LocalDateTime.format(timeZone: TimeZone = TimeZone.currentSystemDefault(), format: String): String
expect fun LocalDateTime.formatDate(timeZone: TimeZone = TimeZone.currentSystemDefault(), style: FormatStyle = FormatStyle.SHORT): String
expect fun LocalDateTime.formatTime(timeZone: TimeZone = TimeZone.currentSystemDefault(), style: FormatStyle = FormatStyle.SHORT): String
expect fun LocalDateTime.formatDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault(), dateStyle: FormatStyle = FormatStyle.SHORT, timeStyle: FormatStyle = FormatStyle.SHORT): String

fun LocalDate.formatDate(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    style: FormatStyle = FormatStyle.SHORT
): String {
    val localDateTime = LocalDateTime(this, localTimeNow())

    return localDateTime.formatDate(timeZone, style)
}

fun LocalTime.formatTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    style: FormatStyle = FormatStyle.SHORT
): String {
    val localDateTime = LocalDateTime(localDateNow(), this)

    return localDateTime.formatTime(timeZone, style)
}

fun localDateTimeToMillis(localDateTime: LocalDateTime, timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return localDateTime.toInstant(timeZone).toEpochMilliseconds()
}

expect fun Int.getShortMonthName(): String // Pass in a number that is 1-based (i.e 1 for January)

enum class FormatStyle {
    SHORT,
    MEDIUM,
    LONG,
    FULL
}