package com.mangala.wallet.domain.token.historicalprice.repository

import com.mangala.wallet.domain.token.historicalprice.mapper.toTokenHistoricalPrice
import com.mangala.wallet.local.token.historicalprice.TokenHistoricalPriceLocalDataSource
import com.mangala.wallet.model.token.TokenHistoricalPrice
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.provider.coingecko.CoingeckoRemoteDataSource
import com.mangala.wallet.remote.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow

class TokenHistoricalPriceRepositoryImpl(
    private val localDataSource: TokenHistoricalPriceLocalDataSource,
    private val networkDataSource: CoingeckoRemoteDataSource
) : TokenHistoricalPriceRepository {

    override suspend fun fetchHistoricalTokenPrice(
        coinGeckoId: String,
        date: String
    ): Result<TokenHistoricalPrice> {
        val cachedResult = localDataSource.getPriceByDateAndId(coinGeckoId, date)

        if (cachedResult != null) {
            return Result.success(cachedResult.toTokenHistoricalPrice())
        }

        val networkResult = networkDataSource.priceHistory(
            coinId = coinGeckoId,
            date = date,
            localization = false
        )

        return when (networkResult) {
            is ApiResponse.Success -> {
                val response = networkResult.body.toTokenHistoricalPrice(date)
                localDataSource.saveHistoricalPrice(response)
                Result.success(response)
            }
            is ApiResponse.Error -> {
                Result.failure(Exception())
            }
        }
    }

    override fun fetchHistoricalTokenPriceFlow(
        coinGeckoId: String,
        date: String,
        forceRefresh: Boolean
    ): Flow<Resource<TokenHistoricalPrice?>> {
        return networkBoundResource(
            query = { localDataSource.getPriceByDateAndIdFlow(coinGeckoId, date) },
            fetch = {
                networkDataSource.priceHistory(
                    coinId = coinGeckoId,
                    date = date,
                    localization = false
                )
            },
            saveFetchResult = { result ->
                localDataSource.saveHistoricalPrice(result.toTokenHistoricalPrice(date))
            },
            shouldFetch = { cachedData -> cachedData == null },
            entityToDomain = {
                it?.toTokenHistoricalPrice()
            }
        )
    }
}