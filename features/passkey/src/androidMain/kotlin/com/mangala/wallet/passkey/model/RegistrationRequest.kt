package com.mangala.wallet.passkey.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val challenge: String,
    val rp: RelyingParty,
    val user: User,
    val pubKeyCredParams: List<PublicKeyCredentialParameters>,
    val timeout: Long,
    val authenticatorSelection: AuthenticatorSelection,
    val attestation: String? = null
) {
    @Serializable
    data class RelyingParty(
        val id: String,
        val name: String
    )

    @Serializable
    data class User(
        val id: String,
        val name: String,
        val displayName: String
    )

    @Serializable
    data class PublicKeyCredentialParameters(
        val type: String,
        val alg: Long
    )

    @Serializable
    data class AuthenticatorSelection(
        val authenticatorAttachment: String,
        val requireResidentKey: Boolean? = null,
        val residentKey: String,
        val userVerification: String
    )
}