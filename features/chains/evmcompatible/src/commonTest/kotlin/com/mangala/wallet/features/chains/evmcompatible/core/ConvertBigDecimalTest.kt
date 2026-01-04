package com.mangala.wallet.features.chains.evmcompatible.core

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConvertBigDecimalTest {

    @Test
    fun convertBigDecimal() {
        val input = "0.012"
        val value = BigDecimal.parseString(input)
        val decimal = 18
        val bigInteger = value.moveDecimalPoint(decimal).toBigInteger()
        val result = "0x${bigInteger.toString(16)}"
        assertEquals(true, "0x2aa1efb94e0000" == result)
    }
}