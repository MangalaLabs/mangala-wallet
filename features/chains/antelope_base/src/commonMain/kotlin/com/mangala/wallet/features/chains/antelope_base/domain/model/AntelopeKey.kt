package com.mangala.wallet.features.chains.antelope_base.domain.model

data class AntelopeKey(
    val id: String,
    val key: String,
    val weight: Int,
    val isSynced: Boolean
)