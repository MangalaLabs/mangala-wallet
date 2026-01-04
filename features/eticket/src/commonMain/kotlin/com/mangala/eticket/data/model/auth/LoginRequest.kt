package com.mangala.eticket.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("public_key")
    val publicKey: String,
    @SerialName("signed_public_key")
    val signedPublicKey: String,
    @SerialName("message_signed")
    val messageSigned: String,
    val signature: String
)