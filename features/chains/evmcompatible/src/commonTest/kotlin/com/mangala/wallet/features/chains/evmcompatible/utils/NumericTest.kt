package com.mangala.wallet.features.chains.evmcompatible.utils

import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals

class NumericTest {

    @Test
    fun testHexStringToByteArray() {
        val input = "0x1058d2810000000000000000000000000000000000000000000000000000000000000000"
        val expected = byteArrayOf(0x10, 0x58, -46, -127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val actual = Numeric.hexStringToByteArray(input)
        val output = actual.toHexString()
        assertEquals(input, output)
    }

}