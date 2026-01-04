package com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura

import kotlinx.serialization.Serializable

@Serializable
data class NodeRequest(
    val jsonrpc: String?,
    val method: String?,
    @Serializable(ParamListSerializer::class)
    val params: List<Param?>?,
    val id: Int?
)