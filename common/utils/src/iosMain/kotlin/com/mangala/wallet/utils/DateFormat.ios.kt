package com.mangala.wallet.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toNSTimeZone
import platform.Foundation.*


// Can use kotlinx.datetime built in format and remove this function
// once this is complete https://github.com/Kotlin/kotlinx-datetime/discussions/253
actual fun LocalDateTime.format(timeZone: TimeZone, format: String): String {
    val nsDateFormatter = NSDateFormatter()
    val date = NSDate.dateWithTimeIntervalSince1970(this.toInstant(timeZone).epochSeconds.toDouble())

    return nsDateFormatter.apply {
        setDateFormat(format)
        setTimeZone(timeZone.toNSTimeZone())
        setLocale(NSLocale.currentLocale())
    }.stringFromDate(date)
}

actual fun Int.getShortMonthName(): String {
    return NSCalendar.currentCalendar().monthSymbols[this] as String
}



// https://developer.apple.com/documentation/foundation/dateformatter/1415241-localizedstring
actual fun LocalDateTime.formatDate(timeZone: TimeZone, style: FormatStyle): String {
    val nsDateFormatter = NSDateFormatter()
    val date = NSDate.dateWithTimeIntervalSince1970(this.toInstant(timeZone).epochSeconds.toDouble())

    return nsDateFormatter.apply {
        setDateStyle(style.toNSDateFormatterStyle())
        setTimeStyle(NSDateFormatterNoStyle)
        setTimeZone(timeZone.toNSTimeZone())
        setLocale(NSLocale.currentLocale())
    }.stringFromDate(date)
}

actual fun LocalDateTime.formatTime(timeZone: TimeZone, style: FormatStyle): String {
    val nsDateFormatter = NSDateFormatter()
    val date = NSDate.dateWithTimeIntervalSince1970(this.toInstant(timeZone).epochSeconds.toDouble())

    return nsDateFormatter.apply {
        setDateStyle(NSDateFormatterNoStyle)
        setTimeStyle(style.toNSDateFormatterStyle())
        setTimeZone(timeZone.toNSTimeZone())
        setLocale(NSLocale.currentLocale())
    }.stringFromDate(date)
}

actual fun LocalDateTime.formatDateTime(timeZone: TimeZone, dateStyle: FormatStyle, timeStyle: FormatStyle): String {
    val nsDateFormatter = NSDateFormatter()
    val date = NSDate.dateWithTimeIntervalSince1970(this.toInstant(timeZone).epochSeconds.toDouble())

    return nsDateFormatter.apply {
        setDateStyle(dateStyle.toNSDateFormatterStyle())
        setTimeStyle(timeStyle.toNSDateFormatterStyle())
        setTimeZone(timeZone.toNSTimeZone())
        setLocale(NSLocale.currentLocale())
    }.stringFromDate(date)
}

private fun FormatStyle.toNSDateFormatterStyle(): NSDateFormatterStyle {
    return when(this) {
        FormatStyle.SHORT -> NSDateFormatterShortStyle
        FormatStyle.MEDIUM -> NSDateFormatterMediumStyle
        FormatStyle.LONG -> NSDateFormatterLongStyle
        FormatStyle.FULL -> NSDateFormatterFullStyle
    }
}
