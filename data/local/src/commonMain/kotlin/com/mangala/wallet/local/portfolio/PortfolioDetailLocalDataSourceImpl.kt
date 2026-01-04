package com.mangala.wallet.local.portfolio

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.mangala.wallet.database.MangalaWalletDatabase
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

/**
 * Implementation of PortfolioDetailLocalDataSource using SQLDelight
 */
class PortfolioDetailLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val portfolioLocalDataSource: PortfolioLocalDataSource
) : PortfolioDetailLocalDataSource {

    private val database = databaseWrapper.instance
    
    override suspend fun savePortfolioDetail(portfolioDetail: CachedPortfolioDetail) = withContext(Dispatchers.IO) {
        portfolioLocalDataSource.insertOrReplacePortfolio(
            portfolioId = portfolioDetail.portfolio.portfolioId,
            userId = portfolioDetail.portfolio.userId,
            networkId = portfolioDetail.portfolio.networkId,
            balanceUsdt = portfolioDetail.portfolio.balanceUsdt,
            pnl24hUsdt = portfolioDetail.portfolio.pnl24hUsdt,
            pnl24hPercent = portfolioDetail.portfolio.pnl24hPercent,
            createdAt = portfolioDetail.portfolio.createdAt,
            updatedAt = portfolioDetail.portfolio.updatedAt
        )
        
        database.transaction {
            // Clear existing accounts and tokens for this portfolio
            database.mangalaWalletDatabaseQueries.deletePortfolioAccountsByUserIdAndNetworkId(
                portfolioDetail.portfolio.userId,
                portfolioDetail.portfolio.networkId.toLong()
            )
            
            // Save accounts
            portfolioDetail.accounts.forEach { account ->
                database.mangalaWalletDatabaseQueries.insertOrReplacePortfolioAccount(
                    account_id = account.accountId,
                    user_id = account.userId,
                    network_id = account.networkId.toLong(),
                    address = account.address,
                    label = account.label,
                    created_at = account.createdAt,
                    balance_usdt = account.balanceUsdt,
                    pnl_24h_usdt = account.pnl24hUsdt,
                    pnl_24h_percent = account.pnl24hPercent
                )
            }
            
            // Save tokens
            portfolioDetail.tokens.forEach { token ->
                database.mangalaWalletDatabaseQueries.insertOrReplacePortfolioToken(
                    token_key = token.tokenKey,
                    account_id = token.accountId,
                    user_id = token.userId,
                    network_id = token.networkId.toLong(),
                    symbol = token.symbol,
                    name = token.name,
                    quantity = token.quantity,
                    balance_usdt = token.balanceUsdt,
                    price_usdt = token.priceUsdt,
                    pnl_24h_usdt = token.pnl24hUsdt,
                    pnl_24h_percent = token.pnl24hPercent
                )
            }
            
            // Save pricing context
            portfolioDetail.pricingContext?.let { context ->
                database.mangalaWalletDatabaseQueries.insertOrReplacePricingContext(
                    user_id = context.userId,
                    network_id = context.networkId.toLong(),
                    as_of = context.asOf,
                    quote_currency = context.quoteCurrency,
                    status = context.status,
                    updated_at = context.updatedAt
                )
            }
            
            // Save token prices
            portfolioDetail.tokenPrices.forEach { price ->
                database.mangalaWalletDatabaseQueries.insertOrReplacePortfolioTokenPrice(
                    token_key = price.tokenKey,
                    user_id = price.userId,
                    network_id = price.networkId.toLong(),
                    spot = price.spot,
                    price_24h_ago = price.price24hAgo,
                    last_updated = price.lastUpdated,
                    source = price.source
                )
            }
        }
    }
    
    override suspend fun getPortfolioDetail(userId: String, networkId: Int): CachedPortfolioDetail? = 
        withContext(Dispatchers.IO) {
            val portfolio = portfolioLocalDataSource.getPortfolioByUserIdAndNetworkId(userId, networkId)
                ?: return@withContext null
            
            val accounts = database.mangalaWalletDatabaseQueries
                .getPortfolioAccountsByUserIdAndNetworkId(userId, networkId.toLong())
                .executeAsList()
                .map { row ->
                    PortfolioAccountEntity(
                        accountId = row.account_id,
                        userId = row.user_id,
                        networkId = row.network_id.toInt(),
                        address = row.address,
                        label = row.label,
                        createdAt = row.created_at,
                        balanceUsdt = row.balance_usdt,
                        pnl24hUsdt = row.pnl_24h_usdt,
                        pnl24hPercent = row.pnl_24h_percent
                    )
                }
            
            val tokens = accounts.flatMap { account ->
                database.mangalaWalletDatabaseQueries
                    .getPortfolioTokensByAccountId(account.accountId, userId, networkId.toLong())
                    .executeAsList()
                    .map { row ->
                        PortfolioTokenEntity(
                            tokenKey = row.token_key,
                            accountId = row.account_id,
                            userId = row.user_id,
                            networkId = row.network_id.toInt(),
                            symbol = row.symbol,
                            name = row.name,
                            quantity = row.quantity,
                            balanceUsdt = row.balance_usdt,
                            priceUsdt = row.price_usdt,
                            pnl24hUsdt = row.pnl_24h_usdt,
                            pnl24hPercent = row.pnl_24h_percent
                        )
                    }
            }
            
            val pricingContext = database.mangalaWalletDatabaseQueries
                .getPricingContextByUserIdAndNetworkId(userId, networkId.toLong())
                .executeAsOneOrNull()
                ?.let { row ->
                    PortfolioPricingContextEntity(
                        userId = row.user_id,
                        networkId = row.network_id.toInt(),
                        asOf = row.as_of,
                        quoteCurrency = row.quote_currency,
                        status = row.status,
                        updatedAt = row.updated_at
                    )
                }
            
            val tokenPrices = database.mangalaWalletDatabaseQueries
                .getPortfolioTokenPricesByUserIdAndNetworkId(userId, networkId.toLong())
                .executeAsList()
                .map { row ->
                    PortfolioTokenPriceEntity(
                        tokenKey = row.token_key,
                        userId = row.user_id,
                        networkId = row.network_id.toInt(),
                        spot = row.spot,
                        price24hAgo = row.price_24h_ago,
                        lastUpdated = row.last_updated,
                        source = row.source
                    )
                }
            
            CachedPortfolioDetail(
                portfolio = portfolio,
                accounts = accounts,
                tokens = tokens,
                pricingContext = pricingContext,
                tokenPrices = tokenPrices
            )
        }
    
    override fun getPortfolioDetailFlow(userId: String, networkId: Int): Flow<CachedPortfolioDetail?> {
        val portfolioFlow = portfolioLocalDataSource.getPortfolioByUserIdAndNetworkIdFlow(userId, networkId)
        
        val accountsFlow = database.mangalaWalletDatabaseQueries
            .getPortfolioAccountsByUserIdAndNetworkId(userId, networkId.toLong())
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
        
        return combine(portfolioFlow, accountsFlow) { portfolio, _ ->
            if (portfolio != null) {
                getPortfolioDetail(userId, networkId)
            } else {
                null
            }
        }
    }
    
    override suspend fun getPortfolioByAccountAddress(address: String): CachedPortfolioDetail? = 
        withContext(Dispatchers.IO) {
            val account = database.mangalaWalletDatabaseQueries
                .getPortfolioAccountByAddress(address)
                .executeAsOneOrNull()
                ?: return@withContext null
            
            getPortfolioDetail(account.user_id, account.network_id.toInt())
        }
    
    override suspend fun deletePortfolioDetail(userId: String, networkId: Int) = withContext(Dispatchers.IO) {
        portfolioLocalDataSource.deletePortfolioByUserIdAndNetworkId(userId, networkId)
    }
    
    override suspend fun isDataFresh(userId: String, networkId: Int, maxAgeMinutes: Int): Boolean = 
        withContext(Dispatchers.IO) {
            val portfolio = portfolioLocalDataSource.getPortfolioByUserIdAndNetworkId(userId, networkId)
                ?: return@withContext false
            
            val now = Clock.System.now().toEpochMilliseconds()
            val ageMinutes = (now - portfolio.updatedAt) / (1000 * 60)
            
            ageMinutes <= maxAgeMinutes
        }
}