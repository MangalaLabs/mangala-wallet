package com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc

import kotlinx.serialization.json.JsonElement

data class RpcResponse(val id: Int, val result: JsonElement?, val error: Error?) {
    data class Error(val code: Int, val message: String)
}