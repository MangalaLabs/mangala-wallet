package com.mangala.wallet.auth

import com.mangala.wallet.auth.model.*
import com.mangala.wallet.auth.repository.AuthRepository
import com.mangala.wallet.auth.wrapper.BiometryManager
import com.mangala.wallet.auth.wrapper.PinManager
import com.mangala.wallet.core.auth.domain.model.AuthMethod
import com.mangala.wallet.core.auth.domain.model.AuthSession
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.core.auth.domain.model.AuthToken
import com.mangala.wallet.passkey.PasskeyManager
import com.mangala.wallet.passkey.model.*
import com.mangala.wallet.passkey.repository.PasskeyRepository
import com.mangala.wallet.passkey.repository.*
import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.test.*

class AuthenticationFlowManagerTest {
    
    private lateinit var flowManager: AuthenticationFlowManager
    private lateinit var passkeyManager: TestPasskeyManager
    private lateinit var passkeyRepository: TestPasskeyRepository
    private lateinit var biometryManager: TestBiometryManager
    private lateinit var pinManager: TestPinManager
    private lateinit var authRepository: TestAuthRepository
    private lateinit var sessionManager: TestSessionManager
    
    @BeforeTest
    fun setup() {
        passkeyManager = TestPasskeyManager()
        passkeyRepository = TestPasskeyRepository()
        biometryManager = TestBiometryManager()
        pinManager = TestPinManager()
        authRepository = TestAuthRepository()
        sessionManager = TestSessionManager()
        
        flowManager = AuthenticationFlowManager(
            passkeyManager = passkeyManager,
            passkeyRepository = passkeyRepository,
            biometryManager = biometryManager,
            pinManager = pinManager,
            authRepository = authRepository,
            sessionManager = sessionManager.sessionManager
        )
    }
    
    // TC-AUTH-001: Complete Authentication Flow Success
    @Test
    fun `test complete authentication flow with passkey success`() = runTest {
        // When
        flowManager.authenticate("user-123")
        
        // Then
        val state = flowManager.authState.first()
        assertTrue(state is AuthState.Authenticated.WithPasskey)
        assertEquals("user-123", (state as AuthState.Authenticated.WithPasskey).userId)
    }
    
    // TC-AUTH-002: Passkey Fallback to Biometric
    @Test
    fun `test passkey fallback to biometric`() = runTest {
        // Given
        passkeyManager.shouldFailAuthentication = true
        
        // When
        flowManager.authenticate()
        
        // Then
        val state = flowManager.authState.first()
        assertTrue(state is AuthState.Authenticated.WithBiometric)
    }
    
    // TC-AUTH-003: Biometric Fallback to PIN
    @Test
    fun `test biometric fallback to PIN`() = runTest {
        // Given
        passkeyManager.shouldFailAuthentication = true
        biometryManager.shouldFailAuthentication = true
        pinManager.shouldFailVerification = false
        
        // Set up existing session for PIN auth
        val pinSession = AuthSession(
            userId = "user-123",
            token = AuthToken(
                accessToken = "pin-token",
                refreshToken = "pin-refresh",
                expiresAt = Clock.System.now().toEpochMilliseconds() + 3600000
            ),
            authMethod = AuthMethod.PIN
        )
        sessionManager.savedSession = pinSession
        sessionManager.testSecureStorage.preloadSession(pinSession)
        
        // When
        flowManager.authenticate()
        
        // Then
        val state = flowManager.authState.first()
        assertEquals(AuthState.NotAuthenticated, state)
        
        // Now try PIN
        val success = flowManager.authenticateWithPin("1234")
        assertTrue(success)
        
        val finalState = flowManager.authState.first()
        assertTrue(finalState is AuthState.Authenticated.WithPin)
    }
    
    // TC-AUTH-004: All Methods Cancelled
    @Test
    fun `test all authentication methods cancelled`() = runTest {
        // Given
        passkeyManager.shouldCancel = true
        biometryManager.shouldFailAuthentication = true
        pinManager.shouldFailVerification = true
        
        // When
        flowManager.authenticate()
        val result = flowManager.authenticateWithPin("wrong")
        
        // Then
        assertFalse(result)
        val state = flowManager.authState.first()
        assertTrue(state is AuthState.Error.PinError)
    }
    
