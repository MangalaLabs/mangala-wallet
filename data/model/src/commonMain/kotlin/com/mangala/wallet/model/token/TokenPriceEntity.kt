package com.mangala.wallet.model.token

@kotlinx.serialization.Serializable
data class TokenPriceEntity(
    val coinUid: String,
    val currencyCode: String,
    val currentPrice: String?,
    val marketCap: String?,
    val marketCapRank: Int?,
    val totalVolume: String?,
    val high24h: String?,
    val low24h: String?,
    val priceChange24h: String?,
    val priceChangePercentage24h: String?,
    val marketCapChange24h: String?,
    val marketCapChangePercentage24h: String?,
    val fullyDilutedValuation: String?,
    val circulatingSupply: String?,
    val totalSupply: String?,
    val maxSupply: String?,
    val ath: String?,
    val athChangePercentage: String?,
    val athDate: String?,
    val atl: String?,
    val priceChangePercentage1hInCurrency: String?,
    val priceChangePercentage24hInCurrency: String?,
    val priceChangePercentage7dInCurrency: String?,
    val lastUpdated: Long?,
    val sparklineIn7d: SparklineIn7d?,
) {
    @kotlinx.serialization.Serializable
    data class SparklineIn7d(
        val price: MutableList<Double>?
    )
}