package com.mangala.wallet.model.coin

import com.ionspin.kotlin.bignum.decimal.BigDecimal

class CoinHistoricalPrice(
    val coinUid: String,
    val currencyCode: String,
    val value: BigDecimal,
    val timestamp: Long
)
