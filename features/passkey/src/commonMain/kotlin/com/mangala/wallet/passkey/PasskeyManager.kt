package com.mangala.wallet.passkey

import com.mangala.wallet.passkey.model.*

interface PasskeyManager {
    
    /**
     * Get the raw authentication JSON response if available (platform-specific)
     * Returns null if not supported or not available
     */
    fun getLastAuthenticationRawJson(): String? = null
    suspend fun isSupported(): Boolean
    
    suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String = "Mangala Wallet",
        userName: String,
        userDisplayName: String
    ): PasskeyCredential
    
    suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor> = emptyList()
    ): AuthenticationResult
    
    suspend fun deleteCredential(credentialId: String)
    
    fun getLastAuthenticationCredential(): PasskeyCredential?
}