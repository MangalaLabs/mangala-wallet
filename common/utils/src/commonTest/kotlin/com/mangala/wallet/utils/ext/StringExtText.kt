package com.mangala.wallet.utils.ext

import com.mangala.wallet.utils.formatAmountInput
import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtText {

    @Test
    fun `Given large value_ when formatCompact_ then return abbreviated value`() {
        val string = "39969142670"
        val result = string.formatCompact()

        assertEquals("39.96B", result)
    }

    @Test
    fun `Given inputted value with single trailing decimal place_when formatAmountInput_then return value as is`() {
        val string = "0."
        val result = string.formatAmountInput("0")

        assertEquals("0.", result)
    }

    @Test
    fun `Given inputted value with single trailing comma as decimal place_when formatAmountInput_then return value as is and replaced with colon`() {
        val string = "0,"
        val result = string.formatAmountInput("0")

        assertEquals("0.", result)
    }

    @Test
    fun `Given inputted value with double decimal places_when formatAmountInput_then return value with decimal places trimmed and replaced with colon`() {
        val string = "0.."
        val result = string.formatAmountInput("0.")

        assertEquals("0.", result)
    }

    @Test
    fun `Given inputted value with double comma as decimal place_when formatAmountInput_then return value with decimal places trimmed and replaced with colon`() {
        val string = "0,,"
        val result = string.formatAmountInput("0.")

        assertEquals("0.", result)
    }

    @Test
    fun `Given inputted value with decimal place and trailing zeroes_when formatAmountInput_then return value as is`() {
        val string = "0.0000"
        val result = string.formatAmountInput("0.000")

        assertEquals("0.0000", result)
    }

    @Test
    fun `Given inputted value is significant and with decimal place_when formatAmountInput_then return value as is`() {
        val string = "0.00001"
        val result = string.formatAmountInput("0.0000")

        assertEquals("0.00001", result)
    }

    @Test
    fun `Given inputted value is significant and with comma as decimal place_when formatAmountInput_then return value as is and replaced with colon`() {
        val string = "0,00001"
        val result = string.formatAmountInput("0.0000")

        assertEquals("0.00001", result)
    }

    @Test
    fun `Given inputted value contains 18 decimal digits_when formatAmountInput_then return value as is`() {
        val string = "0.000000000000000001"
        val result = string.formatAmountInput("0.00000000000000000")

        assertEquals("0.000000000000000001", result)
    }

    @Test
    fun `Given inputted value contains 19 decimal digits_when formatAmountInput_then trim the input to 18 decimal digits`() {
        val string = "0.0000000000000000001"
        val result = string.formatAmountInput("0.000000000000000000")

        assertEquals("0.000000000000000000", result)
    }
}