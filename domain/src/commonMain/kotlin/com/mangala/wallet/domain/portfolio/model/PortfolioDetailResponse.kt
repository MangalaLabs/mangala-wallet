package com.mangala.wallet.domain.portfolio.model

data class PortfolioDetailResponse(
    val portfolio: Portfolio,
    val pricingContext: PricingContext
)