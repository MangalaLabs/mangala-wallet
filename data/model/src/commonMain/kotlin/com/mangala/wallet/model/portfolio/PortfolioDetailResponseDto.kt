package com.mangala.wallet.model.portfolio

import kotlinx.serialization.Serializable

/**
 * Complete portfolio detail response DTO matching the actual API structure
 */
@Serializable
data class PortfolioDetailResponseDto(
    val data: PortfolioDataDto
)

/**
 * Portfolio data container DTO
 */
@Serializable
data class PortfolioDataDto(
    val portfolio: PortfolioDto,
    val pricingContext: PricingContextDto
)

@Serializable
data class PortfolioDto(
    val userId: String,
    val networkId: Int,
    val portfolioId: String,
    val totals: PortfolioTotalsDto,
    val accounts: List<PortfolioAccountDto>
)

@Serializable
data class PortfolioTotalsDto(
    val balanceUsdt: String,
    val pnl24hUsdt: String,
    val pnl24hPercent: String
)

@Serializable
data class PortfolioAccountDto(
    val accountId: String,
    val address: String,
    val label: String,
    val createdAt: String,
    val totals: PortfolioTotalsDto,
    val tokens: List<PortfolioTokenDto>,
    val resources: PortfolioResourcesDto?
)

/**
 * Portfolio token DTO
 */
@Serializable
data class PortfolioTokenDto(
    val tokenKey: String,
    val symbol: String,
    val name: String,
    val quantity: String,
    val balanceUsdt: String,
    val priceUsdt: String,
    val pnl24hUsdt: String,
    val pnl24hPercent: String
)

/**
 * Portfolio resources DTO (CPU, NET, RAM)
 */
@Serializable
data class PortfolioResourcesDto(
    val cpu: ResourceInfoDto?,
    val net: ResourceInfoDto?,
    val ram: ResourceInfoDto?
)

/**
 * Resource information DTO
 */
@Serializable
data class ResourceInfoDto(
    val used: String,
    val max: String,
    val available: String
)

/**
 * Pricing context DTO
 */
@Serializable
data class PricingContextDto(
    val asOf: String,
    val quoteCurrency: String,
    val status: String,
    val prices: Map<String, TokenPriceDto>
)

/**
 * Token price DTO
 */
@Serializable
data class TokenPriceDto(
    val spot: String,
    val price24hAgo: String,
    val lastUpdated: String,
    val source: String
)