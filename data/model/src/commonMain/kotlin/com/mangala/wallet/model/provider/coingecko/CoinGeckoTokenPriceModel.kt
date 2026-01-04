package com.mangala.wallet.model.provider.coingecko

import com.ionspin.kotlin.bignum.decimal.BigDecimal

data class CoinGeckoTokenPriceModel(
    val data: Map<String, BigDecimal>
)
