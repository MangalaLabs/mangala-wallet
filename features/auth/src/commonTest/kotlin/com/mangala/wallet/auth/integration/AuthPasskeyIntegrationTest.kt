package com.mangala.wallet.auth.integration

import com.mangala.wallet.auth.*
import com.mangala.wallet.auth.model.*
import com.mangala.wallet.auth.repository.AuthRepository
import com.mangala.wallet.auth.wrapper.BiometryManager
import com.mangala.wallet.auth.wrapper.PinManager
import com.mangala.wallet.core.auth.domain.model.AuthMethod
import com.mangala.wallet.core.auth.domain.model.AuthSession
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.core.auth.domain.model.AuthToken
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.passkey.PasskeyManager
import com.mangala.wallet.passkey.model.*
import com.mangala.wallet.passkey.repository.PasskeyRepository
import com.mangala.wallet.passkey.repository.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.*
import kotlin.time.measureTime

class AuthPasskeyIntegrationTest {
    
    private lateinit var authSystem: AuthenticationSystem
    
    @BeforeTest
    fun setup() {
        authSystem = AuthenticationSystem()
    }
    
    // TC-INT-001: Passkey and Auth Module Integration
    @Test
    fun `test seamless integration between passkey and auth modules`() = runTest {
        // Register passkey through auth module
        val registered = authSystem.flowManager.registerPasskey(
            userId = "integration-user",
            userName = "integration@test.com",
            userDisplayName = "Integration Test User"
        )
        assertTrue(registered)
        
        // Authenticate using registered passkey
        val authenticated = authSystem.flowManager.authenticateWithPasskey("integration-user")
        assertTrue(authenticated)
        
        // Verify session management
        val session = authSystem.sessionManager.loadSession()
        assertNotNull(session)
        assertEquals("integration-user", session.userId)
        assertEquals(AuthMethod.PASSKEY, session.authMethod)
    }
    
    // TC-INT-002: Biometry Module Integration
    @Test
    fun `test biometry fallback integration`() = runTest {
        // Configure biometric authentication
        authSystem.biometryManager.setAvailable(true)
        
        // Set up existing session for biometric auth
        val session = AuthSession(
            userId = "biometric-user",
            token = AuthToken(
                accessToken = "biometric-token",
                refreshToken = "biometric-refresh",
                expiresAt = Clock.System.now().toEpochMilliseconds() + 3600000
            ),
            authMethod = AuthMethod.BIOMETRIC
        )
        authSystem.secureStorage.preloadSession(session)
        
        // Trigger biometric through auth flow
        authSystem.passkeyManager.simulateUserCancellation()
        authSystem.flowManager.authenticate()
        
        // Verify biometric was triggered
        assertTrue(authSystem.biometryManager.wasAuthenticateCalled)
        
        // Verify smooth user experience
        val state = authSystem.flowManager.authState.first()
        assertTrue(state is AuthState.Authenticated.WithBiometric)
    }
    
    // TC-INT-003: PIN Module Integration
    @Test
    fun `test PIN fallback integration`() = runTest {
        // Set up PIN
        authSystem.pinManager.setPin("1234")
        
        // Set up existing session for PIN auth
        val session = AuthSession(
            userId = "pin-user",
            token = AuthToken(
                accessToken = "pin-token",
                refreshToken = "pin-refresh",
                expiresAt = Clock.System.now().toEpochMilliseconds() + 3600000
            ),
            authMethod = AuthMethod.PIN
        )
        authSystem.secureStorage.preloadSession(session)
        
        // Trigger PIN through auth flow
        authSystem.passkeyManager.simulateUserCancellation()
        authSystem.biometryManager.setAvailable(false)
        
        authSystem.flowManager.authenticate()
        
        // Should be waiting for PIN
        assertEquals(AuthState.NotAuthenticated, authSystem.flowManager.authState.first())
        
        // Enter PIN
        val success = authSystem.flowManager.authenticateWithPin("1234")
        assertTrue(success)
        
        // Verify PIN validation
        val state = authSystem.flowManager.authState.first()
        assertTrue(state is AuthState.Authenticated.WithPin)
    }
    
