package com.mangala.wallet.features.chains.antelope_base.domain.model.rex

data class AntelopeRexPoolInfo(
    val totalLent: String,
    val totalUnlent: String,
    val totalRent: String,
    val totalLendable: String,
    val totalRex: String,
    val nameBidProceeds: String,
    val loanNum: Int
)