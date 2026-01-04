package com.mangala.wallet.passkey.repository

import com.mangala.wallet.passkey.model.*

interface PasskeyRepository {
    suspend fun getRegistrationOptions(userId: String, username: String? = null): RegistrationOptions
    
    suspend fun verifyRegistration(
        credential: PasskeyCredential,
        userId: String
    ): RegistrationVerificationResult
    
    suspend fun getAuthenticationOptions(userId: String? = null, username: String? = null): AuthenticationOptions
    
    suspend fun verifyAuthentication(
        credential: PasskeyCredential,
        challenge: ByteArray
    ): AuthenticationVerificationResult
    
    suspend fun verifyAuthenticationRaw(
        rawCredentialJson: String
    ): AuthenticationVerificationResult
    
    suspend fun getStoredCredentials(userId: String): CredentialListResponse
    
    suspend fun deleteCredential(credentialId: String)
}

data class RegistrationVerificationResult(
    val verified: Boolean,
    val credentialId: String? = null,
    val message: String? = null,
    val token: String? = null,
    val refreshToken: String? = null,
    val userId: String? = null,
    val expiresIn: Long? = null
)

data class AuthenticationVerificationResult(
    val verified: Boolean,
    val userId: String? = null,
    val token: String? = null,
    val refreshToken: String? = null,
    val message: String? = null,
    val keycloakAccessToken: String? = null,
    val keycloakRefreshToken: String? = null
)

data class CredentialListResponse(
    val credentials: List<StoredCredentialInfo>
)

data class StoredCredentialInfo(
    val id: String,
    val userId: String,
    val createdAt: Long,
    val lastUsedAt: Long,
    val name: String? = null
)