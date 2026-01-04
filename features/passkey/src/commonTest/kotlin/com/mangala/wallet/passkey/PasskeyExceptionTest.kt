package com.mangala.wallet.passkey

import com.mangala.wallet.passkey.exception.PasskeyException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PasskeyExceptionTest {
    
    @Test
    fun `test PasskeyException types`() {
        // Test each exception type
        val notSupported = PasskeyException.NotSupported("Not supported")
        assertIs<PasskeyException.NotSupported>(notSupported)
        assertEquals("Not supported", notSupported.message)
        
        val userCancelled = PasskeyException.UserCancelled("User cancelled")
        assertIs<PasskeyException.UserCancelled>(userCancelled)
        assertEquals("User cancelled", userCancelled.message)
        
        val timeout = PasskeyException.Timeout("Operation timed out")
        assertIs<PasskeyException.Timeout>(timeout)
        assertEquals("Operation timed out", timeout.message)
        
        val invalidChallenge = PasskeyException.InvalidChallenge("Invalid challenge")
        assertIs<PasskeyException.InvalidChallenge>(invalidChallenge)
        assertEquals("Invalid challenge", invalidChallenge.message)
        
        val credentialNotFound = PasskeyException.CredentialNotFound("Credential not found")
        assertIs<PasskeyException.CredentialNotFound>(credentialNotFound)
        assertEquals("Credential not found", credentialNotFound.message)
        
        val networkError = PasskeyException.NetworkError("Network error", null)
        assertIs<PasskeyException.NetworkError>(networkError)
        assertEquals("Network error", networkError.message)
        
        val serverError = PasskeyException.ServerError("Server error", 500)
        assertIs<PasskeyException.ServerError>(serverError)
        assertEquals("Server error", serverError.message)
        assertEquals(500, serverError.statusCode)
        
        val invalidState = PasskeyException.InvalidState("Invalid state")
        assertIs<PasskeyException.InvalidState>(invalidState)
        assertEquals("Invalid state", invalidState.message)
        
        val unknownError = PasskeyException.UnknownError("Unknown error", null)
        assertIs<PasskeyException.UnknownError>(unknownError)
        assertEquals("Unknown error", unknownError.message)
    }
    
    @Test
    fun `test default exception messages`() {
        // Test default messages
        val notSupported = PasskeyException.NotSupported()
        assertEquals("Passkey authentication is not supported on this device", notSupported.message)
        
        val userCancelled = PasskeyException.UserCancelled()
        assertEquals("User cancelled passkey operation", userCancelled.message)
        
        val timeout = PasskeyException.Timeout()
        assertEquals("Passkey operation timed out", timeout.message)
        
        val invalidChallenge = PasskeyException.InvalidChallenge()
        assertEquals("Invalid or expired challenge", invalidChallenge.message)
        
        val credentialNotFound = PasskeyException.CredentialNotFound()
        assertEquals("No matching credential found", credentialNotFound.message)
        
        val networkError = PasskeyException.NetworkError()
        assertEquals("Network error occurred", networkError.message)
        
        val serverError = PasskeyException.ServerError()
        assertEquals("Server error occurred", serverError.message)
        
        val invalidState = PasskeyException.InvalidState()
        assertEquals("Invalid state for passkey operation", invalidState.message)
        
        val unknownError = PasskeyException.UnknownError()
        assertEquals("Unknown error occurred", unknownError.message)
    }
}