package com.mangala.wallet.utils.ext

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class BigDecimalExtFormatFiatTest {

    @Test
    fun `Given a BigDecimal with single digit in decimal part_When format_Then output value has currency symbol with an added trailing zero`() {
        val value = BigDecimal.fromDouble(2.7)
        val symbol = "$"

        val result = value.formatFiat(symbol)

        assertEquals("$2.70", result)
    }

    @Test
    fun `Given a zero BigDecimal value_When format_Then output has currency symbol without added trailing zero`() {
        val value = BigDecimal.fromDouble(0.0)
        val symbol = "$"

        val result = value.formatFiat(symbol)

        assertEquals("$0", result)
    }

    @Test
    fun `Given a BigDecimal with multiple digit in decimal part and rounding mode CEILING_When format_Then output value has currency symbol rounded`() {
        val value = BigDecimal.fromDouble(2.7024)
        val symbol = "$"

        val result = value.formatFiat(symbol, roundingMode = RoundingMode.CEILING)

        assertEquals("$2.71", result)
    }

    @Test
    fun `Given a BigDecimal with multiple digit in decimal part without specifying rounding mode_When format_Then output value has currency symbol rounded half even`() {
        val value = BigDecimal.fromDouble(2.755)
        val symbol = "$"

        val result = value.formatFiat(symbol, roundingMode = RoundingMode.CEILING)

        assertEquals("$2.76", result)
    }

    @Test
    fun `Given a BigDecimal with double digits in decimal part_When format_Then output value has currency symbol and number is kept as is`() {
        val value = BigDecimal.fromDouble(2.73)
        val symbol = "$"

        val result = value.formatFiat(symbol)

        assertEquals("$2.73", result)
    }

    @Test
    fun `Given a BigDecimal with value less than min value of currency_When format_Then output value with smallest denomination and less than sign`() {
        val value = BigDecimal.fromDouble(0.000001)
        val symbol = "$"

        val result = value.formatFiat(symbol)

        assertEquals("<$0.01", result)
    }

    @Test
    fun `Given a BigDecimal with value equal min value of currency_When format_Then output value with currency symbol and number kept as is`() {
        val value = BigDecimal.fromDouble(0.01)
        val symbol = "$"

        val result = value.formatFiat(symbol)

        assertEquals("$0.01", result)
    }

    @Test
    fun `Given a number with 4 decimals_when formatFiat called_then return correctly formatted value`() {
        val result = "17.0033".toBigDecimal().formatFiat("")

        assertEquals("17", result)
    }

    @Test
    fun `Given a number with small decimals_when formatFiat called_then return correctly formatted value`() {
        val result = "0.000000000001".toBigDecimal().formatFiat("")

        assertEquals("<0.01", result)
    }
}