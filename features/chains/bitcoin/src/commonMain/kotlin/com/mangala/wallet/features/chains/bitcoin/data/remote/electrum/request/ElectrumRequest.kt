package com.mangala.wallet.features.chains.bitcoin.data.remote.electrum.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ElectrumRequest(
    val id: Int,
    val method: String,
    val params: List<JsonElement>
)