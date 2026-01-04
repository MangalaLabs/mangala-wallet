package com.mangala.wallet.features.chains.antelope_base.domain.model.multisig

data class AccountWeight(
    val accountWeightMap: Map<String?, Long>,
    val threshold: Long
)