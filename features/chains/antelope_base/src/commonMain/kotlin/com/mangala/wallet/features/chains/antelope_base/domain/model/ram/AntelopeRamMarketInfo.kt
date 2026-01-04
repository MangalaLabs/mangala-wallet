package com.mangala.wallet.features.chains.antelope_base.domain.model.ram

data class AntelopeRamMarketInfo(
    val supply: String,
    val base: Pair,
    val quote: Pair
) {
    data class Pair(
        val balance: String,
        val weight: Double
    )
}