package com.mangala.wallet.utils

fun hexStringToByteArray(s: String): ByteArray {
    return s.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

fun String.truncateDecimal(
    decimalPlaces: Long,
    ignoreParsingZeroValues: Boolean = true,
    ignoreLocale: Boolean = false,
    roundingMode: DecimalFormatRoundingMode? = null
): String = truncateDecimal(decimalPlaces.toInt(), ignoreParsingZeroValues, ignoreLocale, roundingMode)

fun String.truncateDecimal(
    decimalPlaces: Int,
    ignoreParsingZeroValues: Boolean = true,
    ignoreLocale: Boolean = false,
    roundingMode: DecimalFormatRoundingMode? = null
): String {
    if (this.endsWith(",") || this.endsWith(".")) return this // Allow for decimal place input as 0

    if (ignoreParsingZeroValues && (this.toDouble() == 0.0 || this.endsWith("0"))) return this // Ignore parsing for 0.0 values

    return this.toDouble().truncateDecimal(decimalPlaces, ignoreLocale, roundingMode)
}

/**
 * Converts RGB values to hex color string (cross-platform safe)
 */
fun rgbToHex(red: Int, green: Int, blue: Int): String {
    fun Int.toHex(): String {
        val hex = this.toString(16).uppercase()
        return if (hex.length == 1) "0$hex" else hex
    }
    
    return "#${red.toHex()}${green.toHex()}${blue.toHex()}"
}