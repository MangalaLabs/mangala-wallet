package com.mangala.wallet.auth.integration

import com.mangala.wallet.auth.AuthenticationFlowManager
import com.mangala.wallet.auth.SessionManager
import com.mangala.wallet.auth.model.*
import com.mangala.wallet.auth.repository.AuthRepositoryImpl
import com.mangala.wallet.auth.wrapper.BiometryManager
import com.mangala.wallet.auth.wrapper.PinManager
import com.mangala.wallet.core.auth.domain.model.AuthMethod
import com.mangala.wallet.core.auth.domain.model.AuthSession
import com.mangala.wallet.core.auth.domain.model.AuthToken
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.passkey.PasskeyManager
import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.passkey.model.*
import com.mangala.wallet.passkey.repository.PasskeyRepositoryImpl
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.*

/**
 * Integration tests for backend API
 * Run these tests with the backend server running on localhost:8089
 */
class BackendIntegrationTest {
    
    private lateinit var httpClient: HttpClient
    private lateinit var passkeyRepository: PasskeyRepositoryImpl
    private lateinit var authRepository: AuthRepositoryImpl
    private lateinit var sessionManager: SessionManager
    private lateinit var authFlowManager: AuthenticationFlowManager
    
    private val baseUrl = "http://localhost:8089/api/v1"
    private val testUserId = "test_user_${System.currentTimeMillis()}"
    private val testEmail = "$testUserId@example.com"
    
    @BeforeTest
    fun setup() {
        httpClient = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
        }
        
        passkeyRepository = PasskeyRepositoryImpl(baseUrl, httpClient)
        authRepository = AuthRepositoryImpl(baseUrl, httpClient)
        sessionManager = SessionManager(TestSecureStorage())
        
