package com.mangala.wallet.features.chains.evmcompatible.model

import com.mangala.wallet.utils.ByteArrayAsBase64StringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SignedTransactionResponse(
    val signTransactionRequest: SignTransactionRequest,
    val v: Int,
    @Serializable(with = ByteArrayAsBase64StringSerializer::class)
    val r: ByteArray,
    @Serializable(with = ByteArrayAsBase64StringSerializer::class)
    val s: ByteArray
) {
    fun getSignature(): Signature = Signature(v, r, s)

    constructor(
        signTransactionRequest: SignTransactionRequest,
        signature: Signature
    ): this(signTransactionRequest, signature.v, signature.r, signature.s)
}