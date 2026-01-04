package com.mangala.wallet.model.provider.coingecko

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoingeckoPriceDto(
    @SerialName("id")
    val id: String?,
    @SerialName("symbol")
    val symbol: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("image")
    val image: String?,
    @SerialName("current_price")
    val currentPrice: String?,
    @SerialName("market_cap")
    val marketCap: String?,
    @SerialName("market_cap_rank")
    val marketCapRank: Int?,
    @SerialName("total_volume")
    val totalVolume: String?,
    @SerialName("high_24h")
    val high24h: String?,
    @SerialName("low_24h")
    val low24h: String?,
    @SerialName("price_change_24h")
    val priceChange24h: String?,
    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24h: String?,
    @SerialName("market_cap_change_24h")
    val marketCapChange24h: String?,
    @SerialName("market_cap_change_percentage_24h")
    val marketCapChangePercentage24h: String?,
    @SerialName("fully_diluted_valuation")
    val fullyDilutedValuation: String?,
    @SerialName("circulating_supply")
    val circulatingSupply: String?,
    @SerialName("total_supply")
    val totalSupply: String?,
    @SerialName("max_supply")
    val maxSupply: String?,
    val ath: String?,
    @SerialName("ath_change_percentage")
    val athChangePercentage: String?,
    @SerialName("ath_date")
    val athDate: String?,
    val atl: String?,
    val price_change_percentage_1h_in_currency: String?,
    val price_change_percentage_24h_in_currency: String?,
    val price_change_percentage_7d_in_currency: String?,
    @SerialName("last_updated")
    val lastUpdated: String?,
    @SerialName("sparkline_in_7d")
    val sparklineIn7d: SparklineIn7d? = null
) {
    @Serializable
    data class SparklineIn7d(
        val price: List<Double>?
    )
}