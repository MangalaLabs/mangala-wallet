package com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto

import kotlinx.serialization.Serializable

@Serializable
class SignMessageResponse (val data: SignMessageResponseData)

@Serializable
class SignMessageResponseData (
    val newAccountName: String,
    val publicActiveKey: String,
    val publicOwnerKey: String,
    val token: String,
    val amount: String,
    val nonce: Long,
    val evmAddress: String,
    val signature: String,
    val chainId: String
)