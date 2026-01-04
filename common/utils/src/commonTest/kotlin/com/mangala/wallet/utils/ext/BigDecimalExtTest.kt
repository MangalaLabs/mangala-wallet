package com.mangala.wallet.utils.ext

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class BigDecimalExtTest {

    @Test
    fun `Given a large BigInteger value_When parse it to BigInteger and use weiToEth to parse it to an Ether value_Then precision is not lost`() {
        val value = BigInteger.parseString(
            "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
            base = 16
        )
        val convert = BigDecimal.fromBigInteger(value)
        assertEquals(
            "115792089237316195423570985008687907853269984665640564039457.584007913129639935".toBigDecimal(),
            convert.weiToEth(18)
        )
    }
}