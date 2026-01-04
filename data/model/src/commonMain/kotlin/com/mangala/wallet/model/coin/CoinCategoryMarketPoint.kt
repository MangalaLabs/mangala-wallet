package com.mangala.wallet.model.coin

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.SerialName

data class CoinCategoryMarketPoint(
    val timestamp: Long,
    @SerialName("market_cap")
    val marketCap: BigDecimal,
)