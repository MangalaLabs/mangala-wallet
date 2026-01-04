package com.mangala.wallet.core.security.auth

import com.mangala.wallet.core.security.models.SecurityLevel

/**
 * Interface for managing authentication across the app
 */
interface AuthenticationManager {
    /**
     * Perform authentication for a given security level
     */
    suspend fun authenticate(level: SecurityLevel): Result<AuthenticationResult>
    
    /**
     * Check if user is currently authenticated for a security level
     */
    suspend fun isAuthenticated(level: SecurityLevel): Boolean
    
    /**
     * Clear authentication state
     */
    suspend fun clearAuthentication()
}

/**
 * Result of an authentication attempt
 */
sealed class AuthenticationResult {
    data object Success : AuthenticationResult()
    data class Failure(val reason: String) : AuthenticationResult()
    data object Cancelled : AuthenticationResult()
}

/**
 * Types of authentication methods
 */
enum class AuthenticationMethod {
    PIN,
    BIOMETRIC,
    TWO_FACTOR
}