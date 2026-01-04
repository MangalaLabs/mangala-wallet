package com.mangala.wallet.local.portfolio

import com.mangala.wallet.database.MangalaWalletDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Local data source interface for portfolio operations
 */
interface PortfolioLocalDataSource {
    
    /**
     * Insert or replace portfolio data
     */
    suspend fun insertOrReplacePortfolio(
        userId: String,
        networkId: Int,
        portfolioId: String,
        balanceUsdt: String,
        pnl24hUsdt: String,
        pnl24hPercent: String,
        createdAt: Long,
        updatedAt: Long
    )
    
    /**
     * Get portfolio by user ID and network ID
     */
    suspend fun getPortfolioByUserIdAndNetworkId(userId: String, networkId: Int): PortfolioEntity?
    
    /**
     * Get portfolio as flow for reactive updates
     */
    fun getPortfolioByUserIdAndNetworkIdFlow(userId: String, networkId: Int): Flow<PortfolioEntity?>
    
    /**
     * Get all portfolios for a user
     */
    suspend fun getAllPortfoliosByUserId(userId: String): List<PortfolioEntity>
    
    /**
     * Delete portfolio
     */
    suspend fun deletePortfolioByUserIdAndNetworkId(userId: String, networkId: Int)
}

data class PortfolioEntity(
    val portfolioId: String,
    val userId: String,
    val networkId: Int,
    val balanceUsdt: String,
    val pnl24hUsdt: String,
    val pnl24hPercent: String,
    val createdAt: Long,
    val updatedAt: Long
)