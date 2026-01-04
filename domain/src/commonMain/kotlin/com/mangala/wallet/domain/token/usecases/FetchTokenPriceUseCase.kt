package com.mangala.wallet.domain.token.usecases

import com.mangala.wallet.domain.CACHE_FOR_PRICE
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.provider.coingecko.repository.CoingeckoRepository
import com.mangala.wallet.domain.token.price.repository.TokenPriceRepository
import com.mangala.wallet.model.provider.coingecko.CoinGeckoTokenPriceModel
import com.mangala.wallet.model.provider.coingecko.CoingeckoPriceDto
import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.model.token.TokenPriceEntity
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.utils.CalBalance
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class FetchTokenPriceUseCase(
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val coingeckoRepository: CoingeckoRepository,
    private val tokenPriceRepository: TokenPriceRepository
) {

    suspend operator fun invoke(
        forceReload: Boolean,
        tokenUid: String,
        sparkline: Boolean,
    ): TokenPriceEntity? {
        val currencyCode = getCurrentCurrencyCodeUseCase()

        val dataLocal =
            if (sparkline) tokenPriceRepository.getTokenWithSparklineByCoinUidsAndCurrencyCode(
                listOf(tokenUid),
                currencyCode
            ) else tokenPriceRepository.getTokenPriceByCoinIdsAndCurrencyCode(
                listOf(tokenUid),
                currencyCode
            )

        if (forceReload || dataLocal.isEmpty() || (dataLocal.isNotEmpty() && !isCached(
                dataLocal.first().lastUpdated ?: 0
            ))
        ) {
            return fetchTokenPriceAndSave(tokenUid, currencyCode, sparkline)
        }
        return dataLocal.firstOrNull()
    }

    fun getTokenPriceWithSparkline(
        forceReload: Boolean,
        tokenUid: String,
        currencyCode: String
    ): Flow<Resource<List<TokenPriceEntity>>> {
        return tokenPriceRepository.getTokenWithSparklineByCoinUidsAndCurrencyCodeFlow(
            listOf(tokenUid),
            currencyCode,
            forceReload
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getTokenPriceWithSparkline(
        forceReload: Boolean,
        tokenUids: List<String>,
    ): Flow<Resource<List<TokenPriceEntity>>> {
        return getCurrentCurrencyCodeUseCase.invokeFlow().flatMapLatest {
            tokenPriceRepository.getTokenWithSparklineByCoinUidsAndCurrencyCodeFlow(
                tokenUids,
                it,
                forceReload
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getTokenPriceWithSparkline(
        forceReload: Boolean,
        tokenScan: Map<String, TokenBalanceEntity>
    ): Flow<Resource<List<TokenBalanceModel>>> {
        return getCurrentCurrencyCodeUseCase.invokeFlow().flatMapLatest {
            tokenPriceRepository.getTokenWithSparklineByCoinUidsAndCurrencyCodeFlow(
                tokenScan,
                it,
                forceReload
            )
        }
    }

    suspend operator fun invoke(
        forceReload: Boolean,
        tokenScan: Map<String, TokenBalanceEntity>,
        sparkline: Boolean,
    ): List<TokenBalanceModel> {
        val currencyCode = getCurrentCurrencyCodeUseCase()

        val tokenModels = mutableListOf<TokenBalanceModel>()
        if(tokenScan.isNotEmpty()) {
            // TODO: Test case when we already have cached price data but tokenScan has a new coin that we didn't have in cache
            val dataLocal =
                if (sparkline) tokenPriceRepository.getTokenWithSparklineByCoinUidsAndCurrencyCode(
                    tokenScan.keys.toList(),
                    currencyCode
                ) else tokenPriceRepository.getTokenPriceByCoinIdsAndCurrencyCode(
                    tokenScan.keys.toList(),
                    currencyCode
                )

            if (dataLocal.isNotEmpty()) {
                dataLocal.forEach { tokenPriceEntity ->
                    tokenScan[tokenPriceEntity.coinUid]?.let { tokenBalanceEntity ->
                        tokenModels.add(
                            mapTokenBalanceModel(
                                tokenBalanceEntity,
                                tokenPriceEntity,
                                currencyCode
                            )
                        )
                    }
                }
            }
            if (forceReload || dataLocal.isEmpty() || (dataLocal.isNotEmpty() && !isCached(
                    dataLocal.first().lastUpdated ?: 0
                ))
            ) {
                val fetchData =
                    fetchTokenPriceAndSaveAndMapToBalanceModel(tokenScan, currencyCode, sparkline)
                if (fetchData.isNotEmpty()) {
                    tokenModels.clear()
                    tokenModels.addAll(fetchData)
                }
            }

            val combinedData = (dataLocal).associateBy { it.coinUid }

            tokenScan.forEach { (coinId, tokenBalanceEntity) ->
                val tokenPriceEntity = combinedData[coinId]
                if (tokenPriceEntity != null) {
                    tokenModels.add(mapTokenBalanceModel(tokenBalanceEntity, tokenPriceEntity, currencyCode))
                } else {
                    tokenModels.add(mapTokenBalanceModel(tokenBalanceEntity, currencyCode))
                }
            }
        }

        return tokenModels.distinctBy { it.tokenId }
    }

    suspend fun getExchangeRate(
        forceReload: Boolean,
        coinUid: String,
        currencyCode: List<String>
    ): Result<CoinGeckoTokenPriceModel> {
        return coingeckoRepository.getTokenPrice(
            forceRefresh = forceReload,
            coinUid = coinUid,
            currencyCode = currencyCode,
            include_market_cap = false,
            include_24hr_vol = false,
            include_24hr_change = false,
            include_last_updated_at = false,
            precision = SIMPLE_PRICE_PRECISION
        )
    }

    fun getExchangeRateFlow(
        forceReload: Boolean,
        coinUid: String,
        currencyCode: List<String>
    ): Flow<Resource<CoinGeckoTokenPriceModel>> {
        return coingeckoRepository.getTokenPriceFlow(
            forceRefresh = forceReload,
            coinUid = coinUid,
            currencyCode = currencyCode,
            include_market_cap = false,
            include_24hr_vol = false,
            include_24hr_change = false,
            include_last_updated_at = false,
            precision = SIMPLE_PRICE_PRECISION
        )
    }

    private fun isCached(lastUpdated: Long): Boolean{
        val currentTime = Clock.System.now()
        val cacheExpirationDuration = CACHE_FOR_PRICE.minutes
        val lastUpdatedTime = Instant.fromEpochMilliseconds(lastUpdated)
        return currentTime - lastUpdatedTime < cacheExpirationDuration
    }

    private suspend fun fetchTokenPriceAndSaveAndMapToBalanceModel(
        tokenScan: Map<String, TokenBalanceEntity>,
        currencyCode: String,sparkline: Boolean
    ): MutableList<TokenBalanceModel> {
        val tokenModels = mutableListOf<TokenBalanceModel>()
        val tokenPriceEntities = mutableListOf<TokenPriceEntity>()
        val apiResponse = coingeckoRepository.coinsMarkets(
            tokenScan.keys.joinToString(","),
            currencyCode,
            null,
            "market_cap_desc",
            100,
            sparkline,
            "1h,24h,7d",
            "en"
        )

        if(apiResponse is ApiResponse.Success){
            val coinPrices = apiResponse.body.associateBy { it.id }
            tokenScan.forEach { (coinId, tokenBalanceEntity) ->
                val tokenPrice = coinPrices[coinId]

                if (tokenPrice != null) {
                    tokenPriceEntities.add(mapTokenPriceEntity(tokenPrice, currencyCode))
                    tokenPriceRepository.deleteTokenPriceByCoinUid(coinId)
                    tokenModels.add(mapTokenBalanceModel(tokenBalanceEntity, tokenPrice, currencyCode))
                } else {
                    // Case when token is not found on CoinGecko (e.g. testnet, test tokens, ...)
                    tokenModels.add(mapTokenBalanceModel(tokenBalanceEntity, currencyCode))
                }
            }
            //after fetch token price, we need to save token price to database
            if (sparkline) {
                tokenPriceRepository.insertOrReplaceTokenPrice(tokenPriceEntities)
            } else {
                tokenPriceRepository.insertTokenPrice(tokenPriceEntities)
            }
        }else if(apiResponse is ApiResponse.Error.HttpError){
        }

        return tokenModels
    }

    private suspend fun fetchTokenPriceAndSave(
        tokenUid: String,
        currencyCode: String,
        sparkline: Boolean
    ): TokenPriceEntity? {
        val apiResponse = coingeckoRepository.coinsMarkets(
            tokenUid,
            currencyCode,
            null,
            "market_cap_desc",
            100,
            sparkline,
            "1h,24h,7d",
            "en"
        )

        if (apiResponse is ApiResponse.Success) {
            val coinPrices = apiResponse.body.firstOrNull() ?: return null
            tokenPriceRepository.deleteTokenPriceByCoinUid(tokenUid)
            val tokenPriceEntity = mapTokenPriceEntity(coinPrices, currencyCode)
            if (sparkline) {
                tokenPriceRepository.insertOrReplaceTokenPrice(listOf(tokenPriceEntity))
            } else {
                tokenPriceRepository.insertOrReplaceTokenPrice(listOf(tokenPriceEntity))
            }
            return tokenPriceEntity
        } else if (apiResponse is ApiResponse.Error.HttpError) {
            return null
        }

        return null
    }

    private fun mapTokenPriceEntity(
        tokenPrice: CoingeckoPriceDto,
        currencyCode: String
    ): TokenPriceEntity {
        return TokenPriceEntity(
            tokenPrice.id ?: "",
            currencyCode,
            tokenPrice.currentPrice,
            tokenPrice.marketCap,
            tokenPrice.marketCapRank,
            tokenPrice.totalVolume,
            tokenPrice.high24h,
            tokenPrice.low24h,
            tokenPrice.priceChange24h,
            tokenPrice.priceChangePercentage24h,
            tokenPrice.marketCapChange24h,
            tokenPrice.marketCapChangePercentage24h,
            tokenPrice.fullyDilutedValuation,
            tokenPrice.circulatingSupply,
            tokenPrice.totalSupply,
            tokenPrice.maxSupply,
            tokenPrice.ath,
            tokenPrice.athChangePercentage,
            tokenPrice.athDate,
            tokenPrice.atl,
            tokenPrice.price_change_percentage_1h_in_currency,
            tokenPrice.price_change_percentage_24h_in_currency,
            tokenPrice.price_change_percentage_7d_in_currency,
            lastUpdated = Clock.System.now().toEpochMilliseconds(),
            tokenPrice.sparklineIn7d?.toEntity()
        )
    }

    private fun mapTokenBalanceModel(
        tokenBalanceEntity: TokenBalanceEntity,
        tokenPrice: CoingeckoPriceDto,
        currencyCode: String
    ): TokenBalanceModel {
        val amount =
            CalBalance.calBalance(tokenBalanceEntity.balance, tokenBalanceEntity.contractDecimals)
        val totalAmount = amount.toString().toDouble() * tokenPrice.currentPrice?.toDouble()!!

        return TokenBalanceModel(
            tokenBalanceEntity.tokenId,
            tokenBalanceEntity.accountId,
            totalAmount,
            tokenBalanceEntity.balance,
            tokenBalanceEntity.balance24h,
            tokenBalanceEntity.balanceLocked,
            tokenBalanceEntity.orderNumber,
            tokenBalanceEntity.contractDecimals,
            tokenBalanceEntity.contractName,
            tokenBalanceEntity.contractSymbol,
            tokenBalanceEntity.contractAddress,
            tokenBalanceEntity.logoUrl,
            null,
            tokenPrice.id ?: "",
            currencyCode,
            tokenPrice.currentPrice,
            tokenPrice.marketCap,
            tokenPrice.marketCapRank,
            tokenPrice.totalVolume,
            tokenPrice.high24h,
            tokenPrice.low24h,
            tokenPrice.priceChange24h,
            tokenPrice.priceChangePercentage24h,
            tokenPrice.price_change_percentage_7d_in_currency,
            tokenPrice.marketCapChange24h,
            tokenPrice.marketCapChangePercentage24h,
            tokenPrice.sparklineIn7d?.toModel(),
        )
    }

    private fun mapTokenBalanceModel(
        tokenBalanceEntity: TokenBalanceEntity,
        currencyCode: String
    ): TokenBalanceModel {

        return TokenBalanceModel(
            tokenBalanceEntity.tokenId,
            tokenBalanceEntity.accountId,
            0.0,
            tokenBalanceEntity.balance,
            tokenBalanceEntity.balance24h,
            tokenBalanceEntity.balanceLocked,
            tokenBalanceEntity.orderNumber,
            tokenBalanceEntity.contractDecimals,
            tokenBalanceEntity.contractName,
            tokenBalanceEntity.contractSymbol,
            tokenBalanceEntity.contractAddress,
            tokenBalanceEntity.logoUrl,
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

    private fun mapTokenBalanceModel(
        tokenBalanceEntity: TokenBalanceEntity,
        tokenPrice: TokenPriceEntity,
        currencyCode: String
    ): TokenBalanceModel {
        val amount =
            CalBalance.calBalance(tokenBalanceEntity.balance, tokenBalanceEntity.contractDecimals)
        val totalAmount = amount.toString().toDouble() * tokenPrice.currentPrice?.toDouble()!!

        return TokenBalanceModel(
            tokenBalanceEntity.tokenId,
            tokenBalanceEntity.accountId,
            totalAmount,
            tokenBalanceEntity.balance,
            tokenBalanceEntity.balance24h,
            tokenBalanceEntity.balanceLocked,
            tokenBalanceEntity.orderNumber,
            tokenBalanceEntity.contractDecimals,
            tokenBalanceEntity.contractName,
            tokenBalanceEntity.contractSymbol,
            tokenBalanceEntity.contractAddress,
            tokenBalanceEntity.logoUrl,
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

    private fun CoingeckoPriceDto.SparklineIn7d.toEntity(): TokenPriceEntity.SparklineIn7d {
        return TokenPriceEntity.SparklineIn7d(
            price?.toMutableList()
        )
    }

    private fun CoingeckoPriceDto.SparklineIn7d.toModel(): TokenBalanceModel.Sparkline {
        return TokenBalanceModel.Sparkline(
            price?.toMutableList()
        )
    }

    private fun TokenPriceEntity.SparklineIn7d.toModel(): TokenBalanceModel.Sparkline {
        return TokenBalanceModel.Sparkline(
            price?.toMutableList()
        )
    }
    companion object {
        private const val SIMPLE_PRICE_PRECISION = "18"
    }
}