package com.mangala.wallet.domain.portfolio.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.model.token.domain.TokenBalanceModel

/**
 * Domain model representing the complete portfolio across all wallets.
 * Contains pre-calculated totals - business logic is in the UseCase, not here.
 */
data class PortfolioData(
    val accounts: List<AccountPortfolio>,
    val totalValueUsd: BigDecimal,
    val totalPnl: BigDecimal,
    val totalPnlPercentage: BigDecimal
)

/**
 * Domain model representing a single account's portfolio.
 * Contains the account info and its calculated balance data.
 */
data class AccountPortfolio(
    val accountId: String,
    val accountName: String,
    val address: String,
    val walletId: String,
    val balances: List<TokenBalanceModel>,
    val totalValueUsd: BigDecimal,
    val pnl: BigDecimal,
    val yesterdayTotalValue: BigDecimal
)
