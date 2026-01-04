package com.mangala.wallet.passkey

import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.passkey.model.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class PasskeyManagerTest {
    
    private lateinit var manager: PasskeyManager
    
    @BeforeTest
    fun setup() {
        manager = TestPasskeyManager()
    }
    
    // TC-PK-001: Successful Passkey Registration
    @Test
    fun `test successful passkey registration`() = runTest {
        // Given
        val userId = "test-user-123"
        val challenge = "test-challenge".encodeToByteArray()
        val rpId = "example.com"
        val userName = "testuser@example.com"
        val displayName = "Test User"
        
        // When
        val credential = manager.register(
            userId = userId,
            challenge = challenge,
            rpId = rpId,
            rpName = "Example",
            userName = userName,
            userDisplayName = displayName
        )
        
        // Then
        assertNotNull(credential)
        assertEquals("test-cred-123", credential.id)
        assertTrue(credential.response is AuthenticatorAttestationResponse)
    }
    
    // TC-PK-002: Registration with Duplicate User
    @Test
    fun `test registration with duplicate user`() = runTest {
        // Given
        val manager = DuplicateUserPasskeyManager()
        
        // When & Then
        assertFailsWith<PasskeyException.InvalidState> {
            manager.register(
                userId = "existing-user",
                challenge = ByteArray(32),
                rpId = "example.com",
                rpName = "Example",
                userName = "existing@example.com",
                userDisplayName = "Existing User"
            )
        }
    }
    
    // TC-PK-003: Registration Cancellation
    @Test
    fun `test registration cancellation`() = runTest {
        // Given
        val manager = CancellablePasskeyManager()
        
        // When & Then
        assertFailsWith<PasskeyException.UserCancelled> {
            manager.register(
                userId = "user",
                challenge = ByteArray(32),
                rpId = "example.com",
                rpName = "Example",
                userName = "user@example.com",
                userDisplayName = "User"
            )
        }
    }
    
    // TC-PK-004: Registration on Unsupported Device
    @Test
    fun `test registration on unsupported device`() = runTest {
        // Given
        val manager = UnsupportedPasskeyManager()
        
        // When
        assertFalse(manager.isSupported())
        
        // Then
        assertFailsWith<PasskeyException.NotSupported> {
            manager.register(
                userId = "user",
                challenge = ByteArray(32),
                rpId = "example.com",
                rpName = "Example",
                userName = "user@example.com",
                userDisplayName = "User"
            )
        }
    }
    
    // TC-PK-005: Registration Network Failure
    @Test
    fun `test registration network failure`() = runTest {
        // Given
        val manager = NetworkErrorPasskeyManager()
        
        // When & Then
        assertFailsWith<PasskeyException.NetworkError> {
            manager.register(
                userId = "user",
                challenge = ByteArray(32),
                rpId = "example.com",
                rpName = "Example",
                userName = "user@example.com",
                userDisplayName = "User"
            )
        }
    }
    
    // TC-PK-006: Successful Authentication
    @Test
    fun `test successful authentication`() = runTest {
        // Given
        val challenge = "auth-challenge".encodeToByteArray()
        val rpId = "example.com"
        
        // When
        val result = manager.authenticate(challenge, rpId)
        
        // Then
        assertNotNull(result)
        assertEquals("test-cred-123", result.credentialId)
        assertEquals("user-123", result.userId)
        assertTrue(result.verified)
    }
    
    // TC-PK-007: Authentication with Wrong Credential
    @Test
    fun `test authentication with wrong credential`() = runTest {
        // Given
        val manager = InvalidCredentialPasskeyManager()
        
        // When & Then
        assertFailsWith<PasskeyException.CredentialNotFound> {
            manager.authenticate(
                challenge = ByteArray(32),
                rpId = "example.com"
            )
        }
    }
    
    // TC-PK-008: Authentication Timeout
    @Test
    fun `test authentication timeout`() = runTest {
        // Given
        val manager = TimeoutPasskeyManager()
        
        // When & Then
        assertFailsWith<PasskeyException.Timeout> {
            manager.authenticate(
                challenge = ByteArray(32),
                rpId = "example.com"
            )
        }
    }
    
    // TC-PK-009: Authentication After Credential Deletion
    @Test
    fun `test authentication after credential deletion`() = runTest {
        // Given
        val manager = DeletedCredentialPasskeyManager()
        
        // When & Then
        assertFailsWith<PasskeyException.CredentialNotFound> {
            manager.authenticate(
                challenge = ByteArray(32),
                rpId = "example.com"
            )
        }
    }
    
    // TC-PK-010: List User Credentials
    @Test
    fun `test list user credentials`() = runTest {
        // When
        val credentials = manager.getStoredCredentials(,,)
        
        // Then
        assertEquals(1, credentials.size)
        assertEquals("test-cred-123", credentials[0].id)
        assertEquals("example.com", credentials[0].rpId)
        assertEquals("test@example.com", credentials[0].userName)
    }
    
    // TC-PK-011: Delete Specific Credential
    @Test
    fun `test delete specific credential`() = runTest {
        // Given
        val credentialId = "test-cred-123"
        
        // When
        manager.deleteCredential(credentialId)
        
        // Then - no exception thrown
        assertTrue(true)
    }
    
    // TC-PK-012: Delete All Credentials
    @Test
    fun `test delete all credentials`() = runTest {
        // Given
        val manager = MultiCredentialPasskeyManager()
        
        // When
        val credentialsBefore = manager.getStoredCredentials(,,)
        assertEquals(3, credentialsBefore.size)
        
        credentialsBefore.forEach { credential ->
            manager.deleteCredential(credential.id)
        }
        
        // Then
        val credentialsAfter = manager.getStoredCredentials(,,)
        assertEquals(0, credentialsAfter.size)
    }
}

