package com.mangala.features.wallet.details

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import kotlin.test.Test
import kotlin.test.assertEquals

class WalletDetailScreenUiModelTest {

//    @Test
//    fun `Given token with yesterday's value 50 and today's value 100_when getting PNL string_ then get correct result`() {
//        val tokenBalance: TokenBalanceModel = mockk()
//        every { tokenBalance.yesterdaysValue } returns BigDecimal.parseString("50")
//        every { tokenBalance.todaysValue } returns BigDecimal.parseString("100")
//        every { tokenBalance.pnl } returns BigDecimal.parseString("50")
//        every { tokenBalance.balance } returns "1"
//
//        val sut = PortfolioScreenUiModel(
//            listOf(tokenBalance),
//            showBalance = true,
//            hideZeroBalances = true
//        )
//
//        assertEquals("+50$/+100 %", sut.formattedPnl)
//    }
//
//    @Test
//    fun `Given token with yesterday's value 100 and today's value 50_when getting PNL string_ then get correct result`() {
//        val tokenBalance: TokenBalanceModel = mockk()
//        every { tokenBalance.yesterdaysValue } returns BigDecimal.parseString("100")
//        every { tokenBalance.todaysValue } returns BigDecimal.parseString("50")
//        every { tokenBalance.pnl } returns BigDecimal.parseString("-50")
//        every { tokenBalance.balance } returns "1"
//
//        val sut = PortfolioScreenUiModel(
//            listOf(tokenBalance),
//            showBalance = true,
//            hideZeroBalances = true
//        )
//
//        assertEquals("-50$/-50 %", sut.formattedPnl)
//    }
//
//    @Test
//    fun `Given token with yesterday's value 1 and today's value 1 point 1337_when getting PNL string_ then get correct result with rounded PNL value and percentage`() {
//        val tokenBalance: TokenBalanceModel = mockk()
//        every { tokenBalance.yesterdaysValue } returns BigDecimal.parseString("1")
//        every { tokenBalance.todaysValue } returns BigDecimal.parseString("1.01337")
//        every { tokenBalance.pnl } returns BigDecimal.parseString("0.01337")
//        every { tokenBalance.balance } returns "1"
//
//        val sut = PortfolioScreenUiModel(
//            listOf(tokenBalance),
//            showBalance = true,
//            hideZeroBalances = true
//        )
//
//        assertEquals("+0.01$/+1.34 %", sut.formattedPnl)
//    }
}