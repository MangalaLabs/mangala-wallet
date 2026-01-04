package com.mangala.wallet.domain.portfolio.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal

data class PricingContext(
    val asOf: String,
    val quoteCurrency: String,
    val status: String,
    val prices: Map<String, TokenPrice>
)

data class TokenPrice(
    val spot: BigDecimal,
    val price24hAgo: BigDecimal,
    val lastUpdated: String,
    val source: String
)