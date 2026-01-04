package com.mangala.wallet.model.provider.alchemy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlchemyRequest(
    @SerialName("id") val id: Int,
    @SerialName("jsonrpc") val jsonrpc: String = "2.0",
    @SerialName("method") val method: String,
    @SerialName("params") val params: List<String>
)