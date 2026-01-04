package com.mangala.wallet.model.account.domain

@Deprecated("Use AccountModel instead, since it contains address too")
data class AccountBlockchainModel(
    val account: AccountModel,
    val bip44Address: String,
    val bip49Address: String,
    val bip84Address: String
)