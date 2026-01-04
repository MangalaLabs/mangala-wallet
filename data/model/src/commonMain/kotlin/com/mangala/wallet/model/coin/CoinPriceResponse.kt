package com.mangala.wallet.model.coin

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.SerialName

data class CoinPriceResponse(
    val uid: String,
    val price: BigDecimal?,
    @SerialName("price_change_24h")
    val priceChange: BigDecimal?,
    @SerialName("last_updated")
    val lastUpdated: Long?
) {
    fun coinPrice(currencyCode: String) = when {
        price == null || lastUpdated == null -> null
        else -> CoinPrice(uid, currencyCode, price, priceChange, lastUpdated)
    }
}
