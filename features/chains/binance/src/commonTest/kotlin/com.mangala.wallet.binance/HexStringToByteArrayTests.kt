package com.mangala.wallet.binance

import com.mangala.wallet.binance.data.model.hexStringToByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HexStringToByteArrayTests {

    @Test
    fun testEmptyString() {
        val input = ""
        val expectedOutput = byteArrayOf()
        assertEquals(true, expectedOutput.contentEquals(input.hexStringToByteArray()))
    }

    @Test
    fun testStringWithNormal() {
        val input = "0x70b3F49eA1f1F68b85D11899882DDa418e641f73"
        val expectedOutput = byteArrayOf(
            0x70.toByte(), 0xb3.toByte(), 0xf4.toByte(), 0x9e.toByte(),
            0xa1.toByte(), 0xf1.toByte(), 0xf6.toByte(), 0x8b.toByte(),
            0x85.toByte(), 0xd1.toByte(), 0x18.toByte(), 0x99.toByte(),
            0x82.toByte(), 0xdd.toByte(), 0xa4.toByte(), 0x18.toByte(),
            0xe6.toByte(), 0x41.toByte(), 0xf7.toByte(), 0x03.toByte()
        )
        assertEquals(expectedOutput.asList(), input.hexStringToByteArray().asList())
    }

    @Test
    fun testStringWithPrefix() {
        val input = "0x010203"
        val expectedOutput = byteArrayOf(0x01, 0x02, 0x03)
        assertEquals(true, expectedOutput.contentEquals(input.hexStringToByteArray()))
    }

    @Test
    fun testStringWithoutPrefix() {
        val input = "010203"
        val expectedOutput = byteArrayOf(0x01, 0x02, 0x03)
        assertEquals(true, expectedOutput.contentEquals(input.hexStringToByteArray()))
    }

    @Test
    fun testStringWithUpperCase() {
        val input = "0xA1B2C3"
        val expectedOutput = byteArrayOf(0xA1.toByte(), 0xB2.toByte(), 0xC3.toByte())
        assertEquals(true, expectedOutput.contentEquals(input.hexStringToByteArray()))
    }

    @Test
    fun testInvalidCharacter() {
        val input = "0x0G"
        assertFailsWith<IllegalArgumentException> {
            input.hexStringToByteArray()
        }
    }

    @Test
    fun testOddLength() {
        val input = "0x123"
        assertFailsWith<IllegalArgumentException> {
            input.hexStringToByteArray()
        }
    }
}
