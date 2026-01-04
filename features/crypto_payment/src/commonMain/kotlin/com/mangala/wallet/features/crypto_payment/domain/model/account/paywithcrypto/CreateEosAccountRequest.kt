package com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto

import kotlinx.serialization.Serializable

@Serializable
class CreateEosAccountRequest (
    val newAccountName: String,
    val publicActiveKey: String,
    val publicOwnerKey: String,
    val token: String,
    val amount: String,
    val txEvmHash: String,
    val chainId: String
)