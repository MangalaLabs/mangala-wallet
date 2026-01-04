package com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc

abstract class JsonRpc(
    val method: String,
    val params: List<Any>
) {

    val jsonrpc: String = "2.0"
    var id: Int = 1

//    fun parseResponse(response: RpcResponse): T {
//        if (response.error != null) {
//            throw ResponseError.RpcError(response.error)
//        }
//        return parseResult(response.result)
//    }
//
//    fun parseResult(result: JsonElement?): T {
//        return try {
//            Json.decodeFromString<Any>(result.toString()) as T
//            gson.fromJson(result, typeOfResult) as T
//        } catch (error: Throwable) {
//            throw ResponseError.InvalidResult(result.toString())
//        }
//    }

    sealed class ResponseError : Throwable() {
        class RpcError(val error: RpcResponse.Error) : ResponseError()
        class InvalidResult(val result: Any?) : ResponseError()
    }
}
