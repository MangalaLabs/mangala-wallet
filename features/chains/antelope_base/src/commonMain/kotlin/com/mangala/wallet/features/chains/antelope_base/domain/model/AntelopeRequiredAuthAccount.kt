package com.mangala.wallet.features.chains.antelope_base.domain.model

data class AntelopeRequiredAuthAccount(
    val permission: Permission,
    val weight: Long
) {
    data class Permission(
        val actor: String,
        val permission: String
    )
}