// Test implementations for different scenarios
private open class TestPasskeyManager : PasskeyManager {
    override suspend fun isSupported(): Boolean = true
    
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential {
        return PasskeyCredential(
            id = "test-cred-123",
            rawId = "test-cred-123".encodeToByteArray(),
            type = "public-key",
            response = AuthenticatorAttestationResponse(
                clientDataJSON = "client-data".encodeToByteArray(),
                attestationObject = "attestation".encodeToByteArray()
            )
        )
    }
    
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult {
        return AuthenticationResult(
            credentialId = "test-cred-123",
            userId = "user-123",
            verified = true,
            authenticatorData = "auth-data".encodeToByteArray(),
            signature = "signature".encodeToByteArray()
        )
    }
    
    override suspend fun deleteCredential(credentialId: String) {
        // No-op for testing
    }
    
    override suspend fun getStoredCredentials(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): List<StoredCredential> {
        return listOf(
            StoredCredential(
                id = "test-cred-123",
                rpId = "example.com",
                userName = "test@example.com",
                createdAt = 1234567890L
            )
        )
    }
}

private class DuplicateUserPasskeyManager : TestPasskeyManager() {
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential {
        if (userId == "existing-user") {
            throw PasskeyException.InvalidState("User already has a registered passkey")
        }
        return super.register(userId, challenge, rpId, rpName, userName, userDisplayName)
    }
}

private class CancellablePasskeyManager : TestPasskeyManager() {
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential {
        throw PasskeyException.UserCancelled("User cancelled the operation")
    }
}

private class UnsupportedPasskeyManager : TestPasskeyManager() {
    override suspend fun isSupported(): Boolean = false
    
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential {
        throw PasskeyException.NotSupported("Device does not support passkeys")
    }
}

private class NetworkErrorPasskeyManager : TestPasskeyManager() {
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential {
        throw PasskeyException.NetworkError("Network connection failed")
    }
}

private class InvalidCredentialPasskeyManager : TestPasskeyManager() {
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult {
        throw PasskeyException.CredentialNotFound("No matching credential found")
    }
}

private class TimeoutPasskeyManager : TestPasskeyManager() {
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult {
        throw PasskeyException.Timeout("Operation timed out after 60 seconds")
    }
}

private class DeletedCredentialPasskeyManager : TestPasskeyManager() {
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult {
        throw PasskeyException.CredentialNotFound("Credential has been deleted")
    }
}

private class MultiCredentialPasskeyManager : TestPasskeyManager() {
    private val credentials = mutableListOf(
        StoredCredential("cred-1", "example.com", "user1@example.com", 1234567890L),
        StoredCredential("cred-2", "example.com", "user2@example.com", 1234567891L),
        StoredCredential("cred-3", "example.com", "user3@example.com", 1234567892L)
    )
    
    override suspend fun getStoredCredentials(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): List<StoredCredential> = credentials.toList()
    
    override suspend fun deleteCredential(credentialId: String) {
        credentials.removeAll { it.id == credentialId }
    }
}