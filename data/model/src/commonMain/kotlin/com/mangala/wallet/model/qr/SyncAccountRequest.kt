package com.mangala.wallet.model.qr

import kotlinx.serialization.Serializable

@Serializable
data class SyncAccountRequest(
    val walletId: String,
    val walletName: String,
    val walletPublicKey: String,
    val accountId: String,
    val accountName: String,
    val derivationPathIndex: Int,
    val bip44Address: String,
    val bip49Address: String,
    val bip84Address: String
)