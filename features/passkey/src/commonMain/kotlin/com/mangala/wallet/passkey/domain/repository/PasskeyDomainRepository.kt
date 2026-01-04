package com.mangala.wallet.passkey.domain.repository

import com.mangala.wallet.passkey.domain.model.*

/**
 * Repository interface for passkey operations in domain layer
 */
interface PasskeyDomainRepository {
    /**
     * Register a new passkey for the user
     */
    suspend fun register(request: PasskeyRegistrationRequest): PasskeyRegistrationResult
    
    /**
     * Authenticate user with passkey
     */
    suspend fun authenticate(request: PasskeyAuthenticationRequest): PasskeyAuthenticationResult
    
    /**
     * Check if passkeys are supported on this device
     */
    suspend fun isSupported(): Boolean
    
    /**
     * Delete a specific passkey credential
     */
    suspend fun deleteCredential(credentialId: String): Boolean
    
    /**
     * Get current passkey configuration
     */
    fun getConfiguration(): PasskeyConfiguration
}