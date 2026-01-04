package com.mangala.wallet.passkey.repository

import com.mangala.wallet.passkey.model.*
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlin.random.Random

/**
 * Mock implementation of PasskeyRepository for testing when server is unavailable
 */
class MockPasskeyRepository : PasskeyRepository {
    
    private val mockChallenge = "mock_challenge_${Random.nextBytes(16).joinToString("") { 
        val byte = it.toInt() and 0xFF
        byte.toString(16).padStart(2, '0')
    }}"
    private val mockCredentialId = "mock_credential_${Random.nextInt(10000)}"
    private val mockCredentials = mutableListOf<StoredCredentialInfo>()
    
    override suspend fun getRegistrationOptions(userId: String, username: String?): RegistrationOptions {
        // Simulate network delay
        delay(500)
        
        val displayUsername = username ?: userId.substringBefore('@')
        
        return RegistrationOptions(
            challenge = mockChallenge.encodeToByteArray(),
            rp = RelyingParty(
                id = "localhost",
                name = "Mock Mangala Wallet"
            ),
            user = User(
                id = userId.encodeToByteArray(),
                name = displayUsername,
                displayName = displayUsername
            ),
            pubKeyCredParams = listOf(
                PublicKeyCredentialParameters(
                    type = "public-key",
                    alg = -7 // ES256
                )
            ),
            attestation = AttestationConveyancePreference.NONE,
            timeout = 60000
        )
    }
    
    override suspend fun verifyRegistration(
        credential: PasskeyCredential,
        userId: String
    ): RegistrationVerificationResult {
        // Simulate network delay
        delay(500)
        
        // Add to mock storage
        mockCredentials.add(
            StoredCredentialInfo(
                id = credential.id,
                userId = userId,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                lastUsedAt = Clock.System.now().toEpochMilliseconds(),
                name = "Mock Passkey"
            )
        )
        
        return RegistrationVerificationResult(
            verified = true,
            credentialId = credential.id,
            message = "Mock registration successful",
            // Mock auto-login tokens
            token = "mock_access_token_${Clock.System.now().toEpochMilliseconds()}",
            refreshToken = "mock_refresh_token_${Clock.System.now().toEpochMilliseconds()}",
            userId = userId
        )
    }
    
    override suspend fun getAuthenticationOptions(userId: String?, username: String?): AuthenticationOptions {
        // Simulate network delay
        delay(500)
        
        return AuthenticationOptions(
            challenge = mockChallenge.encodeToByteArray(),
            rpId = "localhost",
            userVerification = UserVerificationRequirement.PREFERRED,
            timeout = 60000,
            allowCredentials = if (userId != null && mockCredentials.isNotEmpty()) {
                mockCredentials.filter { it.userId == userId }.map {
                    PublicKeyCredentialDescriptor(
                        type = "public-key",
                        id = it.id.encodeToByteArray(),
                        transports = listOf(AuthenticatorTransport.INTERNAL)
                    )
                }
            } else {
                emptyList()
            }
        )
    }
    
    override suspend fun verifyAuthentication(
        credential: PasskeyCredential,
        challenge: ByteArray
    ): AuthenticationVerificationResult {
        // Simulate network delay
        delay(500)
        
        // Find the credential
        val storedCredential = mockCredentials.find { it.id == credential.id }
        
        return if (storedCredential != null) {
            // Update last used time
            val updatedCredentials = mockCredentials.map { 
                if (it.id == credential.id) it.copy(lastUsedAt = Clock.System.now().toEpochMilliseconds()) else it
            }
            mockCredentials.clear()
            mockCredentials.addAll(updatedCredentials)
            
            AuthenticationVerificationResult(
                verified = true,
                token = "mock_jwt_token_${Clock.System.now().toEpochMilliseconds()}",
                refreshToken = "mock_refresh_token_${Clock.System.now().toEpochMilliseconds()}",
                userId = storedCredential.userId,
                message = "Mock authentication successful"
            )
        } else {
            AuthenticationVerificationResult(
                verified = false,
                message = "Credential not found"
            )
        }
    }

    override suspend fun verifyAuthenticationRaw(rawCredentialJson: String): AuthenticationVerificationResult {
        TODO("Not yet implemented")
    }

    override suspend fun getStoredCredentials(userId: String): CredentialListResponse {
        // Simulate network delay
        delay(300)
        
        return CredentialListResponse(
            credentials = mockCredentials.filter { it.userId == userId }
        )
    }
    
    override suspend fun deleteCredential(credentialId: String) {
        // Simulate network delay
        delay(300)
        
        mockCredentials.removeAll { it.id == credentialId }
    }
}