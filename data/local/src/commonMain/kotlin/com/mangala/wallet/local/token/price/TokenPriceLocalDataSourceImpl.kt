package com.mangala.wallet.local.token.price

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.model.provider.coingecko.CoinsMarketsResponse
import com.mangala.wallet.model.token.TokenPriceEntity
import commangalawalletdatabase.GetTokenWithSparklineByCoinUidsAndCurrencyCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class TokenPriceLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TokenPriceLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun deleteTokenPriceByCoinId(coinId: String) = withContext(ioDispatcher) {
        dbQuery.deleteTokenPriceByCoinUid(coinId)
    }

    override suspend fun getTokenPriceByCoinId(coinId: String): List<TokenPriceEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getTokenPriceByCoinUid(coinId,::mapTokenPrice).executeAsList()
    }

    override suspend fun getTokenPriceByCoinIdAndCurrencyCode(
        coinId: String,
        currencyCode: String
    ): List<TokenPriceEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getTokenPriceByCoinUidAndCurrencyCode(coinId, currencyCode,::mapTokenPrice).executeAsList()
    }

    override suspend fun getTokenPriceByCoinIdsAndCurrencyCode(
        coinIds: List<String>,
        currencyCode: String
    ): List<TokenPriceEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getTokenPriceByCoinUidsAndCurrencyCode(coinIds, currencyCode,::mapTokenPrice).executeAsList()
    }

    override suspend fun getTokenWithSparklineByCoinUidsAndCurrencyCode(
        coinIds: List<String>,
        currencyCode: String
    ): List<TokenPriceEntity> = withContext(ioDispatcher) {
        val rows = dbQuery.getTokenWithSparklineByCoinUidsAndCurrencyCode(coinIds, currencyCode).executeAsList()
        return@withContext rows.toTokenPriceEntityList()
    }

    override fun getTokenWithSparklineByCoinUidsAndCurrencyCodeFlow(
        coinIds: List<String>,
        currencyCode: String
    ): Flow<List<TokenPriceEntity>> {
        return dbQuery.getTokenWithSparklineByCoinUidsAndCurrencyCode(coinIds, currencyCode)
            .asFlow()
            .map { it.executeAsList().toTokenPriceEntityList() }
            .flowOn(ioDispatcher)
    }

    override suspend fun insertTokenPrice(tokenPrices: List<TokenPriceEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            tokenPrices.forEach { token ->
                insertTokenPrice(token)
            }
        }
    }

    override suspend fun insertOrReplaceTokenPrice(tokenPrices: List<TokenPriceEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            val timestamp = Clock.System.now().toEpochMilliseconds()
            tokenPrices.forEach { token ->
                insertOrReplaceTokenPrice(token)
                token.sparklineIn7d?.price?.let {
                    insertTokenPriceSparklineEntity(token.coinUid, timestamp, it)
                }
            }
        }
    }

    override suspend fun updateTokenPrice(tokenPrices: List<TokenPriceEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            tokenPrices.forEach { token ->
                updateTokenPrice(token)
            }
        }
    }

    private fun mapTokenPrice(
        coinUid: String,
        currencyCode: String,
        currentPrice: String?,
        marketCap: String?,
        marketCapRank: Long?,
        totalVolume: String?,
        high24h: String?,
        low24h: String?,
        priceChange24h: String?,
        priceChangePercentage24h: String?,
        marketCapChange24h: String?,
        marketCapChangePercentage24h: String?,
        fullyDilutedValuation: String?,
        circulatingSupply: String?,
        totalSupply: String?,
        maxSupply: String?,
        ath: String?,
        athChangePercentage: String?,
        athDate: String?,
        atl: String?,
        priceChangePercentage1hInCurrency: String?,
        priceChangePercentage24hInCurrency: String?,
        priceChangePercentage7dInCurrency: String?,
        lastUpdated: Long?,
        sparklineIn7d: CoinsMarketsResponse.SparklineIn7d? = null
    ): TokenPriceEntity {
        return TokenPriceEntity(
            coinUid,
            currencyCode,
            currentPrice,
            marketCap,
            marketCapRank?.toInt(),
            totalVolume,
            high24h,
            low24h,
            priceChange24h,
            priceChangePercentage24h,
            marketCapChange24h,
            marketCapChangePercentage24h,
            fullyDilutedValuation,
            circulatingSupply,
            totalSupply,
            maxSupply,
            ath,
            athChangePercentage,
            athDate,
            atl,
            priceChangePercentage1hInCurrency,
            priceChangePercentage24hInCurrency,
            priceChangePercentage7dInCurrency,
            lastUpdated,
            sparklineIn7d = null
        )
    }

    private fun insertTokenPrice(token: TokenPriceEntity) {
        dbQuery.insertTokenPriceEntity(
            token.coinUid,
            token.currencyCode,
            token.currentPrice,
            token.marketCap,
            token.marketCapRank?.toLong(),
            token.totalVolume,
            token.high24h,
            token.low24h,
            token.priceChange24h,
            token.priceChangePercentage24h,
            token.marketCapChange24h,
            token.marketCapChangePercentage24h,
            token.fullyDilutedValuation,
            token.circulatingSupply,
            token.totalSupply,
            token.maxSupply,
            token.ath,
            token.athChangePercentage,
            token.athDate,
            token.atl,
            token.priceChangePercentage1hInCurrency,
            token.priceChangePercentage24hInCurrency,
            token.priceChangePercentage7dInCurrency,
            token.lastUpdated
        )
    }

    private fun insertOrReplaceTokenPrice(token: TokenPriceEntity) {
        dbQuery.insertOrReplaceTokenPriceEntity(
            token.coinUid,
            token.currencyCode,
            token.currentPrice,
            token.marketCap,
            token.marketCapRank?.toLong(),
            token.totalVolume,
            token.high24h,
            token.low24h,
            token.priceChange24h,
            token.priceChangePercentage24h,
            token.marketCapChange24h,
            token.marketCapChangePercentage24h,
            token.fullyDilutedValuation,
            token.circulatingSupply,
            token.totalSupply,
            token.maxSupply,
            token.ath,
            token.athChangePercentage,
            token.athDate,
            token.atl,
            token.priceChangePercentage1hInCurrency,
            token.priceChangePercentage24hInCurrency,
            token.priceChangePercentage7dInCurrency,
            token.lastUpdated
        )
    }

    private fun insertTokenPriceSparklineEntity(coinUid: String, timestamp: Long, price: List<Double>) {
        dbQuery.transaction {
            dbQuery.deleteTokenPriceSparklineByCoinUid(coinUid)
            price.forEach {
                dbQuery.insertOrReplaceTokenPriceSparklineEntity(
                    coinUid,
                    timestamp,
                    it
                )
            }
        }
    }

    private fun updateTokenPrice(token: TokenPriceEntity) {
        dbQuery.updateTokenPriceByCoinUidAndCurrencyCode(
            token.currentPrice,
            token.marketCap,
            token.marketCapRank?.toLong(),
            token.totalVolume,
            token.high24h,
            token.low24h,
            token.priceChange24h,
            token.priceChangePercentage24h,
            token.marketCapChange24h,
            token.marketCapChangePercentage24h,
            token.fullyDilutedValuation,
            token.circulatingSupply,
            token.totalSupply,
            token.maxSupply,
            token.ath,
            token.athChangePercentage,
            token.athDate,
            token.atl,
            token.priceChangePercentage1hInCurrency,
            token.priceChangePercentage24hInCurrency,
            token.priceChangePercentage7dInCurrency,
            token.lastUpdated,
            token.coinUid,
            token.currencyCode,
        )
    }

    private fun List<GetTokenWithSparklineByCoinUidsAndCurrencyCode>.toTokenPriceEntityList(): List<TokenPriceEntity> {
        val result = mutableListOf<TokenPriceEntity>()

        // Create a mutable map to store TokenPriceEntity objects by their ids
        val entitiesById = mutableMapOf<String, TokenPriceEntity>()

        for (row in this) {
            val id = row.coin_uid
            // Check if we already have a TokenPriceEntity for this id
            val entity = entitiesById[id]
            if (entity != null) {
                // If we do, we just add the new price to the existing SparklineIn7d
                entity.sparklineIn7d?.price?.add(row.price ?: 0.0)
            } else {
                // If we don't, we create a new TokenPriceEntity and SparklineIn7d
                entitiesById[id] = TokenPriceEntity(
                    coinUid = row.coin_uid,
                    currencyCode = row.currency_code,
                    currentPrice = row.current_price,
                    marketCap = row.market_cap,
                    marketCapRank = row.market_cap_rank?.toInt(),
                    totalVolume = row.total_volume,
                    high24h = row.high_24h,
                    low24h = row.low_24h,
                    priceChange24h = row.price_change_24h,
                    priceChangePercentage24h = row.price_change_percentage_24h,
                    marketCapChange24h = row.market_cap_change_24h,
                    marketCapChangePercentage24h = row.market_cap_change_percentage_24h,
                    fullyDilutedValuation = row.fully_diluted_valuation,
                    circulatingSupply = row.circulating_supply,
                    totalSupply = row.total_supply,
                    maxSupply = row.max_supply,
                    ath = row.ath,
                    athChangePercentage = row.ath_change_percentage,
                    athDate = row.ath_date,
                    atl = row.atl,
                    priceChangePercentage1hInCurrency = row.price_change_percentage_1h_in_currency,
                    priceChangePercentage24hInCurrency = row.price_change_percentage_24h_in_currency,
                    priceChangePercentage7dInCurrency = row.price_change_percentage_7d_in_currency,
                    lastUpdated = row.last_updated,
                    sparklineIn7d = TokenPriceEntity.SparklineIn7d(mutableListOf(row.price ?: 0.0))
                )
            }
        }

        // Add all entities to the result list
        result.addAll(entitiesById.values)
        return result
    }
}