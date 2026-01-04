package com.mangala.wallet.utils.ext

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class ListExtTest {
    
    @Test
    fun `Given an empty list_when sumOf called_then return zero`() {
        val emptyList = listOf<Int>()
        val sum = emptyList.sumOf { it.toBigDecimal() }

        assertEquals(BigDecimal.ZERO, sum)
    }

    @Test
    fun `Given a list of integers 1 to 5_when sumOf called_then return 15`() {
        val numbers = listOf(1, 2, 3, 4, 5)
        val sum = numbers.sumOf { it.toBigDecimal() }

        assertEquals(15.toBigDecimal(), sum)
    }

    @Test
    fun `Given a list of BigDecimals 1 point 5 and 2 point 5 and 3 point 5_when sumOf called_then return 7 point 5`() {
        val numbers = listOf(1.5, 2.5, 3.5).map { it.toBigDecimal() }
        val sum = numbers.sumOf { it }

        assertEquals(7.5.toBigDecimal(), sum)
    }

    @Test
    fun `Given a list of Products with prices 10 point 5 and 5 point 25 and 20 point 75_when sumOf called_then return 36 point 5`() {
        data class Product(val price: BigDecimal)

        val products = listOf(
            Product(10.5.toBigDecimal()),
            Product(5.25.toBigDecimal()),
            Product(20.75.toBigDecimal())
        )

        val sum = products.sumOf { it.price }

        assertEquals(36.5.toBigDecimal(), sum)
    }
}