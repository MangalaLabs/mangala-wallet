package com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc

@kotlinx.serialization.Serializable
data class InfuraEstimateGasRequest(
    val jsonrpc: String?,
    val method: String?,
    val params: List<MutableMap<String, String?>>?,
    val id: Int?
)