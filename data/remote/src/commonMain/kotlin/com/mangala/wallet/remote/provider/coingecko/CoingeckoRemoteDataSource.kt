package com.mangala.wallet.remote.provider.coingecko

import com.mangala.wallet.model.provider.coingecko.CoinGeckoCoinResponse
import com.mangala.wallet.model.provider.coingecko.CoingeckoPriceDto
import com.mangala.wallet.model.provider.coingecko.CoingeckoPriceHistoryResponse
import com.mangala.wallet.model.provider.coingecko.CoinsMarketsResponse
import com.mangala.wallet.remote.BuildKonfig
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.serialization.json.JsonObject


class CoingeckoRemoteDataSource(private val api: CoingeckoApi) {

    suspend fun marketTickers(coinGeckoId: String): CoinGeckoCoinResponse {
        return api.marketTickers(
            coinGeckoId,
            "true",
            "false",
            "false",
            "false",
            "false",
            "false",
        )
    }

    suspend fun getTokenPrice(
        ids: String,
        vs_currencies: String,
        include_market_cap: Boolean,
        include_24hr_vol: Boolean,
        include_24hr_change: Boolean,
        include_last_updated_at: Boolean,
        precision: String,
    ): ApiResponse<JsonObject, String> = safeApiCall {
        api.simplePrice(
            ids,
            vs_currencies,
            include_market_cap,
            include_24hr_vol,
            include_24hr_change,
            include_last_updated_at,
            precision
        )
    }

    suspend fun coinsMarkets(
        ids: String,
        vs_currencies: String, //(usd, eur, jpy, etc.)
        category: String?,
        order: String, //market_cap_asc, market_cap_desc, volume_asc, volume_desc, id_asc, id_desc
        per_page: Int, //1..250
        sparkline: Boolean,
        price_change_percentage: String,//Include price change percentage in 1h, 24h, 7d, 14d, 30d, 200d, 1y (eg. '1h,24h,7d' comma-separated, invalid values will be discarded)
        locale: String, //valid values: ar, bg, cs, da, de, el, en, es, fi, fr, he, hi, hr, hu, id, it, ja, ko, lt, nl, no, pl, pt, ro, ru, sk, sl, sv, th, tr, uk, vi, zh, zh-tw
    ): ApiResponse<List<CoingeckoPriceDto>, String> = safeApiCall {
        api.coinsMarkets(
            ids,
            vs_currencies,
            category,
            order,
            per_page,
            sparkline,
            price_change_percentage,
            locale
        )
    }

    suspend fun priceHistory(
        coinId: String,
        date: String,
        localization: Boolean,
    ): ApiResponse<CoingeckoPriceHistoryResponse, String> =
        safeApiCall {
            api.priceHistory(coinId, date, localization)
        }
}