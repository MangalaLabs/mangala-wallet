package com.mangala.wallet.core.ai.data.remote.providers.mangala.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangalaRequest(
    @SerialName("sender")
    val sender: String,
    
    @SerialName("message")
    val message: String
)