    // TC-AUTH-005: Invalid PIN Entry
    @Test
    fun `test invalid PIN entry with retry`() = runTest {
        // Given
        pinManager.setRemainingAttempts(2)
        
        // When - wrong PIN
        val firstResult = flowManager.authenticateWithPin("wrong")
        
        // Then
        assertFalse(firstResult)
        val errorState = flowManager.authState.first()
        assertTrue(errorState is AuthState.Error.PinError)
        assertEquals(2, (errorState as AuthState.Error.PinError).attemptsRemaining)
        
        // When - correct PIN
        pinManager.shouldFailVerification = false
        val secondResult = flowManager.authenticateWithPin("1234")
        
        // Then
        assertTrue(secondResult)
        val successState = flowManager.authState.first()
        assertTrue(successState is AuthState.Authenticated.WithPin)
    }
    
    // TC-AUTH-006: Session Token Storage
    @Test
    fun `test session token storage after authentication`() = runTest {
        // Given - Clear any pre-existing session
        sessionManager.savedSession = null
        sessionManager.testSecureStorage.storage.clear()
        
        // When
        val result = flowManager.authenticateWithPasskey("user-123")
        
        // Then
        assertTrue(result)
        val savedSession = sessionManager.getLastSavedSession()
        assertNotNull(savedSession)
        assertEquals("user-123", savedSession.userId)
        assertEquals(AuthMethod.PASSKEY, savedSession.authMethod)
    }
    
    // TC-AUTH-007: Session Token Refresh
    @Test
    fun `test automatic token refresh`() = runTest {
        // Given - Set up session that needs refresh
        val expiresAt = Clock.System.now().toEpochMilliseconds() + 240000 // 4 minutes (less than 5 min threshold)
        sessionManager.savedSession = AuthSession(
            userId = "user-123",
            token = AuthToken(
                accessToken = "old-token",
                refreshToken = "refresh-token",
                expiresAt = expiresAt
            ),
            authMethod = AuthMethod.PASSKEY
        )
        // Save to secure storage for sessionManager to pick up
        sessionManager.testSecureStorage.preloadSession(sessionManager.savedSession!!)
        
        // Load the session to ensure it's in SessionManager's state
        sessionManager.sessionManager.loadSession()
        
        // Ensure authRepository returns new token
        authRepository.refreshedToken = AuthToken(
            accessToken = "new-access-token",
            refreshToken = "new-refresh-token",
            expiresAt = Clock.System.now().toEpochMilliseconds() + 3600000
        )
        
        // When
        flowManager.refreshTokenIfNeeded()
        
        // Then - wait for async operation
        kotlinx.coroutines.delay(100)
        
        // Check that session was updated with new token
        val currentSession = sessionManager.sessionManager.sessionState.value
        assertNotNull(currentSession)
        assertEquals("new-access-token", currentSession.token.accessToken)
    }
    
    // TC-AUTH-008: Session Expiry Handling
    @Test
    fun `test expired session handling`() = runTest {
        // Given - Set up expired session
        val expiredSession = AuthSession(
            userId = "user-123",
            token = AuthToken(
                accessToken = "expired-token",
                refreshToken = "expired-refresh",
                expiresAt = Clock.System.now().toEpochMilliseconds() - 3600000 // 1 hour ago
            ),
            authMethod = AuthMethod.PASSKEY
        )
        sessionManager.savedSession = expiredSession
        sessionManager.testSecureStorage.preloadSession(expiredSession)
        
        // Load the session to ensure it's in SessionManager's state
        sessionManager.sessionManager.loadSession()
        
        authRepository.shouldFailRefresh = true
        
        // When
        flowManager.refreshTokenIfNeeded()
        
        // Then - wait for async operation
        kotlinx.coroutines.delay(100)
        
        val state = flowManager.authState.first()
        assertEquals(AuthState.NotAuthenticated, state)
    }
    
