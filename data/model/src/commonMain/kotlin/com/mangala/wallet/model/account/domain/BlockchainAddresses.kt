package com.mangala.wallet.model.account.domain

data class BlockchainAddresses(
    val bip44Address: String,
    val bip49Address: String,
    val bip84Address: String,
    val publicKey: String
)