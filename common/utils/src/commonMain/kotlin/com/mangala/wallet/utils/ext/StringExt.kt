package com.mangala.wallet.utils.ext

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.byUnicodePattern

fun String?.formatCompact(decimalScale: Long = 2L): String {
    val bigDecimal = BigDecimal.parseString(if (this.isNullOrBlank()) "0" else this)
    return bigDecimal.formatCompact(decimalScale)
}

fun String?.toInstant(): Instant {
    return this?.let { date ->
        val isoString = if (date.endsWith("Z") || date.contains("+")) date else "${date}Z"
        Instant.parse(isoString)
    } ?: Clock.System.now()
}

fun String?.toLongOrZero(): Long {
    return this?.toLongOrNull() ?: 0L
}

fun String?.toDoubleOrZero(): Double {
    return this?.toDoubleOrNull() ?: 0.0
}

fun String.getByteArray(): ByteArray {
    var hexWithoutPrefix = this.stripHexPrefix()
    if (hexWithoutPrefix.length % 2 == 1) {
        hexWithoutPrefix = "0$hexWithoutPrefix"
    }
    return hexWithoutPrefix.chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun String.stripHexPrefix(): String {
    return if (this.startsWith("0x", true)) {
        this.substring(2)
    } else {
        this
    }
}

fun String.parseUtcDateTimeToInstantOrNull(): Instant? {
    return try {
        val dateTimeFormat = DateTimeComponents.Format {
            byUnicodePattern("uuuu-MM-dd'T'HH:mm[:ss[[.SSS]['Z']]]")
        }
        Instant.parse(this, dateTimeFormat)
    } catch (e: Exception) {
        null
    }
}

fun String?.wrapInParenthesis(): String {
    return "($this)"
}