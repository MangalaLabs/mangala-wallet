package com.mangala.wallet.features.chains.bitcoin.data.remote.electrum.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class ElectrumNotification(
    val method: String,
    val params: JsonArray
)