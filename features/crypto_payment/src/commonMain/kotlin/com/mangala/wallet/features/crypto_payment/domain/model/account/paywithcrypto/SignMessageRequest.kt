package com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto

import kotlinx.serialization.Serializable

@Serializable
class SignMessageRequest (
    val newAccountName: String,
    val publicActiveKey: String,
    val publicOwnerKey: String,
    val token: String,
    val amount: String,
    val evmAddress: String,
    val chainId: String
)