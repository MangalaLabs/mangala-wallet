package com.mangala.wallet.local.portfolio

import kotlinx.coroutines.flow.Flow

/**
 * Local data source for complete portfolio detail operations
 * Handles portfolio accounts, tokens, and pricing context
 */
interface PortfolioDetailLocalDataSource {
    
    /**
     * Save complete portfolio detail data
     */
    suspend fun savePortfolioDetail(portfolioDetail: CachedPortfolioDetail)
    
    /**
     * Get complete portfolio detail by user ID and network ID
     */
    suspend fun getPortfolioDetail(userId: String, networkId: Int): CachedPortfolioDetail?
    
    /**
     * Get portfolio detail as flow
     */
    fun getPortfolioDetailFlow(userId: String, networkId: Int): Flow<CachedPortfolioDetail?>
    
    /**
     * Get portfolio by account address
     */
    suspend fun getPortfolioByAccountAddress(address: String): CachedPortfolioDetail?
    
    /**
     * Delete complete portfolio detail
     */
    suspend fun deletePortfolioDetail(userId: String, networkId: Int)
    
    /**
     * Check if portfolio data exists and is fresh
     */
    suspend fun isDataFresh(userId: String, networkId: Int, maxAgeMinutes: Int): Boolean
}

/**
 * Cached portfolio detail entity
 */
data class CachedPortfolioDetail(
    val portfolio: PortfolioEntity,
    val accounts: List<PortfolioAccountEntity>,
    val tokens: List<PortfolioTokenEntity>,
    val pricingContext: PortfolioPricingContextEntity?,
    val tokenPrices: List<PortfolioTokenPriceEntity>
)

/**
 * Portfolio account entity
 */
data class PortfolioAccountEntity(
    val accountId: String,
    val userId: String,
    val networkId: Int,
    val address: String,
    val label: String,
    val createdAt: String,
    val balanceUsdt: String,
    val pnl24hUsdt: String,
    val pnl24hPercent: String
)

/**
 * Portfolio token entity
 */
data class PortfolioTokenEntity(
    val tokenKey: String,
    val accountId: String,
    val userId: String,
    val networkId: Int,
    val symbol: String,
    val name: String,
    val quantity: String,
    val balanceUsdt: String,
    val priceUsdt: String,
    val pnl24hUsdt: String,
    val pnl24hPercent: String
)

/**
 * Portfolio pricing context entity
 */
data class PortfolioPricingContextEntity(
    val userId: String,
    val networkId: Int,
    val asOf: String,
    val quoteCurrency: String,
    val status: String,
    val updatedAt: Long
)

/**
 * Portfolio token price entity
 */
data class PortfolioTokenPriceEntity(
    val tokenKey: String,
    val userId: String,
    val networkId: Int,
    val spot: String,
    val price24hAgo: String,
    val lastUpdated: String,
    val source: String
)