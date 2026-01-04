package com.mangala.wallet.model.provider

import kotlinx.serialization.Serializable

@Serializable
data class JsonRpcNodeResponse(
    val jsonrpc: String?,
    val id: Int?,
    val result: String? = null,
    val error: NodeResponseError? = null
)

@Serializable
data class NodeResponseError(
    val code: Int,
    val message: String
)
