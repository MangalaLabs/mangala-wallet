package com.mangala.wallet.auth

import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.core.auth.domain.model.AuthMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AuthStateTest {
    
    @Test
    fun `test AuthState types`() {
        // Test Initial state
        val initial = AuthState.Initial
        assertIs<AuthState.Initial>(initial)
        
        // Test Loading state
        val loading = AuthState.Loading
        assertIs<AuthState.Loading>(loading)
        
        // Test NotAuthenticated state
        val notAuthenticated = AuthState.NotAuthenticated
        assertIs<AuthState.NotAuthenticated>(notAuthenticated)
        
        // Test Authenticated states
        val withPasskey = AuthState.Authenticated.WithPasskey(
            userId = "user-123",
            credentialId = "cred-123"
        )
        assertIs<AuthState.Authenticated.WithPasskey>(withPasskey)
        assertEquals("user-123", withPasskey.userId)
        assertEquals("cred-123", withPasskey.credentialId)
        assertEquals(AuthMethod.PASSKEY, withPasskey.authMethod)
        
        val withBiometric = AuthState.Authenticated.WithBiometric(
            userId = "user-456"
        )
        assertIs<AuthState.Authenticated.WithBiometric>(withBiometric)
        assertEquals("user-456", withBiometric.userId)
        assertEquals(AuthMethod.BIOMETRIC, withBiometric.authMethod)
        
        val withPin = AuthState.Authenticated.WithPin(
            userId = "user-789"
        )
        assertIs<AuthState.Authenticated.WithPin>(withPin)
        assertEquals("user-789", withPin.userId)
        assertEquals(AuthMethod.PIN, withPin.authMethod)
        
        // Test Error states
        val passkeyError = AuthState.Error.PasskeyError(
            message = "Passkey error",
            canRetry = true
        )
        assertIs<AuthState.Error.PasskeyError>(passkeyError)
        assertEquals("Passkey error", passkeyError.message)
        assertEquals(true, passkeyError.canRetry)
        
        val biometricError = AuthState.Error.BiometricError(
            message = "Biometric error",
            canRetry = false
        )
        assertIs<AuthState.Error.BiometricError>(biometricError)
        assertEquals("Biometric error", biometricError.message)
        assertEquals(false, biometricError.canRetry)
        
        val pinError = AuthState.Error.PinError(
            message = "Invalid PIN",
            attemptsRemaining = 2
        )
        assertIs<AuthState.Error.PinError>(pinError)
        assertEquals("Invalid PIN", pinError.message)
        assertEquals(2, pinError.attemptsRemaining)
        assertEquals(true, pinError.canRetry) // Default value
        
        val networkError = AuthState.Error.NetworkError(
            message = "Network unavailable"
        )
        assertIs<AuthState.Error.NetworkError>(networkError)
        assertEquals("Network unavailable", networkError.message)
        assertEquals(true, networkError.canRetry) // Default value
        
        val unknownError = AuthState.Error.UnknownError(
            message = "Something went wrong"
        )
        assertIs<AuthState.Error.UnknownError>(unknownError)
        assertEquals("Something went wrong", unknownError.message)
        assertEquals(false, unknownError.canRetry) // Default value
    }
    
    @Test
    fun `test AuthMethod enum values`() {
        assertEquals(3, AuthMethod.entries.size)
        assertEquals(AuthMethod.PASSKEY, AuthMethod.valueOf("PASSKEY"))
        assertEquals(AuthMethod.BIOMETRIC, AuthMethod.valueOf("BIOMETRIC"))
        assertEquals(AuthMethod.PIN, AuthMethod.valueOf("PIN"))
    }
}