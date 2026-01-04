package com.mangala.wallet.passkey.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationRequest(
    val challenge: String,
    val rpId: String,
    val timeout: Long,
    val userVerification: String,
    val allowCredentials: List<AllowCredential> = emptyList()
) {
    @Serializable
    data class AllowCredential(
        val type: String,
        val id: String
    )
}