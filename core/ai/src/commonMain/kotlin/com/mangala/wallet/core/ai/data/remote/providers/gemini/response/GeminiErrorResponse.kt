package com.mangala.wallet.core.ai.data.remote.providers.gemini.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiErrorResponse(
    @SerialName("error")
    val error: Error? = null
) {
    @Serializable
    data class Error(
        @SerialName("code")
        val code: Int? = null,
        @SerialName("message")
        val message: String? = null,
        @SerialName("status")
        val status: String? = null
    )
}