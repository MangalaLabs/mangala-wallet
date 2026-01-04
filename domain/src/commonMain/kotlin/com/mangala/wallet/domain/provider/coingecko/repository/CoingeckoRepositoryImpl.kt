package com.mangala.wallet.domain.provider.coingecko.repository

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.domain.provider.coingecko.mapper.toCoinGeckoTokenPriceModel
import com.mangala.wallet.local.token.exchangerate.TokenExchangeRateLocalDataSource
import com.mangala.wallet.local.token.exchangerate.TokenExchangeRateMetadataLocalDataSource
import com.mangala.wallet.model.provider.coingecko.CoinGeckoCoinResponse
import com.mangala.wallet.model.provider.coingecko.CoinGeckoTokenPriceModel
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.provider.coingecko.CoingeckoRemoteDataSource
import com.mangala.wallet.remote.utils.cachedResource
import com.mangala.wallet.remote.utils.networkBoundResource
import com.mangala.wallet.utils.Constants.TOKEN_EXCHANGE_RATE_CACHE_TIMEOUT_MILLIS
import com.mangala.wallet.utils.ext.jsonObjectOrNull
import commangalawalletdatabase.TokenExchangeRateEntity
import commangalawalletdatabase.TokenExchangeRateMetadataEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull

class CoingeckoRepositoryImpl(
    private val network: CoingeckoRemoteDataSource,
    private val localDataSource: TokenExchangeRateLocalDataSource,
    private val tokenExchangeRateMetadataLocalDataSource: TokenExchangeRateMetadataLocalDataSource
) :
    CoingeckoRepository {
    override suspend fun getMarketTicker(coinGeckoId: String): CoinGeckoCoinResponse {
        return network.marketTickers(coinGeckoId)
    }

    override suspend fun getTokenPrice(
        forceRefresh: Boolean,
        coinUid: String,
        currencyCode: List<String>,
        include_market_cap: Boolean,
        include_24hr_vol: Boolean,
        include_24hr_change: Boolean,
        include_last_updated_at: Boolean,
        precision: String
    ): Result<CoinGeckoTokenPriceModel> {
        return cachedResource(
            query = {
                localDataSource.getTokenExchangeRateByCoinUid(coinUid)
            },
            fetch = {
                fetchTokenPrice(
                    coinUid,
                    currencyCode,
                    include_market_cap,
                    include_24hr_vol,
                    include_24hr_change,
                    include_last_updated_at,
                    precision
                )
            },
            saveFetchResult = {
                saveGetTokenPriceResult(it, coinUid)
            },
            shouldFetch = {
                checkShouldFetchTokenPrice(coinUid, forceRefresh)
            },
            entityToDomain = { it.toCoinGeckoTokenPriceModel() }
        )
    }

    override fun getTokenPriceFlow(
        forceRefresh: Boolean,
        coinUid: String,
        currencyCode: List<String>,
        include_market_cap: Boolean,
        include_24hr_vol: Boolean,
        include_24hr_change: Boolean,
        include_last_updated_at: Boolean,
        precision: String
    ): Flow<Resource<CoinGeckoTokenPriceModel>> = networkBoundResource(
        query = {
            localDataSource.getTokenExchangeRateByCoinUidFlow(coinUid)
        },
        fetch = {
            fetchTokenPrice(
                coinUid,
                currencyCode,
                include_market_cap,
                include_24hr_vol,
                include_24hr_change,
                include_last_updated_at,
                precision
            )
        },
        saveFetchResult = {
            saveGetTokenPriceResult(it, coinUid)
        },
        shouldFetch = { checkShouldFetchTokenPrice(coinUid, forceRefresh) },
        entityToDomain = { it.toCoinGeckoTokenPriceModel() }
    )

    override suspend fun coinsMarkets(
        ids: String,
        currencyCode: String,
        category: String?,
        order: String,
        per_page: Int,
        sparkline: Boolean,
        price_change_percentage: String,
        locale: String
    ) = network.coinsMarkets(
        ids,
        currencyCode,
        category,
        order,
        per_page,
        sparkline,
        price_change_percentage,
        locale
    )

    private suspend fun fetchTokenPrice(
        coinUid: String,
        currencyCode: List<String>,
        include_market_cap: Boolean,
        include_24hr_vol: Boolean,
        include_24hr_change: Boolean,
        include_last_updated_at: Boolean,
        precision: String
    ) = network.getTokenPrice(
        coinUid,
        currencyCode.joinToString(","),
        include_market_cap,
        include_24hr_vol,
        include_24hr_change,
        include_last_updated_at,
        precision
    ).map {
        val priceJsonObject = it[coinUid]?.jsonObjectOrNull
        CoinGeckoTokenPriceModel(
            data = currencyCode.mapNotNull { unitToken ->
                // Attempt to retrieve the price for each unit token
                val price =
                    (priceJsonObject?.get(unitToken) as? JsonPrimitive)?.doubleOrNull
                price?.let { unitToken to price.toBigDecimal() }  // Only map non-null values
            }.toMap()
        )
    }

    private suspend fun saveGetTokenPriceResult(
        it: CoinGeckoTokenPriceModel,
        coinUid: String
    ) {
        val updatedAt = Clock.System.now().toEpochMilliseconds()

        val items = it.data.map { (currency, rate) ->
            TokenExchangeRateEntity(
                coin_uid = coinUid,
                quote_currency = currency,
                rate = rate.toString(),
                updated_at = updatedAt
            )
        }

        localDataSource.insertOrReplaceTokenExchangeRate(items)
        tokenExchangeRateMetadataLocalDataSource.insertTokenExchangeRateMetadata(
            TokenExchangeRateMetadataEntity(
                coin_uid = coinUid,
                updated_at = updatedAt
            )
        )
    }

    private suspend fun checkShouldFetchTokenPrice(coinUid: String, forceRefresh: Boolean): Boolean {
        val lastUpdatedTimestamp =
            tokenExchangeRateMetadataLocalDataSource.getLastUpdatedTimestamp(coinUid) ?: 0L

        return lastUpdatedTimestamp + TOKEN_EXCHANGE_RATE_CACHE_TIMEOUT_MILLIS <= Clock.System.now()
            .toEpochMilliseconds() || forceRefresh
    }
}