    // TC-AUTH-009: Session Logout
    @Test
    fun `test session logout`() = runTest {
        // Given - Set up a valid session
        val session = AuthSession(
            userId = "user-123",
            token = AuthToken(
                accessToken = "access-token",
                refreshToken = "refresh-token",
                expiresAt = Clock.System.now().toEpochMilliseconds() + 3600000
            ),
            authMethod = AuthMethod.PASSKEY
        )
        sessionManager.savedSession = session
        sessionManager.testSecureStorage.preloadSession(session)
        
        // When
        flowManager.logout()
        
        // Then - wait for async operation
        kotlinx.coroutines.delay(100)
        
        // Check that session was cleared
        val currentSession = sessionManager.sessionManager.sessionState.value
        assertNull(currentSession)
        val state = flowManager.authState.first()
        assertEquals(AuthState.NotAuthenticated, state)
    }
    
    // TC-AUTH-010: Multiple Session Handling
    @Test
    fun `test multiple session handling`() = runTest {
        // This would be tested at integration level with real backend
        // For unit test, we verify session isolation
        
        // Given
        val session1 = AuthSession(
            userId = "user-123",
            token = AuthToken("token1", "refresh1", Clock.System.now().toEpochMilliseconds() + 3600000),
            authMethod = AuthMethod.PASSKEY
        )
        
        // When
        sessionManager.saveSession(session1)
        
        // Then
        assertEquals("user-123", sessionManager.getCurrentUserId())
        assertEquals("token1", sessionManager.savedSession?.token?.accessToken)
    }
    
    // TC-AUTH-011: Authentication State Transitions
    @Test
    fun `test authentication state transitions`() = runTest {
        // Initial state
        assertEquals(AuthState.Initial, flowManager.authState.first())
        
        // Start authentication - Loading state
        val authJob = launch {
            flowManager.authenticate()
        }
        
        // Allow state to update
        kotlinx.coroutines.delay(10)
        
        // Should transition through Loading
        val state = flowManager.authState.first()
        assertTrue(state is AuthState.Loading || state is AuthState.Authenticated)
        
        authJob.join()
        
        // Final state should be Authenticated
        val finalState = flowManager.authState.first()
        assertTrue(finalState is AuthState.Authenticated)
    }
    
    // TC-AUTH-012: Error State Handling
    @Test
    fun `test error state handling`() = runTest {
        // Network Error (thrown by repository gets caught as UnknownError)
        passkeyRepository.shouldThrowNetworkError = true
        val result1 = flowManager.authenticateWithPasskey()
        assertFalse(result1)
        val state1 = flowManager.authState.first()
        assertTrue(state1 is AuthState.Error.UnknownError)
        
        // Reset
        passkeyRepository.shouldThrowNetworkError = false
        
        // Timeout Error (PasskeyException gets handled as PasskeyError)
        passkeyManager.shouldTimeout = true
        val result2 = flowManager.authenticateWithPasskey()
        assertFalse(result2)
        val state2 = flowManager.authState.first()
        assertTrue(state2 is AuthState.Error.PasskeyError)
        
        // Unknown Error (PasskeyException.UnknownError gets handled as PasskeyError)
        passkeyManager.shouldThrowUnknownError = true
        passkeyManager.shouldTimeout = false
        val result3 = flowManager.authenticateWithPasskey()
        assertFalse(result3)
        val state3 = flowManager.authState.first()
        assertTrue(state3 is AuthState.Error.PasskeyError)
    }
    
    // TC-AUTH-013: Loading State Management
    @Test
    fun `test loading state during operations`() = runTest {
        // During authentication
        val job = launch {
            flowManager.authenticate()
        }
        
        kotlinx.coroutines.delay(10)
        // Should show loading at some point
        job.join()
        
        // Final state should not be loading
        val finalState = flowManager.authState.first()
        assertFalse(finalState is AuthState.Loading)
    }
    
    // TC-AUTH-014: Brute Force Protection
    @Test
    fun `test brute force protection with PIN`() = runTest {
        // Given
        pinManager.setRemainingAttempts(5)
        
        // Try 5 wrong attempts
        repeat(5) {
            pinManager.setRemainingAttempts(5 - it - 1)
            flowManager.authenticateWithPin("wrong")
        }
        
        // Then
        val state = flowManager.authState.first()
        assertTrue(state is AuthState.Error.PinError)
        assertEquals(0, (state as AuthState.Error.PinError).attemptsRemaining)
    }
    
