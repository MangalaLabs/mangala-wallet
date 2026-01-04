package com.mangala.wallet.utils

import java.math.RoundingMode
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.text.DecimalFormat as JvmDecimalFormat

actual class DecimalFormat actual constructor(
    pattern: String,
    roundingMode: DecimalFormatRoundingMode?,
    ignoreLocale: Boolean
) {
    private val locale: Locale = if (ignoreLocale) {
        Locale("en", "us")
    } else {
        Locale.getDefault()
    }

    private val formatter = JvmDecimalFormat(pattern, DecimalFormatSymbols(locale)).apply {
        if (roundingMode != null) {
            this.roundingMode = when (roundingMode) {
                DecimalFormatRoundingMode.CEILING -> RoundingMode.CEILING
                DecimalFormatRoundingMode.HALF_UP -> RoundingMode.HALF_UP
            }
        }
    }

    actual fun format(value: Double): String {
        return formatter.format(value)
    }
}