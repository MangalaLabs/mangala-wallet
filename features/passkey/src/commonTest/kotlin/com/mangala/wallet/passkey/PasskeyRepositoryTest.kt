package com.mangala.wallet.passkey

import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.passkey.model.*
import com.mangala.wallet.passkey.repository.PasskeyRepository
import com.mangala.wallet.passkey.repository.RegistrationVerificationResult
import com.mangala.wallet.passkey.repository.AuthenticationVerificationResult
import com.mangala.wallet.passkey.repository.CredentialListResponse
import com.mangala.wallet.passkey.repository.StoredCredentialInfo
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class PasskeyRepositoryTest {
    
    private lateinit var repository: PasskeyRepository
    
    @BeforeTest
    fun setup() {
        repository = TestPasskeyRepository()
    }
    
    @Test
    fun `test get registration options`() = runTest {
        // When
        val options = repository.getRegistrationOptions("user-123")
        
        // Then
        assertNotNull(options)
        assertEquals("example.com", options.rp.id)
        assertEquals("Example", options.rp.name)
        assertTrue(options.challenge.isNotEmpty())
    }
    
    @Test
    fun `test verify registration`() = runTest {
        // Given
        val credential = PasskeyCredential(
            id = "cred-123",
            rawId = "cred-123".encodeToByteArray(),
            type = "public-key",
            response = AuthenticatorAttestationResponse(
                clientDataJSON = "client-data".encodeToByteArray(),
                attestationObject = "attestation".encodeToByteArray()
            )
        )
        
        // When
        val result = repository.verifyRegistration(credential, "user-123")
        
        // Then
        assertTrue(result.verified)
        assertEquals("cred-123", result.credentialId)
    }
    
    @Test
    fun `test get authentication options`() = runTest {
        // When
        val options = repository.getAuthenticationOptions("user-123")
        
        // Then
        assertNotNull(options)
        assertEquals("example.com", options.rpId)
        assertTrue(options.challenge.isNotEmpty())
    }
    
    @Test
    fun `test verify authentication`() = runTest {
        // Given
        val credential = PasskeyCredential(
            id = "cred-123",
            rawId = "cred-123".encodeToByteArray(),
            type = "public-key",
            response = AuthenticatorAssertionResponse(
                clientDataJSON = "client-data".encodeToByteArray(),
                authenticatorData = "auth-data".encodeToByteArray(),
                signature = "signature".encodeToByteArray(),
                userHandle = "user-123".encodeToByteArray()
            )
        )
        
        // When
        val result = repository.verifyAuthentication(
            credential,
            "challenge".encodeToByteArray()
        )
        
        // Then
        assertTrue(result.verified)
        assertEquals("user-123", result.userId)
        assertNotNull(result.token)
        assertNotNull(result.refreshToken)
    }
    
    @Test
    fun `test get stored credentials`() = runTest {
        // When
        val result = repository.getStoredCredentials("user-123")
        
        // Then
        assertEquals(2, result.credentials.size)
        assertTrue(result.credentials.all { it.userId == "user-123" })
    }
    
    @Test
    fun `test delete credential`() = runTest {
        // When & Then - no exception thrown
        repository.deleteCredential("cred-123")
        assertTrue(true)
    }
    
    @Test
    fun `test server error handling`() = runTest {
        // Given
        val errorRepository = ServerErrorPasskeyRepository()
        
        // When & Then
        assertFailsWith<PasskeyException.ServerError> {
            errorRepository.getRegistrationOptions("user-123")
        }
    }
    
    @Test
    fun `test network error handling`() = runTest {
        // Given
        val errorRepository = NetworkErrorPasskeyRepository()
        
        // When & Then
        assertFailsWith<PasskeyException.NetworkError> {
            errorRepository.getAuthenticationOptions("user-123")
        }
    }
}

// Test implementations
private open class TestPasskeyRepository : PasskeyRepository {
    override suspend fun getRegistrationOptions(userId: String): RegistrationOptions {
        return RegistrationOptions(
            challenge = "test-challenge".encodeToByteArray(),
            rp = RelyingParty("example.com", "Example"),
            user = User(userId.encodeToByteArray(), userId, "Test User"),
            pubKeyCredParams = listOf(
                PublicKeyCredentialParameters(alg = -7),
                PublicKeyCredentialParameters(alg = -257)
            )
        )
    }
    
    override suspend fun verifyRegistration(
        credential: PasskeyCredential,
        userId: String
    ): RegistrationVerificationResult {
        return RegistrationVerificationResult(
            verified = true,
            credentialId = credential.id
        )
    }
    
    override suspend fun getAuthenticationOptions(userId: String?): AuthenticationOptions {
        return AuthenticationOptions(
            challenge = "auth-challenge".encodeToByteArray(),
            rpId = "example.com",
            allowCredentials = if (userId != null) {
                listOf(
                    PublicKeyCredentialDescriptor(
                        id = "cred-123".encodeToByteArray()
                    )
                )
            } else emptyList()
        )
    }
    
    override suspend fun verifyAuthentication(
        credential: PasskeyCredential,
        challenge: ByteArray
    ): AuthenticationVerificationResult {
        return AuthenticationVerificationResult(
            verified = true,
            userId = "user-123",
            token = "access-token-123",
            refreshToken = "refresh-token-123"
        )
    }
    
    override suspend fun getStoredCredentials(userId: String): CredentialListResponse {
        return CredentialListResponse(
            credentials = listOf(
                StoredCredentialInfo(
                    id = "cred-1",
                    userId = userId,
                    createdAt = 1234567890L,
                    lastUsedAt = 1234567890L,
                    name = "Chrome on Windows"
                ),
                StoredCredentialInfo(
                    id = "cred-2",
                    userId = userId,
                    createdAt = 1234567891L,
                    lastUsedAt = 1234567891L,
                    name = "Safari on iPhone"
                )
            )
        )
    }
    
    override suspend fun deleteCredential(credentialId: String) {
        // No-op for testing
    }
}

private class ServerErrorPasskeyRepository : TestPasskeyRepository() {
    override suspend fun getRegistrationOptions(userId: String): RegistrationOptions {
        throw PasskeyException.ServerError("Internal server error", 500)
    }
}

private class NetworkErrorPasskeyRepository : TestPasskeyRepository() {
    override suspend fun getAuthenticationOptions(userId: String?): AuthenticationOptions {
        throw PasskeyException.NetworkError("Network timeout")
    }
}