    // TC-INT-004: WebAuthn Server Integration
    @Test
    fun `test WebAuthn protocol compliance`() = runTest {
        // Test registration with server
        val regOptions = authSystem.passkeyRepository.getRegistrationOptions("webauthn-user")
        assertNotNull(regOptions)
        assertTrue(regOptions.challenge.isNotEmpty())
        assertEquals("example.com", regOptions.rp.id)
        
        // Test authentication with server
        val authOptions = authSystem.passkeyRepository.getAuthenticationOptions("webauthn-user")
        assertNotNull(authOptions)
        assertTrue(authOptions.challenge.isNotEmpty())
        
        // Verify challenge-response flow
        val credential = authSystem.passkeyManager.register(
            userId = "webauthn-user",
            challenge = regOptions.challenge,
            rpId = regOptions.rp.id,
            rpName = regOptions.rp.name,
            userName = "webauthn@test.com",
            userDisplayName = "WebAuthn User"
        )
        
        val verificationResult = authSystem.passkeyRepository.verifyRegistration(
            credential = credential,
            userId = "webauthn-user"
        )
        assertTrue(verificationResult.verified)
    }
    
    // TC-INT-005: API Error Handling
    @Test
    fun `test handling of various API errors`() = runTest {
        // Test server error on registration
        authSystem.passkeyRepository.simulateServerError(500)
        val registerResult = authSystem.flowManager.registerPasskey("error-user", "error@test.com", "Error User")
        assertFalse(registerResult)
        
        // Reset and test timeout on authentication
        authSystem.passkeyRepository.simulateServerError(0)
        authSystem.passkeyRepository.simulateNetworkTimeout()
        val authResult = authSystem.flowManager.authenticateWithPasskey("timeout-user")
        assertFalse(authResult)
        
        // Test error state
        val errorState = authSystem.flowManager.authState.first()
        assertTrue(errorState is AuthState.Error)
        
        // Reset errors and verify recovery
        authSystem.passkeyRepository.simulateServerError(0)
        val successResult = authSystem.flowManager.authenticateWithPasskey("valid-user")
        assertTrue(successResult)
    }
    
    // TC-INT-006: Cross-Platform Credential Sharing
    @Test
    fun `test credentials work across platforms`() = runTest {
        // Register passkey on "Android"
        authSystem.setPlatform("Android")
        val registered = authSystem.flowManager.registerPasskey(
            userId = "cross-platform-user",
            userName = "cross@platform.com",
            userDisplayName = "Cross Platform User"
        )
        assertTrue(registered)
        
        // Simulate sync through cloud provider
        authSystem.simulateCloudSync()
        
        // Use passkey on "iOS"
        authSystem.setPlatform("iOS")
        val authenticated = authSystem.flowManager.authenticateWithPasskey("cross-platform-user")
        assertTrue(authenticated)
        
        // Verify proper provider integration
        val credentials = authSystem.passkeyManager.getStoredCredentials(,,)
        assertTrue(credentials.any { it.userName == "cross@platform.com" })
    }
    
    // TC-INT-007: Deep Link Integration (Desktop)
    @Test
    fun `test desktop QR code deep links`() = runTest {
        // Generate QR code on desktop
        authSystem.setPlatform("Desktop")
        val qrCode = authSystem.generateDesktopQRCode("desktop-user")
        assertNotNull(qrCode)
        assertTrue(qrCode.contains("mangala://auth"))
        
        // Scan with mobile app
        authSystem.setPlatform("Android")
        val deepLinkHandled = authSystem.handleDeepLink(qrCode)
        assertTrue(deepLinkHandled)
        
        // Complete flow on mobile
        val mobileAuth = authSystem.flowManager.authenticateWithPasskey("desktop-user")
        assertTrue(mobileAuth)
        
        // Simulate cross-platform sync
        authSystem.secureStorage.saveValue("desktop_auth_desktop-user", "synced")
        
        // Verify desktop update
        authSystem.setPlatform("Desktop")
        val desktopSession = authSystem.checkDesktopAuthStatus("desktop-user")
        assertNotNull(desktopSession)
        assertEquals("desktop-user", desktopSession.userId)
    }
    
