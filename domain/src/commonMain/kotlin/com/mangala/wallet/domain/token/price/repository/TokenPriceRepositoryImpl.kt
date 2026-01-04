package com.mangala.wallet.domain.token.price.repository

import com.mangala.wallet.domain.CACHE_FOR_PRICE
import com.mangala.wallet.local.token.price.TokenPriceLocalDataSource
import com.mangala.wallet.model.provider.coingecko.CoingeckoPriceDto
import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.model.token.TokenPriceEntity
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.provider.coingecko.CoingeckoRemoteDataSource
import com.mangala.wallet.remote.utils.networkBoundResource
import com.mangala.wallet.utils.CalBalance
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class TokenPriceRepositoryImpl(
    private val tokenPriceLocalDataSource: TokenPriceLocalDataSource,
    private val coinGeckoRemoteDataSource: CoingeckoRemoteDataSource
) : TokenPriceRepository {
    override suspend fun deleteTokenPriceByCoinUid(coinId: String) {
        tokenPriceLocalDataSource.deleteTokenPriceByCoinId(coinId)
    }

    override suspend fun getTokenPriceByCoinId(coinId: String): List<TokenPriceEntity> {
        return tokenPriceLocalDataSource.getTokenPriceByCoinId(coinId)
    }

    override suspend fun getTokenPriceByCoinIdAndCurrencyCode(
        coinId: String,
        currencyCode: String
    ): List<TokenPriceEntity> {
        return tokenPriceLocalDataSource.getTokenPriceByCoinIdAndCurrencyCode(coinId, currencyCode)
    }

    override suspend fun getTokenPriceByCoinIdsAndCurrencyCode(
        coinIds: List<String>,
        currencyCode: String
    ): List<TokenPriceEntity> {
        return tokenPriceLocalDataSource.getTokenPriceByCoinIdsAndCurrencyCode(coinIds, currencyCode)
    }

    override suspend fun getTokenWithSparklineByCoinUidsAndCurrencyCode(
        coinIds: List<String>,
        currencyCode: String
    ): List<TokenPriceEntity> {
        return tokenPriceLocalDataSource.getTokenWithSparklineByCoinUidsAndCurrencyCode(coinIds, currencyCode)
    }

    override fun getTokenWithSparklineByCoinUidsAndCurrencyCodeFlow(
        coinIds: List<String>,
        currencyCode: String,
        forceReload: Boolean
    ): Flow<Resource<List<TokenPriceEntity>>> = networkBoundResource(
        query = {
            tokenPriceLocalDataSource.getTokenWithSparklineByCoinUidsAndCurrencyCodeFlow(
                coinIds,
                currencyCode
            )
        },
        fetch = {
            coinGeckoRemoteDataSource.coinsMarkets(
                coinIds.joinToString(","),
                currencyCode,
                null,
                "market_cap_desc",
                100,
                sparkline = true,
                "1h,24h,7d",
                "en"
            )
        },
        saveFetchResult = {
            val lastUpdated = Clock.System.now().toEpochMilliseconds()

            val tokenPriceEntities = it.mapNotNull { dto ->
                dto.id?.let { coinId ->
                    tokenPriceLocalDataSource.deleteTokenPriceByCoinId(coinId)

                    dto.toTokenPriceEntity(currencyCode, lastUpdated)
                }
            }

            tokenPriceLocalDataSource.insertOrReplaceTokenPrice(tokenPriceEntities)
        },
        entityToDomain = {
            it
        },
        shouldFetch = {
            forceReload || it.isEmpty() || (it.isNotEmpty() && !isCached(it.first().lastUpdated ?: 0))
        }
    )

    override fun getTokenWithSparklineByCoinUidsAndCurrencyCodeFlow(
        tokenScan: Map<String, TokenBalanceEntity>,
        currencyCode: String,
        forceReload: Boolean
    ): Flow<Resource<List<TokenBalanceModel>>> = networkBoundResource(
        query = {
            tokenPriceLocalDataSource.getTokenWithSparklineByCoinUidsAndCurrencyCodeFlow(
                tokenScan.keys.toList(),
                currencyCode
            )
        },
        fetch = {
            coinGeckoRemoteDataSource.coinsMarkets(
                tokenScan.keys.joinToString(","),
                currencyCode,
                null,
                "market_cap_desc",
                100,
                true,
                "1h,24h,7d",
                "en"
            )
        },
        saveFetchResult = {
            val tokenPriceEntities = mutableListOf<TokenPriceEntity>()
            val coinPrices = it.associateBy { it.id }

            val lastUpdated = Clock.System.now().toEpochMilliseconds()

            tokenScan.forEach { (coinId, _) ->
                coinPrices[coinId]?.let { tokenPrice ->
                    tokenPriceEntities.add(tokenPrice.toTokenPriceEntity(currencyCode, lastUpdated))
                    tokenPriceLocalDataSource.deleteTokenPriceByCoinId(coinId)
                }
            }

            tokenPriceLocalDataSource.insertOrReplaceTokenPrice(tokenPriceEntities)
        },
        entityToDomain = { tokenPrices ->
            val tokenModels = mutableListOf<TokenBalanceModel>()
            val combinedData = tokenPrices.associateBy { it.coinUid }

            tokenScan.forEach { (coinId, tokenBalanceEntity) ->
                val tokenPriceEntity = combinedData[coinId]
                if (tokenPriceEntity != null) {
                    tokenModels.add(tokenBalanceEntity.mapTokenBalanceModel(tokenPriceEntity, currencyCode))
                } else {
                    tokenModels.add(tokenBalanceEntity.mapTokenBalanceModel(currencyCode))
                }
            }

            tokenModels.distinctBy { it.tokenId }
        },
        shouldFetch = { cachedData ->
            forceReload || cachedData.isEmpty() || (cachedData.isNotEmpty() && !isCached(cachedData.first().lastUpdated ?: 0))
        }
    )

    override suspend fun insertTokenPrice(tokenPrices: List<TokenPriceEntity>) {
        return tokenPriceLocalDataSource.insertTokenPrice(tokenPrices)
    }

    override suspend fun insertOrReplaceTokenPrice(tokenPrices: List<TokenPriceEntity>) {
        return tokenPriceLocalDataSource.insertOrReplaceTokenPrice(tokenPrices)
    }

    override suspend fun updateTokenPrice(tokenPrices: List<TokenPriceEntity>) {
        tokenPriceLocalDataSource.updateTokenPrice(tokenPrices)
    }

    private fun CoingeckoPriceDto.toTokenPriceEntity(
        currencyCode: String,
        lastUpdated: Long // Pass this in to ensure that all entities have the same lastUpdated time
    ): TokenPriceEntity {
        return TokenPriceEntity(
            id ?: "",
            currencyCode,
            currentPrice,
            marketCap,
            marketCapRank,
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
            price_change_percentage_1h_in_currency,
            price_change_percentage_24h_in_currency,
            price_change_percentage_7d_in_currency,
            lastUpdated = lastUpdated,
            sparklineIn7d?.toEntity()
        )
    }

    private fun TokenBalanceEntity.mapTokenBalanceModel(
        tokenPrice: TokenPriceEntity,
        currencyCode: String
    ): TokenBalanceModel {
        val amount =
            CalBalance.calBalance(balance, contractDecimals)
        val totalAmount = amount.toString().toDouble() * tokenPrice.currentPrice?.toDouble()!!

        return TokenBalanceModel(
            tokenId,
            accountId,
            totalAmount,
            balance,
            balance24h,
            balanceLocked,
            orderNumber,
            contractDecimals,
            contractName,
            contractSymbol,
            contractAddress,
            logoUrl,
            null,
            tokenPrice.coinUid,
            currencyCode,
            tokenPrice.currentPrice,
            tokenPrice.marketCap,
            tokenPrice.marketCapRank,
            tokenPrice.totalVolume,
            tokenPrice.high24h,
            tokenPrice.low24h,
            tokenPrice.priceChange24h,
            tokenPrice.priceChangePercentage24h,
            tokenPrice.priceChangePercentage7dInCurrency,
            tokenPrice.marketCapChange24h,
            tokenPrice.marketCapChangePercentage24h,
            tokenPrice.sparklineIn7d?.toModel(),
        )
    }

    private fun TokenBalanceEntity.mapTokenBalanceModel(
        currencyCode: String
    ): TokenBalanceModel {

        return TokenBalanceModel(
            tokenId,
            accountId,
            0.0,
            balance,
            balance24h,
            balanceLocked,
            orderNumber,
            contractDecimals,
            contractName,
            contractSymbol,
            contractAddress,
            logoUrl,
            null,
            "",
            currencyCode,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
        )
    }

    private fun CoingeckoPriceDto.SparklineIn7d.toEntity(): TokenPriceEntity.SparklineIn7d {
        return TokenPriceEntity.SparklineIn7d(
            price?.toMutableList()
        )
    }

    private fun TokenPriceEntity.SparklineIn7d.toModel(): TokenBalanceModel.Sparkline {
        return TokenBalanceModel.Sparkline(
            price?.toMutableList()
        )
    }

    private fun isCached(lastUpdated: Long): Boolean{
        val currentTime = Clock.System.now()
        val cacheExpirationDuration = CACHE_FOR_PRICE.minutes
        val lastUpdatedTime = Instant.fromEpochMilliseconds(lastUpdated)
        return currentTime - lastUpdatedTime < cacheExpirationDuration
    }
}