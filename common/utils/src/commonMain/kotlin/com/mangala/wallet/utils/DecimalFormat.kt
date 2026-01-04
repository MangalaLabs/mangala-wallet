package com.mangala.wallet.utils

// This class support different decimal separator
expect class DecimalFormat(
    pattern: String,
    roundingMode: DecimalFormatRoundingMode? = null,
    ignoreLocale: Boolean = false
) {
    fun format(value: Double): String
}

enum class DecimalFormatRoundingMode {
    CEILING,
    HALF_UP
}