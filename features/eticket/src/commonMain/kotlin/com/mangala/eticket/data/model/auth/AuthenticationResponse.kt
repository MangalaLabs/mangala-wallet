package com.mangala.eticket.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("token_expired_seconds")
    val tokenExpiredSeconds: Long,
    @SerialName("refresh_expired_seconds")
    val refreshExpiredSeconds: Long,
    @SerialName("token_type")
    val tokenType: String
)
