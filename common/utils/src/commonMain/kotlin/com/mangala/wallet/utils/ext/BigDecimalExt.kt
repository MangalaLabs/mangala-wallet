package com.mangala.wallet.utils.ext

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.utils.CrashlyticsUtils
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.calculatingDecimalMode
import com.mangala.wallet.utils.getCurrentLocaleDecimalSeparator
import kotlin.math.min

fun BigDecimal.weiToEth(decimals: Int): BigDecimal {
    return this.divide(BigDecimal.TEN.pow(decimals), calculatingDecimalMode)
}

fun BigDecimal.ethToWei(decimals: Int): BigDecimal {
    return this.multiply(BigDecimal.TEN.pow(decimals))
}

fun BigDecimal.removeTrailingZeroes(): BigDecimal {
    if (this == BigDecimal.ZERO) return BigDecimal.ZERO
    var significand = this.significand
    var divisionResult = BigInteger.QuotientAndRemainder(this.significand, BigInteger.ZERO)
    do {
        divisionResult = divisionResult.quotient.divrem(BigInteger.TEN)
        if (divisionResult.remainder == BigInteger.ZERO) {
            significand = divisionResult.quotient
        }
    } while (divisionResult.remainder == BigInteger.ZERO)
    return BigDecimal.fromBigIntegerWithExponent(significand, this.exponent)
}

fun BigDecimal.formatFiat(
    symbol: String,
    decimalPlaces: Long = 2L,
    roundingMode: RoundingMode? = null,
): String {
    val minDenomination = BigDecimal.ONE.weiToEth(decimalPlaces.toInt())
    val decimalFormat = DecimalFormat("#.##")

    if (this == BigDecimal.ZERO) return symbol + "0"
    if (this < minDenomination) {
        return "<" + symbol + decimalFormat.format(minDenomination.doubleValue(exactRequired = false))
    }

    val roundedValue = this
        .roundToDigitPositionAfterDecimalPoint(
            decimalPlaces,
            roundingMode
                ?: if (this.roundingMode == RoundingMode.NONE) RoundingMode.ROUND_HALF_AWAY_FROM_ZERO else this.roundingMode
        )

    val strValue = roundedValue.toStringExpanded()
    val decimalDigits = strValue.substringAfter('.', "")
    val hasNonZeroDecimals = decimalDigits.any { it != '0' }
    val padChar = if (decimalDigits.length < decimalPlaces && this != BigDecimal.ZERO && hasNonZeroDecimals) "0" else ""

    return symbol + decimalFormat.format(roundedValue.doubleValue(exactRequired = false)) + padChar
}

fun BigDecimal.format(
    decimalPlaces: Long = 2L,
    roundingMode: RoundingMode? = null,
    ignoreLocale: Boolean = false,
): String {
    val minDenomination = BigDecimal.ONE.weiToEth(decimalPlaces.toInt())
    val pattern = if (decimalPlaces > 0) {
        "#." + "#".repeat(decimalPlaces.toInt())
    } else {
        "#"
    }
    val decimalFormat = DecimalFormat(pattern, ignoreLocale = ignoreLocale)

    // Check if value is smaller than minimum denomination
    if (this != BigDecimal.ZERO && this < minDenomination) {
        return "<" + decimalFormat.format(minDenomination.doubleValue(exactRequired = false))
    }

    val roundedValue = this.roundToDigitPositionAfterDecimalPoint(
        decimalPlaces,
        roundingMode
            ?: if (this.roundingMode == RoundingMode.NONE) RoundingMode.ROUND_HALF_AWAY_FROM_ZERO else this.roundingMode
    )

    return decimalFormat.format(roundedValue.doubleValue(exactRequired = false))
}

fun BigDecimal.divideSafe(divisor: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
    try {
        return this.divide(divisor, decimalMode)
    } catch (e: Exception) {
        CrashlyticsUtils.logNonFatal(e)
        return BigDecimal.ZERO
    }
}

fun BigDecimal?.orZero(): BigDecimal {
    return this ?: BigDecimal.ZERO
}

fun BigDecimal.formatCompact(decimalScale: Long = 2L, useScaleBasedCompactForSmallerThanOne: Boolean = false): String {
    if (this == BigDecimal.ZERO) return "0"

    val decimalSeparatorChar = getCurrentLocaleDecimalSeparator().toString()
    val isLessThanOne = this < BigDecimal.ONE
    val strValue = this.toPlainString()

    if (isLessThanOne) {
        return formatSmallDecimal(strValue, decimalScale.toInt(), decimalSeparatorChar, useScaleBasedCompactForSmallerThanOne)
    } else {
        val suffixes = arrayOf("", "K", "M", "B", "T", "Qa", "Qi")
        val integerPartSize = strValue.substringBefore('.').length
        val index = min((integerPartSize - 1) / 3, suffixes.size - 1)
        val compactValue = this.divide(BigDecimal.parseString("1000.0").pow(index),
            calculatingDecimalMode
        )
        return "${
            BigDecimal.fromBigDecimal(
                compactValue,
                decimalMode = DecimalMode(
                    scale = decimalScale,
                    roundingMode = RoundingMode.TOWARDS_ZERO,
                    decimalPrecision = 20
                )
            ).removeTrailingZeroes().toPlainString()
        }${suffixes[index]}".replace(".", decimalSeparatorChar)
    }
}

private fun formatSmallDecimal(strValue: String, decimalScale: Int, decimalSeparatorChar: String, useScaleBasedCompact: Boolean): String {
    val decimalPart = strValue.substringAfter('.', "")

    if (decimalPart.isEmpty()) return strValue.replace(".", decimalSeparatorChar)

    return if (useScaleBasedCompact) {
        // New mode: Compress based on decimalScale for last digits only
        val significantDecimalDigits = decimalPart.trimEnd('0')

        // If significant digits <= decimalScale, show as is
        if (significantDecimalDigits.length <= decimalScale) {
            return strValue.replace(".", decimalSeparatorChar)
        }

        // Calculate compression threshold: first(1) + dots(3) + last digits(decimalScale) = 4 + decimalScale
        val BASE_COMPRESSION_THRESHOLD = 4
        val compressionThreshold = BASE_COMPRESSION_THRESHOLD + decimalScale

        // Only compress if decimal part is longer than the compressed format would be
        if (significantDecimalDigits.length <= compressionThreshold) {
            return strValue.replace(".", decimalSeparatorChar)
        }

        val lastDigits = significantDecimalDigits.takeLast(decimalScale)
        val firstDigit = significantDecimalDigits.first()

        "0${decimalSeparatorChar}${firstDigit}...${lastDigits}"
    } else {
        // Original mode: Compress leading zeros
        val LEADING_ZEROS_THRESHOLD = 5
        val leadingZeroes = decimalPart.length - decimalPart.trimStart('0').length

        if (leadingZeroes < LEADING_ZEROS_THRESHOLD) {
            val ORIGINAL_MODE_TAKE_COUNT = LEADING_ZEROS_THRESHOLD + 2 // LEADING_ZEROS_THRESHOLD digits + 1 decimal point + 1 zero
            strValue.take(ORIGINAL_MODE_TAKE_COUNT).replace(".", decimalSeparatorChar)
        } else {
            val ORIGINAL_MODE_LAST_DIGITS_COUNT = 5
            "0${decimalSeparatorChar}0...${strValue.dropWhile { it == '0' || it == '.' }.takeLast(ORIGINAL_MODE_LAST_DIGITS_COUNT)}"
        }
    }
}
