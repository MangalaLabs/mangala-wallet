package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.mangala.wallet.model.provider.JsonRpcNodeResponse
import kotlinx.serialization.json.Json

class ParseNodeResponseUseCase(
    private val parsingJson: Json
) {

    operator fun invoke(response: String): String {
        val jsonRpcNodeResponse = try {
            // TODO: Upgrade to Result wrapper for more robust error handling
            parsingJson.decodeFromString(JsonRpcNodeResponse.serializer(), response)
        } catch (e: Exception) {
            return ""
        }
        return jsonRpcNodeResponse.result.orEmpty() // TODO: Check fail case
    }
}