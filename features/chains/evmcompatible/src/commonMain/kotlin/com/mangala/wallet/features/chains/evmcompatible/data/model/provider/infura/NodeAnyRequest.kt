package com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class NodeAnyRequest(
    val jsonrpc: String?,
    val method: String?,
    @Contextual val params: List< @Contextual Any?>?,
    val id: Int?
)