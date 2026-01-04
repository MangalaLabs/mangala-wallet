package com.linh.antelope_qr.domain.model

import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import kotlinx.serialization.Serializable

@Serializable
data class SignedTransactionResponse(
    val signature: String,
    val signTransactionRequest: SignTransactionRequest
)