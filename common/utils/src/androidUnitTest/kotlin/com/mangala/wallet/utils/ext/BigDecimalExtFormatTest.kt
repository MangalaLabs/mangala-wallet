package com.mangala.wallet.utils.ext

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.utils.displayRoundingMode
import kotlin.test.Test
import kotlin.test.assertEquals

class BigDecimalExtFormatTest {

    @Test
    fun `Given small value_when format fiat_ then return value with less than character`() {
        val value = "0.00000000000000000004".toBigDecimal()
        val formattedValue = value.format(
            decimalPlaces = 5,
            displayRoundingMode,
            ignoreLocale = false
        )

        val expected = "<0.00001"

        assertEquals(expected, formattedValue)
    }
}