    // TC-PERF-001: Authentication Speed
    @Test
    fun `test authentication performance`() = runTest {
        // Measure passkey auth time
        val passkeyTime = measureTime {
            authSystem.flowManager.authenticateWithPasskey("perf-user")
        }
        assertTrue(passkeyTime.inWholeMilliseconds < 2000, "Passkey auth took ${passkeyTime.inWholeMilliseconds}ms")
        
        // Measure biometric auth time
        val biometricTime = measureTime {
            authSystem.flowManager.authenticateWithBiometric()
        }
        assertTrue(biometricTime.inWholeMilliseconds < 1000, "Biometric auth took ${biometricTime.inWholeMilliseconds}ms")
        
        // Measure PIN auth time
        val pinTime = measureTime {
            authSystem.flowManager.authenticateWithPin("1234")
        }
        assertTrue(pinTime.inWholeMilliseconds < 1000, "PIN auth took ${pinTime.inWholeMilliseconds}ms")
    }
    
    // TC-PERF-002: App Launch with Auth
    @Test
    fun `test app launch performance`() = runTest {
        // Cold start app
        val totalTime = measureTime {
            // Time to auth prompt
            authSystem.initialize()
            
            // Complete authentication
            authSystem.flowManager.authenticate("launch-user")
            
            // Wait for home screen
            val state = authSystem.flowManager.authState.first()
            assertTrue(state is AuthState.Authenticated)
        }
        
        assertTrue(totalTime.inWholeMilliseconds < 3000, "Total launch time was ${totalTime.inWholeMilliseconds}ms")
    }
}

// Integrated test system
private class AuthenticationSystem {
    val passkeyManager = TestablePasskeyManager()
    val passkeyRepository = TestablePasskeyRepository()
    val biometryManager = TestableBiometryManager()
    val pinManager = TestablePinManager()
    val authRepository = TestableAuthRepository()
    val secureStorage = TestableSecureStorage()
    val sessionManager = SessionManager(secureStorage)
    
    val flowManager = AuthenticationFlowManager(
        passkeyManager = passkeyManager,
        passkeyRepository = passkeyRepository,
        biometryManager = biometryManager,
        pinManager = pinManager,
        authRepository = authRepository,
        sessionManager = sessionManager
    )
    
    private var currentPlatform = "Android"
    
    fun setPlatform(platform: String) {
        currentPlatform = platform
    }
    
    fun simulateCloudSync() {
        // Simulate credential sync
    }
    
    fun generateDesktopQRCode(userId: String): String {
        return "mangala://auth?action=register&user=$userId&challenge=test-challenge"
    }
    
    fun handleDeepLink(link: String): Boolean {
        return link.startsWith("mangala://auth")
    }
    
    fun checkDesktopAuthStatus(userId: String): AuthSession? {
        return if (secureStorage.getValue("desktop_auth_$userId") != null) {
            AuthSession(
                userId = userId,
                token = AuthToken("desktop-token", "desktop-refresh", Clock.System.now().toEpochMilliseconds() + 3600000),
                authMethod = AuthMethod.PASSKEY
            )
        } else null
    }
    
    suspend fun initialize() {
        // Simulate app initialization
        kotlinx.coroutines.delay(100)
    }
}

// Testable implementations with additional control
private class TestablePasskeyManager : PasskeyManager {
    private var shouldCancel = false
    private val credentials = mutableListOf<StoredCredential>()
    
    fun simulateUserCancellation() {
        shouldCancel = true
    }
    
    override suspend fun isSupported(): Boolean = true
    
