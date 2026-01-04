package com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura

@kotlinx.serialization.Serializable
data class EstimateGasDto(
    val id: Int?,
    val jsonrpc: String?,
    val result: String?
)