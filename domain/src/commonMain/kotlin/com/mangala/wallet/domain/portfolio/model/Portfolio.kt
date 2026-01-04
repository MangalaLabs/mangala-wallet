package com.mangala.wallet.domain.portfolio.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal

data class Portfolio(
    val portfolioId: String,
    val userId: String,
    val networkId: Int,
    val totals: PortfolioTotals,
    val accounts: List<PortfolioAccount>
)

/**
 * Portfolio-level aggregated totals
 */
data class PortfolioTotals(
    val balanceUsdt: BigDecimal,
    val pnl24hUsdt: BigDecimal,
    val pnl24hPercent: BigDecimal
)

/**
 * Individual account within a portfolio
 */
data class PortfolioAccount(
    val accountId: String,
    val address: String,
    val label: String,
    val createdAt: String,
    val totals: PortfolioTotals,
    val tokens: List<PortfolioToken>,
    val resources: PortfolioResources?
)

/**
 * Individual token data within an account
 */
data class PortfolioToken(
    val tokenKey: String,
    val symbol: String,
    val name: String,
    val quantity: BigDecimal,
    val balanceUsdt: BigDecimal,
    val priceUsdt: BigDecimal,
    val pnl24hUsdt: BigDecimal,
    val pnl24hPercent: BigDecimal
)

/**
 * Resource information (CPU, NET, RAM) for blockchain accounts
 */
data class PortfolioResources(
    val cpu: ResourceInfo?,
    val net: ResourceInfo?,
    val ram: ResourceInfo?
)

/**
 * Individual resource information
 */
data class ResourceInfo(
    val used: BigDecimal,
    val max: BigDecimal,
    val available: BigDecimal
)