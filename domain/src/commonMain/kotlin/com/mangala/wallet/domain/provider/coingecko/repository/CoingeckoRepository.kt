package com.mangala.wallet.domain.provider.coingecko.repository

import com.mangala.wallet.model.provider.coingecko.CoinGeckoCoinResponse
import com.mangala.wallet.model.provider.coingecko.CoinGeckoTokenPriceModel
import com.mangala.wallet.model.provider.coingecko.CoingeckoPriceDto
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import kotlinx.coroutines.flow.Flow

interface CoingeckoRepository {

    suspend fun getMarketTicker(coinGeckoId: String): CoinGeckoCoinResponse
    suspend fun getTokenPrice(
        forceRefresh: Boolean,
        coinUid: String,
        currencyCode: List<String>,
        include_market_cap: Boolean,
        include_24hr_vol: Boolean,
        include_24hr_change: Boolean,
        include_last_updated_at: Boolean,
        precision: String,
    ): Result<CoinGeckoTokenPriceModel>
    fun getTokenPriceFlow(
        forceRefresh: Boolean,
        coinUid: String,
        currencyCode: List<String>,
        include_market_cap: Boolean,
        include_24hr_vol: Boolean,
        include_24hr_change: Boolean,
        include_last_updated_at: Boolean,
        precision: String
    ): Flow<Resource<CoinGeckoTokenPriceModel>>

    suspend fun coinsMarkets(
        ids: String,
        vs_currencies: String, //(usd, eur, jpy, etc.)
        category: String?,
        order: String, //market_cap_asc, market_cap_desc, volume_asc, volume_desc, id_asc, id_desc
        per_page: Int, //1..250
        sparkline: Boolean,
        price_change_percentage: String,//Include price change percentage in 1h, 24h, 7d, 14d, 30d, 200d, 1y (eg. '1h,24h,7d' comma-separated, invalid values will be discarded)
        locale: String, //valid values: ar, bg, cs, da, de, el, en, es, fi, fr, he, hi, hr, hu, id, it, ja, ko, lt, nl, no, pl, pt, ro, ru, sk, sl, sv, th, tr, uk, vi, zh, zh-tw
    ): ApiResponse<List<CoingeckoPriceDto>, String>
}