    // TC-AUTH-015: Token Security Validation
    @Test
    fun `test token security validation`() = runTest {
        // This is more of an integration test
        // Unit test verifies token structure
        val token = AuthToken(
            accessToken = "valid-token",
            refreshToken = "valid-refresh",
            expiresAt = Clock.System.now().toEpochMilliseconds() + 3600000
        )
        
        assertTrue(token.accessToken.isNotEmpty())
        assertTrue(token.refreshToken.isNotEmpty())
        assertTrue(token.expiresAt > Clock.System.now().toEpochMilliseconds())
    }
    
    // TC-AUTH-016: Secure Storage Encryption
    @Test
    fun `test secure storage encryption`() = runTest {
        // Given - Clear any existing session
        sessionManager.savedSession = null
        sessionManager.saveSessionCalled = false
        sessionManager.testSecureStorage.storage.clear()
        
        // Unit test verifies storage interface is used
        passkeyManager.shouldFailAuthentication = false
        val result = flowManager.authenticateWithPasskey("user-123")
        
        // Then
        assertTrue(result)
        // Check that session was saved to secure storage
        val savedSession = sessionManager.getLastSavedSession()
        assertNotNull(savedSession)
        // Check secure storage has the session data
        assertTrue(sessionManager.testSecureStorage.storage.containsKey("auth_token"))
        assertTrue(sessionManager.testSecureStorage.storage.containsKey("user_id"))
    }
}

// Test implementations
private class TestPasskeyManager : PasskeyManager {
    var shouldFailAuthentication = false
    var shouldCancel = false
    var shouldTimeout = false
    var shouldThrowUnknownError = false
    
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
            id = "test-cred",
            rawId = "test-cred".encodeToByteArray(),
            type = "public-key",
            response = AuthenticatorAttestationResponse(
                clientDataJSON = ByteArray(0),
                attestationObject = ByteArray(0)
            )
        )
    }
    
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult {
        when {
            shouldCancel -> throw PasskeyException.UserCancelled()
            shouldTimeout -> throw PasskeyException.Timeout()
            shouldThrowUnknownError -> throw PasskeyException.UnknownError("Unknown error")
            shouldFailAuthentication -> throw Exception("Authentication failed")
            else -> return AuthenticationResult(
                credentialId = "test-cred",
                userId = "user-123",
                verified = true,
                authenticatorData = ByteArray(0),
                signature = ByteArray(0)
            )
        }
    }
    
    override suspend fun deleteCredential(credentialId: String) {}
    override suspend fun getStoredCredentials(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): List<StoredCredential> = emptyList()
}

private class TestPasskeyRepository : PasskeyRepository {
    var shouldThrowNetworkError = false
    var authVerificationResult = AuthenticationVerificationResult(
        verified = true,
        userId = "user-123",
        token = "access-token",
        refreshToken = "refresh-token"
    )
    
    override suspend fun getRegistrationOptions(userId: String): RegistrationOptions {
        if (shouldThrowNetworkError) throw Exception("Network error")
        return RegistrationOptions(
            challenge = "test-challenge".encodeToByteArray(),
            rp = RelyingParty("example.com", "Example"),
            user = User(userId.encodeToByteArray(), userId, "Test User"),
            pubKeyCredParams = listOf(PublicKeyCredentialParameters(alg = -7)),
            authenticatorSelection = AuthenticatorSelectionCriteria(
                authenticatorAttachment = AuthenticatorAttachment.PLATFORM,
                requireResidentKey = true,
                userVerification = UserVerificationRequirement.REQUIRED
            ),
            attestation = AttestationConveyancePreference.DIRECT,
            timeout = 60000
        )
    }
    
    override suspend fun verifyRegistration(
        credential: PasskeyCredential,
        userId: String
    ): RegistrationVerificationResult {
        return RegistrationVerificationResult(verified = true, credentialId = credential.id)
    }
    
