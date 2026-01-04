package com.mangala.wallet.local.portfolio

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.mangala.wallet.database.MangalaWalletDatabase
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PortfolioLocalDataSourceImpl(databaseWrapper: MangalaWalletDatabaseWrapper) : PortfolioLocalDataSource {

    private val database = databaseWrapper.instance
    
    override suspend fun insertOrReplacePortfolio(
        userId: String,
        networkId: Int,
        portfolioId: String,
        balanceUsdt: String,
        pnl24hUsdt: String,
        pnl24hPercent: String,
        createdAt: Long,
        updatedAt: Long
    ) = withContext(Dispatchers.IO) {
        database.mangalaWalletDatabaseQueries.insertOrReplacePortfolio(
            user_id = userId,
            network_id = networkId.toLong(),
            portfolio_id = portfolioId,
            balance_usdt = balanceUsdt,
            pnl_24h_usdt = pnl24hUsdt,
            pnl_24h_percent = pnl24hPercent,
            created_at = createdAt,
            updated_at = updatedAt
        )
    }
    
    override suspend fun getPortfolioByUserIdAndNetworkId(userId: String, networkId: Int): PortfolioEntity? = 
        withContext(Dispatchers.IO) {
            database.mangalaWalletDatabaseQueries
                .getPortfolioByUserIdAndNetworkId(userId, networkId.toLong())
                .executeAsOneOrNull()
                ?.let { row ->
                    PortfolioEntity(
                        portfolioId = row.portfolio_id,
                        userId = row.user_id,
                        networkId = row.network_id.toInt(),
                        balanceUsdt = row.balance_usdt,
                        pnl24hUsdt = row.pnl_24h_usdt,
                        pnl24hPercent = row.pnl_24h_percent,
                        createdAt = row.created_at,
                        updatedAt = row.updated_at
                    )
                }
        }
    
    override fun getPortfolioByUserIdAndNetworkIdFlow(userId: String, networkId: Int): Flow<PortfolioEntity?> {
        return database.mangalaWalletDatabaseQueries
            .getPortfolioByUserIdAndNetworkId(userId, networkId.toLong())
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { row ->
                row?.let {
                    PortfolioEntity(
                        userId = it.user_id,
                        networkId = it.network_id.toInt(),
                        portfolioId = it.portfolio_id,
                        balanceUsdt = it.balance_usdt,
                        pnl24hUsdt = it.pnl_24h_usdt,
                        pnl24hPercent = it.pnl_24h_percent,
                        createdAt = it.created_at,
                        updatedAt = it.updated_at
                    )
                }
            }
    }
    
    override suspend fun getAllPortfoliosByUserId(userId: String): List<PortfolioEntity> = 
        withContext(Dispatchers.IO) {
            database.mangalaWalletDatabaseQueries
                .getAllPortfoliosByUserId(userId)
                .executeAsList()
                .map { row ->
                    PortfolioEntity(
                        userId = row.user_id,
                        networkId = row.network_id.toInt(),
                        portfolioId = row.portfolio_id,
                        balanceUsdt = row.balance_usdt,
                        pnl24hUsdt = row.pnl_24h_usdt,
                        pnl24hPercent = row.pnl_24h_percent,
                        createdAt = row.created_at,
                        updatedAt = row.updated_at
                    )
                }
        }
    
    override suspend fun deletePortfolioByUserIdAndNetworkId(userId: String, networkId: Int) = 
        withContext(Dispatchers.IO) {
            database.mangalaWalletDatabaseQueries.deletePortfolioByUserIdAndNetworkId(userId, networkId.toLong())
        }
}