        authFlowManager = AuthenticationFlowManager(
            passkeyManager = MockPasskeyManager(),
            passkeyRepository = passkeyRepository,
            biometryManager = MockBiometryManager(),
            pinManager = MockPinManager(),
            authRepository = authRepository,
            sessionManager = sessionManager
        )
    }
    
    @AfterTest
    fun teardown() {
        httpClient.close()
    }
    
    @Test
    fun testRegistrationFlow() = runTest {
        // Skip if backend is not running
        if (!isBackendRunning()) {
            println("Backend not running, skipping integration test")
            return@runTest
        }
        
        // Step 1: Get registration options
        val registrationOptions = passkeyRepository.getRegistrationOptions(testUserId)
        
        assertNotNull(registrationOptions)
        assertNotNull(registrationOptions.challenge)
        assertEquals("localhost", registrationOptions.rp.id)
        assertEquals(testUserId, registrationOptions.user.name)
        
        // Step 2: Create mock credential
        val mockCredential = PasskeyCredential(
            id = "mock_cred_${System.currentTimeMillis()}",
            rawId = "mock_raw_id".encodeToByteArray(),
            type = "public-key",
            response = AuthenticatorAttestationResponse(
                clientDataJSON = createMockClientDataJSON(registrationOptions.challenge, "webauthn.create"),
                attestationObject = "mock_attestation_object".encodeToByteArray()
            )
        )
        
        // Step 3: Verify registration
        val verificationResult = passkeyRepository.verifyRegistration(mockCredential, testUserId)
        
        assertTrue(verificationResult.verified)
        assertNotNull(verificationResult.credentialId)
        assertEquals("Registration successful", verificationResult.message)
    }
    
    @Test
    fun testAuthenticationFlow() = runTest {
        // Skip if backend is not running
        if (!isBackendRunning()) {
            println("Backend not running, skipping integration test")
            return@runTest
        }
        
        // First register a user
        registerTestUser()
        
        // Step 1: Get authentication options
        val authOptions = passkeyRepository.getAuthenticationOptions(testUserId)
        
        assertNotNull(authOptions)
        assertNotNull(authOptions.challenge)
        assertEquals("localhost", authOptions.rpId)
        
        // Step 2: Create mock assertion
        val mockCredential = PasskeyCredential(
            id = "mock_cred_${System.currentTimeMillis()}",
            rawId = "mock_raw_id".encodeToByteArray(),
            type = "public-key",
            response = MockAuthenticatorAssertionResponse(
                clientDataJSON = createMockClientDataJSON(authOptions.challenge, "webauthn.get"),
                authenticatorData = "mock_authenticator_data".encodeToByteArray(),
                signature = "mock_signature".encodeToByteArray(),
                userHandle = testUserId.encodeToByteArray()
            )
        )
        
        // Step 3: Verify authentication
        val authResult = passkeyRepository.verifyAuthentication(mockCredential, authOptions.challenge)
        
        assertTrue(authResult.verified)
        assertNotNull(authResult.token)
        assertNotNull(authResult.refreshToken)
        assertEquals("Login successful", authResult.message)
    }
    
    @Test
    fun testSessionManagement() = runTest {
        // Skip if backend is not running
        if (!isBackendRunning()) {
            println("Backend not running, skipping integration test")
            return@runTest
        }
        
        // Authenticate and get session
        val authResult = authenticateTestUser()
        assertNotNull(authResult)
        
        // Create session
        val session = AuthSession(
            userId = testUserId,
            token = AuthToken(
                accessToken = authResult.token!!,
                refreshToken = authResult.refreshToken!!,
                expiresAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000) // 30 days
            ),
            authMethod = AuthMethod.PASSKEY
        )
        
        // Save session
        sessionManager.saveSession(session)
        
        // Verify session is saved
        val loadedSession = sessionManager.loadSession()
        assertNotNull(loadedSession)
        assertEquals(testUserId, loadedSession.userId)
        assertEquals(authResult.token, loadedSession.token.accessToken)
        
        // Test session validation
        assertTrue(sessionManager.isSessionValid())
        
        // Test JWT decoding
        val jwtInfo = authRepository.decodeJwt(authResult.token!!)
        assertNotNull(jwtInfo)
        assertTrue(jwtInfo.raw.contains("User ID:"))
    }
    
    @Test
    fun testRollingSessionExtension() = runTest {
        // Skip if backend is not running
        if (!isBackendRunning()) {
            println("Backend not running, skipping integration test")
            return@runTest
        }
        
        // Create and save a session
        val session = AuthSession(
            userId = testUserId,
            token = AuthToken(
                accessToken = "test_token",
                refreshToken = "test_refresh",
                expiresAt = System.currentTimeMillis() + (5L * 24 * 60 * 60 * 1000) // 5 days
            ),
            authMethod = AuthMethod.PASSKEY
        )
        
        sessionManager.saveSession(session)
        
        // Extend session on activity
        sessionManager.extendSessionOnActivity()
        
        // Verify session was extended
        val extendedSession = sessionManager.loadSession()
        assertNotNull(extendedSession)
        assertTrue(extendedSession.token.expiresAt > session.token.expiresAt)
    }
    
    // Helper functions
    
    private suspend fun isBackendRunning(): Boolean {
        return try {
            val response = httpClient.get("$baseUrl/actuator/health")
            response.status.value == 200
        } catch (e: Exception) {
            false
        }
    }
    
    private suspend fun registerTestUser() {
        try {
            val registrationOptions = passkeyRepository.getRegistrationOptions(testUserId)
            val mockCredential = PasskeyCredential(
                id = "mock_cred_reg",
                rawId = "mock_raw_id".encodeToByteArray(),
                type = "public-key",
                response = AuthenticatorAttestationResponse(
                    clientDataJSON = createMockClientDataJSON(registrationOptions.challenge, "webauthn.create"),
                    attestationObject = "mock_attestation_object".encodeToByteArray()
                )
            )
            passkeyRepository.verifyRegistration(mockCredential, testUserId)
        } catch (e: Exception) {
            // User might already exist
        }
    }
    
    private suspend fun authenticateTestUser(): AuthenticationVerificationResult? {
        return try {
            val authOptions = passkeyRepository.getAuthenticationOptions(testUserId)
            val mockCredential = PasskeyCredential(
                id = "mock_cred_auth",
                rawId = "mock_raw_id".encodeToByteArray(),
                type = "public-key",
                response = MockAuthenticatorAssertionResponse(
                    clientDataJSON = createMockClientDataJSON(authOptions.challenge, "webauthn.get"),
                    authenticatorData = "mock_authenticator_data".encodeToByteArray(),
                    signature = "mock_signature".encodeToByteArray(),
                    userHandle = testUserId.encodeToByteArray()
                )
            )
            passkeyRepository.verifyAuthentication(mockCredential, authOptions.challenge)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun createMockClientDataJSON(challenge: ByteArray, type: String): ByteArray {
        val json = """
            {
                "type": "$type",
                "challenge": "${challenge.decodeToString()}",
                "origin": "http://localhost:8089",
                "crossOrigin": false
            }
        """.trimIndent()
        return json.encodeToByteArray()
    }
}

// Mock implementations

private class MockPasskeyManager : PasskeyManager {
    override suspend fun isSupported(): Boolean = true
    
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential {
        throw PasskeyException.NotSupported("Mock implementation")
    }
    
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult {
        throw PasskeyException.NotSupported("Mock implementation")
    }
    
    override suspend fun deleteCredential(credentialId: String) {}
    override suspend fun getStoredCredentials(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): List<StoredCredential> = emptyList()
}

private class MockBiometryManager : BiometryManager {
    override suspend fun authenticate(title: String, subtitle: String): Boolean = true
    override fun isBiometricAvailable(): Boolean = true
}

private class MockPinManager : PinManager {
    override suspend fun verifyPin(pin: String): Boolean = true
    override fun getRemainingAttempts(): Int = 3
}

private class TestSecureStorage : SecureStorageWrapper {
    private val storage = mutableMapOf<String, String>()
    
    override suspend fun saveValue(key: String, value: String) {
        storage[key] = value
    }
    
    override suspend fun getValue(key: String): String? = storage[key]
    
    override suspend fun remove(key: String) {
        storage.remove(key)
    }
    
    override suspend fun clear() {
        storage.clear()
    }
}

private class MockAuthenticatorAssertionResponse(
    override val clientDataJSON: ByteArray,
    override val authenticatorData: ByteArray,
    override val signature: ByteArray,
    override val userHandle: ByteArray?
) : com.mangala.wallet.passkey.repository.AuthenticatorAssertionResponseWrapper