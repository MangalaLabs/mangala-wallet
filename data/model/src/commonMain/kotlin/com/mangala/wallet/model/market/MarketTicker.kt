package com.mangala.wallet.model.market

import com.ionspin.kotlin.bignum.decimal.BigDecimal

data class MarketTicker(
    val base: String,
    val target: String,
    val marketName: String,
    val marketImageUrl: String?,
    val rate: Double,
    val volume: Double,
    val tradeUrl: String?,
)
