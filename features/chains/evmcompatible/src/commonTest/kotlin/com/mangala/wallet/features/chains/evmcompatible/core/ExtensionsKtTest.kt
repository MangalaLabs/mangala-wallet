package com.mangala.wallet.features.chains.evmcompatible.core

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionsKtTest {

    @Test
    fun removeLeadingZeros() {
        val testString1 = "0000F4545"
        val testString2 = "0F4545"
        val testString3 = "F454500"

        assertEquals(true, testString1.removeLeadingZeros() == "F4545")
        assertEquals(true, testString2.removeLeadingZeros() == "F4545")
        assertEquals(true, testString3.removeLeadingZeros() == "F454500")
    }

    @Test
    fun `should convert byte array to int`() {
        val byteArray = byteArrayOf(0x12, 0x34, 0x56, 0x78)
        val expected = 0x12345678
        val actual = byteArray.toInt()
        assertEquals(expected, actual)
    }

    @Test
    fun testRemoveTrailingZeroes_ZeroInput_ReturnsZero() {
        val input = BigDecimal.ZERO
        val expected = BigDecimal.ZERO

        val result = input.removeTrailingZeroes()

        assertEquals(expected, result)
    }

    @Test
    fun testRemoveTrailingZeroes_NoTrailingZeroes_ReturnsSameValue() {
        val input = "123.45".toBigDecimal()
        val expected = "123.45".toBigDecimal()

        val result = input.removeTrailingZeroes()

        assertEquals(expected, result)
    }

    @Test
    fun testRemoveTrailingZeroes_TrailingZeroes_RemovesZeroes() {
        val input = "6000.45000".toBigDecimal()
        val expected = "6000.45".toBigDecimal()

        val result = input.removeTrailingZeroes()

        assertEquals(expected, result)
    }
}