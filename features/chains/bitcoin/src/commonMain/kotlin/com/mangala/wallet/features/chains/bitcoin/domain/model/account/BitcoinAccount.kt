package com.mangala.wallet.features.chains.bitcoin.domain.model.account

data class BitcoinAccount(
    val accountId: String,
    val name: String?,
    val sortingOrder: Int?,
    val bip44Address: String,
    val bip49Address: String,
    val bip84Address: String
)