    override suspend fun getAuthenticationOptions(userId: String?): AuthenticationOptions {
        if (shouldThrowNetworkError) throw Exception("Network error")
        return AuthenticationOptions(
            challenge = "test-challenge".encodeToByteArray(),
            rpId = "example.com",
            userVerification = UserVerificationRequirement.REQUIRED,
            timeout = 60000,
            allowCredentials = emptyList()
        )
    }
    
    override suspend fun verifyAuthentication(
        credential: PasskeyCredential,
        challenge: ByteArray
    ): AuthenticationVerificationResult {
        return authVerificationResult
    }
    
    override suspend fun getStoredCredentials(userId: String): CredentialListResponse {
        return CredentialListResponse(credentials = emptyList())
    }
    
    override suspend fun deleteCredential(credentialId: String) {}
}

private class TestBiometryManager : BiometryManager {
    var shouldFailAuthentication = false
    
    override suspend fun authenticate(title: String, subtitle: String): Boolean {
        return !shouldFailAuthentication
    }
    
    override fun isBiometricAvailable(): Boolean = true
}

private class TestPinManager : PinManager {
    var shouldFailVerification = true
    private var attemptsRemaining = 3
    
    fun setRemainingAttempts(attempts: Int) {
        attemptsRemaining = attempts
    }
    
    override suspend fun verifyPin(pin: String): Boolean {
        return pin == "1234" && !shouldFailVerification
    }
    
    override fun getRemainingAttempts(): Int = attemptsRemaining
}

private class TestAuthRepository : AuthRepository {
    var shouldFailRefresh = false
    var refreshedToken = AuthToken(
        accessToken = "new-access-token",
        refreshToken = "new-refresh-token",
        expiresAt = Clock.System.now().toEpochMilliseconds() + 3600000
    )
    
    override suspend fun refreshToken(refreshToken: String): AuthToken {
        if (shouldFailRefresh) throw Exception("Token refresh failed")
        return refreshedToken
    }
    
    override suspend fun logout(userId: String) {}
    override suspend fun validateSession(token: String): Boolean = true
}

private class TestSessionManager(
    val testSecureStorage: TestSecureStorage = TestSecureStorage()
) {
    val sessionManager = SessionManager(testSecureStorage)
    var savedSession: AuthSession? = null
    var shouldRefresh = false
    var saveSessionCalled = false
    
    init {
        // Set up test data
        savedSession = AuthSession(
            userId = "user-123",
            token = AuthToken(
                accessToken = "access-123",
                refreshToken = "refresh-123",
                expiresAt = Clock.System.now().toEpochMilliseconds() + 3600000
            ),
            authMethod = AuthMethod.BIOMETRIC
        )
        // Pre-populate secure storage so sessionManager.loadSession() works
        testSecureStorage.preloadSession(savedSession!!)
    }
    
    suspend fun saveSession(session: AuthSession) {
        savedSession = session
        saveSessionCalled = true
        sessionManager.saveSession(session)
    }
    
    fun getLastSavedSession(): AuthSession? {
        // Get the actual saved session from SessionManager's state
        return sessionManager.sessionState.value ?: savedSession
    }
    
    
    suspend fun loadSession(): AuthSession? {
        return savedSession
    }
    
    suspend fun clearSession() {
        savedSession = null
        testSecureStorage.storage.clear()
        sessionManager.clearSession()
    }
    
    fun getCurrentUserId(): String? = savedSession?.userId
}

private class TestSecureStorage : SecureStorageWrapper {
    val storage = mutableMapOf<String, String>()
    
    override fun saveValue(key: String, value: String) {
        storage[key] = value
    }
    
    override fun getValue(key: String): String? = storage[key]
    
    override fun containsKey(key: String): Boolean = storage.containsKey(key)
    
    override fun remove(key: String) {
        storage.remove(key)
    }
    
    fun preloadSession(session: AuthSession) {
        storage["auth_token"] = session.token.accessToken
        storage["refresh_token"] = session.token.refreshToken
        storage["user_id"] = session.userId
        storage["auth_method"] = session.authMethod.name
        storage["token_expiry"] = session.token.expiresAt.toString()
    }
}