package com.mangala.wallet.model.provider.coingecko

data class CoinsMarketsResponse(
    val id: String?,
    val symbol: String?,
    val name: String?,
    val image: String?,
    val current_price: Double?,
    val market_cap: Long?,
    val market_cap_rank: Int?,
    val fully_diluted_valuation: Long?,
    val total_volume: Long?,
    val high_24h: Double?,
    val low_24h: Double?,
    val price_change_24h: Double?,
    val price_change_percentage_24h: Double?,
    val market_cap_change_24h: Double?,
    val market_cap_change_percentage_24h: Double?,
    val circulating_supply: Long?,
    val total_supply: Long?,
    val max_supply: Long?,
    val ath: Double?,
    val ath_change_percentage: Double?,
    val ath_date: String?,
    val atl: Double?,
    val atl_change_percentage: Double?,
    val atl_date: String?,
    val roi: Any?,
    val last_updated: String?,
    val sparkline_in_7d: SparklineIn7d?,
    val price_change_percentage_1h_in_currency: Double?,
    val price_change_percentage_24h_in_currency: Double?,
    val price_change_percentage_7d_in_currency: Double?
) {
    data class SparklineIn7d(
        val price: List<Double>?
    )
}
