package com.mangala.wallet.features.chains.bitcoin.data.remote.electrum.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ElectrumResponse(
    val id: Int,
    val result: JsonElement? = null,
    val error: JsonElement? = null
)