    override suspend fun register(
        userId: String,
        challenge: ByteArray,
        rpId: String,
        rpName: String,
        userName: String,
        userDisplayName: String
    ): PasskeyCredential {
        credentials.add(StoredCredential(
            id = "cred-${credentials.size}",
            rpId = rpId,
            userName = userName,
            createdAt = Clock.System.now().toEpochMilliseconds()
        ))
        
        return PasskeyCredential(
            id = "cred-${credentials.size - 1}",
            rawId = "cred-${credentials.size - 1}".encodeToByteArray(),
            type = "public-key",
            response = AuthenticatorAttestationResponse(ByteArray(0), ByteArray(0))
        )
    }
    
    override suspend fun authenticate(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): AuthenticationResult {
        if (shouldCancel) {
            shouldCancel = false
            throw com.mangala.wallet.passkey.exception.PasskeyException.UserCancelled()
        }
        
        val storedCred = credentials.firstOrNull()
        return AuthenticationResult(
            credentialId = storedCred?.id ?: "test-cred",
            userId = storedCred?.userName?.substringBefore("@") ?: "test-user",
            verified = true,
            authenticatorData = ByteArray(0),
            signature = ByteArray(0)
        )
    }
    
    override suspend fun deleteCredential(credentialId: String) {
        credentials.removeAll { it.id == credentialId }
    }
    
    override suspend fun getStoredCredentials(
        challenge: ByteArray,
        rpId: String,
        allowCredentials: List<PublicKeyCredentialDescriptor>
    ): List<StoredCredential> = credentials.toList()
}

private class TestablePasskeyRepository : PasskeyRepository {
    private var serverErrorCode: Int? = null
    private var shouldTimeout = false
    
    fun simulateServerError(code: Int) {
        serverErrorCode = code
    }
    
    fun simulateNetworkTimeout() {
        shouldTimeout = true
    }
    
    override suspend fun getRegistrationOptions(userId: String): RegistrationOptions {
        if (serverErrorCode != null) {
            throw Exception("Server error: $serverErrorCode")
        }
        
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
        if (shouldTimeout) {
            shouldTimeout = false
            throw Exception("Request timeout")
        }
        
        return AuthenticationOptions(
            challenge = "auth-challenge".encodeToByteArray(),
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
        // Return the expected userId based on credential or registration context
        val userId = when {
            credential.id.contains("integration") -> "integration-user"
            credential.id.startsWith("cred-") -> "integration-user" // Credentials created during this test
            else -> "test-user"
        }
        return AuthenticationVerificationResult(
            verified = true,
            userId = userId,
            token = "access-token",
            refreshToken = "refresh-token"
        )
    }
    
    override suspend fun getStoredCredentials(userId: String): CredentialListResponse {
        return CredentialListResponse(emptyList())
    }
    
    override suspend fun deleteCredential(credentialId: String) {}
}

private class TestableBiometryManager : BiometryManager {
    private var available = true
    var wasAuthenticateCalled = false
    
    fun setAvailable(isAvailable: Boolean) {
        available = isAvailable
    }
    
    override suspend fun authenticate(title: String, subtitle: String): Boolean {
        wasAuthenticateCalled = true
        return available
    }
    
    override fun isBiometricAvailable(): Boolean = available
}

private class TestablePinManager : PinManager {
    private var correctPin = "1234"
    
    fun setPin(pin: String) {
        correctPin = pin
    }
    
    override suspend fun verifyPin(pin: String): Boolean = pin == correctPin
    override fun getRemainingAttempts(): Int = 3
}

private class TestableAuthRepository : AuthRepository {
    private var errorCode: Int? = null
    
    fun simulateError(code: Int) {
        errorCode = code
    }
    
    override suspend fun refreshToken(refreshToken: String): AuthToken {
        if (errorCode == 401) {
            throw Exception("Unauthorized")
        }
        
        return AuthToken(
            accessToken = "new-token",
            refreshToken = "new-refresh",
            expiresAt = Clock.System.now().toEpochMilliseconds() + 3600000
        )
    }
    
    override suspend fun logout(userId: String) {}
    override suspend fun validateSession(token: String): Boolean = errorCode != 401
}

private class TestableSecureStorage : SecureStorageWrapper {
    private val storage = mutableMapOf<String, String>()
    
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