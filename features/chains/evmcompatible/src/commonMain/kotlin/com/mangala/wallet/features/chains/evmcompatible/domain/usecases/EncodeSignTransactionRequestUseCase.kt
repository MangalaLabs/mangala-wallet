package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EncodeSignTransactionRequestUseCase(
    private val json: Json
) {
    operator fun invoke(signTransactionRequest: SignTransactionRequest): String {
        return json.encodeToString(signTransactionRequest)
    }

    fun decode(encodedSignTransactionRequest: String): SignTransactionRequest {
        return json.decodeFromString(encodedSignTransactionRequest)
    }
}