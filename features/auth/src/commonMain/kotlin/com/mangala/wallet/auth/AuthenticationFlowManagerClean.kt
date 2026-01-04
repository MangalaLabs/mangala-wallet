package com.mangala.wallet.auth

import com.mangala.wallet.auth.repository.AuthRepository
import com.mangala.wallet.biometry.BiometryAuthenticator
import com.mangala.wallet.core.auth.SessionManager
import com.mangala.wallet.core.auth.domain.model.AuthMethod
import com.mangala.wallet.core.auth.domain.model.AuthSession
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.core.auth.domain.model.AuthToken
import com.mangala.wallet.passkey.domain.usecase.AuthenticateWithPasskeyUseCase
import com.mangala.wallet.passkey.domain.usecase.CheckPasskeySupportUseCase
import com.mangala.wallet.passkey.domain.usecase.RegisterPasskeyUseCase
import dev.icerock.moko.resources.desc.desc
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

/**
 * Clean architecture implementation of AuthenticationFlowManager
 */
class AuthenticationFlowManagerClean(
    private val registerPasskeyUseCase: RegisterPasskeyUseCase,
    private val authenticateWithPasskeyUseCase: AuthenticateWithPasskeyUseCase,
    private val checkPasskeySupportUseCase: CheckPasskeySupportUseCase,
    private val biometryAuthenticator: BiometryAuthenticator,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    suspend fun authenticate(userId: String? = null) {
        _authState.value = AuthState.Loading
        
        // Check if we have an existing session first
        val existingSession = sessionManager.loadSession()
        if (existingSession != null && !sessionManager.shouldRefreshToken()) {
            // We have a valid session, try biometric if available
            if (biometryAuthenticator.isBiometricAvailable()) {
                val biometricResult = tryBiometricAuthentication()
                if (biometricResult != null) {
                    handleAuthenticationSuccess(biometricResult)
                    return
                }
            }
        }
        
        // Try passkey if supported
        if (checkPasskeySupportUseCase()) {
            val passkeyResult = tryPasskeyAuthentication(userId)
            if (passkeyResult != null) {
                handleAuthenticationSuccess(passkeyResult)
                return
            }
        }
        
        // Final fallback - require user to authenticate manually
        _authState.value = AuthState.NotAuthenticated
    }
    
    suspend fun authenticateWithPasskey(userId: String? = null, username: String? = null): Boolean {
        _authState.value = AuthState.Loading
        
        return try {
            val result = authenticateWithPasskeyUseCase(
                userId = userId,
                username = username
            )
            
            if (result.success && result.accessToken != null && result.refreshToken != null) {
                val session = AuthSession(
                    userId = result.userId ?: userId ?: "",
                    username = username,
                    token = AuthToken(
                        accessToken = result.accessToken!!,
                        refreshToken = result.refreshToken!!,
                        expiresAt = Clock.System.now().toEpochMilliseconds() + (result.expiresIn ?: 300000L)
                    ),
                    authMethod = AuthMethod.PASSKEY
                )
                
                sessionManager.saveSession(session)
                _authState.value = AuthState.Authenticated.WithPasskey(
                    userId = session.userId,
                    credentialId = "" // Not available in clean model
                )
                true
            } else {
                _authState.value = AuthState.Error.PasskeyError(
                    message = result.errorMessage ?: "Authentication failed"
                )
                false
            }
        } catch (e: Exception) {
            Napier.e("Passkey authentication failed", e)
            _authState.value = AuthState.Error.UnknownError(
                message = e.message ?: "Unknown error occurred"
            )
            false
        }
    }
    
    suspend fun registerPasskey(
        userId: String,
        userName: String,
        userDisplayName: String
    ): Boolean {
        return try {
            val result = registerPasskeyUseCase(
                userId = userId,
                userName = userName,
                displayName = userDisplayName
            )
            
            if (result.success) {
                // Handle auto-login if registration returns tokens
                if (result.accessToken != null && result.refreshToken != null) {
                    val session = AuthSession(
                        userId = result.userId ?: userId,
                        username = userName,
                        token = AuthToken(
                            accessToken = result.accessToken!!,
                            refreshToken = result.refreshToken!!,
                            expiresAt = Clock.System.now().toEpochMilliseconds() + (result.expiresIn ?: 300000L)
                        ),
                        authMethod = AuthMethod.PASSKEY
                    )
                    
                    sessionManager.saveSession(session)
                    _authState.value = AuthState.Authenticated.WithPasskey(
                        userId = session.userId,
                        credentialId = result.credentialId ?: ""
                    )
                    Napier.d("Passkey registration successful with auto-login")
                } else {
                    Napier.d("Passkey registration successful (no auto-login)")
                }
            }
            
            result.success
        } catch (e: Exception) {
            Napier.e("Passkey registration failed", e)
            false
        }
    }
    
    suspend fun authenticateWithBiometric(): Boolean {
        _authState.value = AuthState.Loading
        
        return try {
            // Load the existing session if available
            val existingSession = sessionManager.loadSession()
            if (existingSession == null || !sessionManager.isSessionValid()) {
                _authState.value = AuthState.Error.BiometricError(
                    message = "No valid session found. Please login with passkey first."
                )
                return false
            }
            
            val result = biometryAuthenticator.checkBiometryAuthentication(
                requestTitle = "Authenticate".desc(),
                requestReason = "Use your biometric to access your wallet".desc(),
                failureButtonText = "Cancel".desc()
            )
            
            if (result) {
                // Use the existing session instead of creating a mock one
                _authState.value = AuthState.Authenticated.WithBiometric(
                    userId = existingSession.userId
                )
                true
            } else {
                _authState.value = AuthState.NotAuthenticated
                false
            }
        } catch (e: Exception) {
            Napier.e("Biometric authentication failed", e)
            _authState.value = AuthState.Error.BiometricError(
                message = e.message ?: "Biometric authentication failed"
            )
            false
        }
    }
    
    suspend fun logout() {
        try {
            val userId = sessionManager.getCurrentUserId()
            if (userId != null) {
                authRepository.logout(userId)
            }
            sessionManager.clearSession()
            _authState.value = AuthState.NotAuthenticated
        } catch (e: Exception) {
            Napier.e("Logout failed", e)
        }
    }
    
    suspend fun refreshTokenIfNeeded() {
        if (sessionManager.shouldRefreshToken()) {
            val currentSession = sessionManager.sessionState.value ?: return
            
            try {
                val newToken = authRepository.refreshToken(currentSession.token.refreshToken)
                sessionManager.updateToken(newToken)
            } catch (e: Exception) {
                Napier.e("Token refresh failed", e)
                _authState.value = AuthState.NotAuthenticated
            }
        }
    }
    
    private suspend fun tryPasskeyAuthentication(userId: String?): AuthSession? {
        return try {
            val currentSession = sessionManager.sessionState.value
            if (authenticateWithPasskey(userId, currentSession?.username)) {
                sessionManager.sessionState.value
            } else {
                null
            }
        } catch (e: Exception) {
            Napier.e("Passkey authentication attempt failed", e)
            null
        }
    }
    
    private suspend fun tryBiometricAuthentication(): AuthSession? {
        return try {
            if (authenticateWithBiometric()) {
                sessionManager.sessionState.value
            } else {
                null
            }
        } catch (e: Exception) {
            Napier.e("Biometric authentication attempt failed", e)
            null
        }
    }
    
    private fun handleAuthenticationSuccess(session: AuthSession) {
        _authState.value = when (session.authMethod) {
            AuthMethod.PASSKEY -> AuthState.Authenticated.WithPasskey(
                userId = session.userId,
                credentialId = ""
            )
            AuthMethod.BIOMETRIC -> AuthState.Authenticated.WithBiometric(
                userId = session.userId
            )
            AuthMethod.PIN -> AuthState.Authenticated.WithPin(
                userId = session.userId
            )
        }
    }
}