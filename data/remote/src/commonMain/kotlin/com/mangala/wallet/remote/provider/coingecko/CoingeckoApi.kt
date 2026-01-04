package com.mangala.wallet.remote.provider.coingecko

import com.mangala.wallet.model.provider.coingecko.CoinGeckoCoinResponse
import com.mangala.wallet.model.provider.coingecko.CoingeckoPriceDto
import com.mangala.wallet.model.provider.coingecko.CoingeckoPriceHistoryResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.serialization.json.JsonObject

interface CoingeckoApi {

    @GET("ping")
    suspend fun ping(): String

    @GET("simple/price")
    suspend fun simplePrice(
        @Query("ids") ids: String,
        @Query("vs_currencies") vs_currencies: String,
        @Query("include_market_cap") include_market_cap: Boolean,
        @Query("include_24hr_vol") include_24hr_vol: Boolean,
        @Query("include_24hr_change") include_24hr_change: Boolean,
        @Query("include_last_updated_at") include_last_updated_at: Boolean,
        @Query("precision") precision: String,
    ): JsonObject

    @GET("coins/markets")
    suspend fun coinsMarkets(
        @Query("ids") ids: String,
        @Query("vs_currency") vs_currencies: String, //(usd, eur, jpy, etc.)
        @Query("category") category: String?,
        @Query("order") order: String, //market_cap_asc, market_cap_desc, volume_asc, volume_desc, id_asc, id_desc
        @Query("per_page") per_page: Int, //1..250
        @Query("sparkline") sparkline: Boolean,
        @Query("price_change_percentage") price_change_percentage: String,//Include price change percentage in 1h, 24h, 7d, 14d, 30d, 200d, 1y (eg. '1h,24h,7d' comma-separated, invalid values will be discarded)
        @Query("locale") locale: String, //valid values: ar, bg, cs, da, de, el, en, es, fi, fr, he, hi, hr, hu, id, it, ja, ko, lt, nl, no, pl, pt, ro, ru, sk, sl, sv, th, tr, uk, vi, zh, zh-tw
    ): List<CoingeckoPriceDto>

    @GET("coins/{coinId}")
    suspend fun marketTickers(
        @Path("coinId") coinId: String,
        @Query("tickers") tickers: String,
        @Query("localization") localization: String,
        @Query("market_data") marketData: String,
        @Query("community_data") communityData: String,
        @Query("developer_data") developerData: String,
        @Query("sparkline") sparkline: String
    ): CoinGeckoCoinResponse

    @GET("coins/{coinId}/history")
    suspend fun priceHistory(
        @Path("coinId") coinId: String,
        @Query("date") date: String,
        @Query("localization") localization: Boolean,
    ): CoingeckoPriceHistoryResponse
}