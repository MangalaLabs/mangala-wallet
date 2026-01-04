package com.mangala.wallet.model.provider.coingecko

import com.mangala.wallet.model.coin.Coin
import com.mangala.wallet.model.market.MarketTicker
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinGeckoCoinResponse(
    val id: String,
    val symbol: String,
    val name: String,
    val platforms: Map<String, String>,
    val tickers: List<MarketTickerRaw>
) {

    private fun isSmartContractAddress(v: String): Boolean {
        return v.matches("^0[xX][A-z0-9]+$".toRegex())
    }

    private fun coinCode(coins: List<Coin>, coinId: String): String? {
        return coins.firstOrNull { it.uid == coinId }?.code
    }

    val exchangeIds: List<String>
        get() = tickers.map { it.market.identifier }

    fun marketTickers(imageUrls: Map<String, String>, coins: List<Coin>): List<MarketTicker> {
        val contractAddresses = platforms.mapNotNull { (platformName, contractAddress) ->
            if (smartContractPlatforms.contains(platformName)) {
                contractAddress.lowercase()
            } else {
                null
            }
        }

        val updatedTickers = tickers.mapNotNull { raw ->
//            if(raw.last.compareTo(BigDecimal.ZERO) == 0  || raw.volume.compareTo(BigDecimal.ZERO) == 0) {
//                return@mapNotNull null
//            }

            var base = if (contractAddresses.contains(raw.base.lowercase())) {
                symbol.uppercase()
            } else {
                raw.base
            }

            var target = if (contractAddresses.contains(raw.target.lowercase())) {
                symbol.uppercase()
            } else {
                raw.target
            }

            if (isSmartContractAddress(base)) {
                val coinCode = coinCode(coins, raw.coin_id)
                if (coinCode != null) {
                    base = coinCode.uppercase()
                } else {
                    return@mapNotNull null
                }
            }

            if (isSmartContractAddress(target)) {
                val coinCode = raw.target_coin_id?.let { coinCode(coins, it) }

                if (coinCode != null) {
                    target = coinCode.uppercase()
                } else {
                    return@mapNotNull null
                }
            }

            MarketTickerRaw(
                raw.coin_id,
                base,
                target,
                raw.market,
                raw.last,
                raw.volume,
                raw.target_coin_id,
                raw.trade_url
            )
        }

        return updatedTickers.map {
            val imageUrl = imageUrls[it.market.identifier]
            var target = it.target
            var base = it.base
            var volume = it.volume
            var lastRate = it.last
            if (it.target.lowercase() == symbol.lowercase()) {
                base = symbol.uppercase()
                target = it.base
                volume *= lastRate
                lastRate = lastRate
            }
            MarketTicker(
                base,
                target,
                it.market.name,
                imageUrl,
                lastRate,
                volume,
                it.trade_url
            )
        }
    }

    companion object {
        private val smartContractPlatforms: List<String> =
            listOf("tron", "ethereum", "eos", "binance-smart-chain", "binancecoin")
    }
}

@Serializable
data class MarketTickerRaw(
    @SerialName("coin_id")
    val coin_id: String,
    val base: String,
    val target: String,
    val market: TickerMarketRaw,
    @SerialName("last")
    val last: Double,
    val volume: Double,
    @SerialName("target_coin_id")
    val target_coin_id: String?,
    @SerialName("trade_url")
    val trade_url: String?,
)

@Serializable
data class TickerMarketRaw(
    @SerialName("identifier")
    val identifier: String,
    val name: String,
)
