package com.mangala.wallet.utils

fun Double.truncateDecimal(
    decimalPlaces: Int,
    ignoreLocale: Boolean = false,
    roundingMode: DecimalFormatRoundingMode? = null
): String {
    val decimalFormat =
        DecimalFormat(
            "#.".padEnd(decimalPlaces + 1, '#'),
            ignoreLocale = ignoreLocale,
            roundingMode = roundingMode
        )
    return decimalFormat.format(this)
}