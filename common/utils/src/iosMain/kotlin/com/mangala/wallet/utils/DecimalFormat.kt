package com.mangala.wallet.utils

import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatterRoundCeiling
import platform.Foundation.NSNumberFormatterRoundHalfUp
import platform.Foundation.numberWithDouble

actual class DecimalFormat actual constructor(
    pattern: String,
    roundingMode: DecimalFormatRoundingMode?,
    ignoreLocale: Boolean
) {
    private val formatter = NSNumberFormatter().apply {
        setPositiveFormat(pattern)
        if (roundingMode != null) {
            when(roundingMode) {
                DecimalFormatRoundingMode.CEILING -> setRoundingMode(NSNumberFormatterRoundCeiling)
                DecimalFormatRoundingMode.HALF_UP -> setRoundingMode(NSNumberFormatterRoundHalfUp)
            }
        }
    }

    actual fun format(value: Double): String {
        val number = NSNumber.numberWithDouble(value)
        return formatter.stringFromNumber(number) ?: ""
    }
}