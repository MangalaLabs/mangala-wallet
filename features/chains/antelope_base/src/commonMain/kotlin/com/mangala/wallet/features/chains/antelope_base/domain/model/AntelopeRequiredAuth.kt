package com.mangala.wallet.features.chains.antelope_base.domain.model

data class AntelopeRequiredAuth(
    val threshold: Int,
    val keys: List<AntelopeKey>,
    val accounts: List<AntelopeRequiredAuthAccount>,
    val waits: List<AntelopeRequiredAuthWaits>
)