package com.mangala.wallet.model.token.domain

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class TokenBalanceModelTest {

    @Test
    fun `Given TokenBalanceModel with today's price 60USD and price change 10USD and balance both days 1_When calculate yesterday's value_Then return 50`() {
        val sut = buildTokenBalanceModel(
            balance = "1",
            balance24h = "1",
            currentPrice = "60",
            priceChange24h = "10",
            contractDecimals = 0L
        )

        assertEquals(BigDecimal.parseString("50"), sut.yesterdaysValue)
    }

    @Test
    fun `Given TokenBalanceModel with today's price 40USD and price change -10USD and balance both days 1_When calculate yesterday's value_Then return 50`() {
        val sut = buildTokenBalanceModel(
            balance = "1",
            balance24h = "1",
            currentPrice = "40",
            priceChange24h = "-10",
            contractDecimals = 0L
        )

        assertEquals(BigDecimal.parseString("50"), sut.yesterdaysValue)
    }

    @Test
    fun `Given TokenBalanceModel with today's price 40USD and price change a floating point value and balance both days 1_When calculate yesterday's value_Then return correct result`() {
        val sut = buildTokenBalanceModel(
            balance = "1",
            balance24h = "1",
            currentPrice = "40",
            priceChange24h = "-10.123456",
            contractDecimals = 0L
        )

        assertEquals(BigDecimal.parseString("50.123456"), sut.yesterdaysValue)
    }

    @Test
    fun `Given TokenBalanceModel with today's price a floating point value and balance both days 1_When calculate yesterday's value_Then return correct result`() {
        val sut = buildTokenBalanceModel(
            balance = "1",
            balance24h = "1",
            currentPrice = "40.123456",
            priceChange24h = "-10",
            contractDecimals = 0L
        )

        assertEquals(BigDecimal.parseString("50.123456"), sut.yesterdaysValue)
    }

    @Test
    fun `Given TokenBalanceModel with today's price 40USD and price change -10USD and contract has decimal value_When calculate yesterday's value_Then return 50`() {
        val sut = buildTokenBalanceModel(
            balance = "1000000000000000000",
            balance24h = "1000000000000000000",
            currentPrice = "40",
            priceChange24h = "-10",
            contractDecimals = 18L
        )

        assertEquals(BigDecimal.parseString("50"), sut.yesterdaysValue)
    }

    @Test
    fun `Given TokenBalanceModel with today's price 60USD and balance 1_When calculate today's value_Then return 60`() {
        val sut = buildTokenBalanceModel(
            balance = "1",
            balance24h = "1",
            currentPrice = "60",
            priceChange24h = "10",
            contractDecimals = 0L
        )

        assertEquals(BigDecimal.parseString("60"), sut.todaysValue)
    }

    @Test
    fun `Given TokenBalanceModel with today's price 60USD and price change -10USD and contract has decimal value_When calculate today's value_Then return 60`() {
        val sut = buildTokenBalanceModel(
            balance = "1000000000000000000",
            balance24h = "1000000000000000000",
            currentPrice = "60",
            priceChange24h = "-10",
            contractDecimals = 18L
        )

        assertEquals(BigDecimal.parseString("60"), sut.todaysValue)
    }
}