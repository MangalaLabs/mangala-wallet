//package com.mangala.wallet.auth
//
//import com.mangala.wallet.auth.model.*
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.datetime.Clock
//
///**
// * Mock authentication flow manager for testing without backend
// */
//class MockAuthenticationFlowManager {
//    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
//    val authState: StateFlow<AuthState> = _authState.asStateFlow()
//
//    private val sessionManager = MockSessionManager()
//
//    suspend fun authenticateWithPasskey(userId: String? = null): Boolean {
//        _authState.value = AuthState.Loading
//
//        // Simulate network delay
//        delay(1000)
//
//        // Mock successful passkey authentication
//        val mockSession = AuthSession(
//            userId = userId ?: "demo_user",
//            token = AuthToken(
//                accessToken = "mock_passkey_token",
//                refreshToken = "mock_passkey_refresh",
//                expiresAt = Clock.System.now().toEpochMilliseconds() + (30L * 24 * 60 * 60 * 1000)
//            ),
//            authMethod = AuthMethod.PASSKEY
//        )
//
//        sessionManager.saveSession(mockSession)
//        _authState.value = AuthState.Authenticated.WithPasskey(
//            userId = mockSession.userId,
//            credentialId = "mock_credential_${userId ?: "demo"}"
//        )
//        return true
//    }
//
//    suspend fun authenticateWithBiometric(): Boolean {
//        _authState.value = AuthState.Loading
//
//        // Biometric is handled by the real BiometryAuthenticator
//        // This just updates the state after successful biometric
//        val mockSession = AuthSession(
//            userId = "demo_user",
//            token = AuthToken(
//                accessToken = "mock_biometric_token",
//                refreshToken = "mock_biometric_refresh",
//                expiresAt = Clock.System.now().toEpochMilliseconds() + (30L * 24 * 60 * 60 * 1000)
//            ),
//            authMethod = AuthMethod.BIOMETRIC
//        )
//
//        sessionManager.saveSession(mockSession)
//        _authState.value = AuthState.Authenticated.WithBiometric(
//            userId = mockSession.userId
//        )
//        return true
//    }
//
//    suspend fun registerPasskey(userId: String, userName: String): Boolean {
//        _authState.value = AuthState.Loading
//
//        // Simulate registration delay
//        delay(1500)
//
//        // Mock successful registration
//        println("Mock Register - User: $userName, ID: $userId")
//        _authState.value = AuthState.Initial
//        return true
//    }
//
//    suspend fun logout() {
//        sessionManager.clearSession()
//        _authState.value = AuthState.NotAuthenticated
//    }
//
//    private class MockSessionManager {
//        private var currentSession: AuthSession? = null
//
//        fun saveSession(session: AuthSession) {
//            currentSession = session
//        }
//
//        fun loadSession(): AuthSession? = currentSession
//
//        fun clearSession() {
//            currentSession = null
//        }
//    }
//}