package com.mangala.wallet.auth

import com.mangala.wallet.auth.repository.AuthRepository
import com.mangala.wallet.auth.storage.CompletedPasskeyStorage
import com.mangala.wallet.biometry.BiometryAuthenticator
import com.mangala.wallet.core.auth.SessionManager
import com.mangala.wallet.core.auth.domain.model.AuthMethod
import com.mangala.wallet.core.auth.domain.model.AuthSession
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.core.auth.domain.model.AuthToken
import com.mangala.wallet.passkey.PasskeyManager
import com.mangala.wallet.passkey.exception.PasskeyException
import com.mangala.wallet.passkey.model.PublicKeyCredentialDescriptor
import com.mangala.wallet.passkey.model.AuthenticatorTransport
import com.mangala.wallet.passkey.repository.PasskeyRepository
import dev.icerock.moko.resources.desc.desc
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

class AuthenticationFlowManager(
    private val passkeyManager: PasskeyManager,
    private val passkeyRepository: PasskeyRepository,
    private val biometryAuthenticator: BiometryAuthenticator,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val completedPasskeyStorage: CompletedPasskeyStorage
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    suspend fun hasSession() = sessionManager.hasSession()
    
    suspend fun authenticate(userId: String? = null) {
        Napier.d("AuthenticationFlowManager: authenticate called with userId: $userId")
        _authState.value = AuthState.Loading
        
        // Check if we have an existing session first
        val existingSession = sessionManager.loadSession()
        Napier.d("AuthenticationFlowManager: existingSession: ${existingSession != null}")
        if (existingSession != null && !sessionManager.shouldRefreshToken()) {
            Napier.d("AuthenticationFlowManager: Valid session exists, checking biometric")
            // We have a valid session, try biometric if available
            if (biometryAuthenticator.isBiometricAvailable()) {
                val biometricResult = tryBiometricAuthentication()
                if (biometricResult != null) {
                    handleAuthenticationSuccess(biometricResult)
                    return
                }
            }
        }
        
        // Try passkey if supported and no valid session
        Napier.d("AuthenticationFlowManager: Checking passkey support")
        val isPasskeySupported = passkeyManager.isSupported()
        Napier.d("AuthenticationFlowManager: isPasskeySupported: $isPasskeySupported")
        if (isPasskeySupported) {
            Napier.d("AuthenticationFlowManager: Passkey is supported, trying authentication")
            val passkeyResult = tryPasskeyAuthentication(userId)
            if (passkeyResult != null) {
                Napier.d("AuthenticationFlowManager: Passkey authentication successful")
                handleAuthenticationSuccess(passkeyResult)
                return
            }
            Napier.d("AuthenticationFlowManager: Passkey authentication returned null")
        } else {
            Napier.d("AuthenticationFlowManager: Passkey is not supported")
        }
        
        // Final fallback - require user to authenticate manually
        Napier.d("AuthenticationFlowManager: Setting state to NotAuthenticated")
        _authState.value = AuthState.NotAuthenticated
    }
    
    suspend fun authenticateWithPasskey(userId: String? = null): Result<Unit> {
        _authState.value = AuthState.Loading

        return try {
            // Get the stored username if available
            val currentSession = sessionManager.sessionState.value
            val username = currentSession?.username

            Napier.d("AuthenticationFlowManager: Starting authenticateWithPasskey for userId: $userId, username: $username")

            // Get list of completed passkey IDs from local storage
            Napier.e("🚨 AUTH DEBUG: Starting authentication for userId: $userId")

            val completedCredentialIds = completedPasskeyStorage.getCredentialIdsForUser(userId)
            Napier.e("🚨 AUTH DEBUG: Found ${completedCredentialIds.size} completed passkeys for userId: $userId")

            // Debug: Show all completed passkeys
            val allCompletedPasskeys = completedPasskeyStorage.getCompletedPasskeys()
            Napier.e("🚨 AUTH DEBUG: Total passkeys in storage: ${allCompletedPasskeys.size}")

            if (allCompletedPasskeys.isEmpty()) {
                Napier.e("❌ AUTH DEBUG: NO PASSKEYS IN STORAGE - This is why filtering shows nothing!")
            } else {
                Napier.e("✅ AUTH DEBUG: Passkeys found in storage:")
                allCompletedPasskeys.forEach { passkey ->
                    Napier.e("  - User: '${passkey.userId}', CredentialId: ${passkey.credentialId.take(20)}...")
                    if (userId != null) {
                        val matches = passkey.userId == userId
                        Napier.e("  - Matches current userId '$userId': $matches")
                    }
                }
            }

            val authOptions = passkeyRepository.getAuthenticationOptions(userId, username)

            // Smart filtering based on stored completed passkeys
            val hasCompletedPasskeys = completedCredentialIds.isNotEmpty()

            val filteredAllowCredentials = if (hasCompletedPasskeys) {
                // We have successful registrations - apply strict filtering
                Napier.w("✅ FILTERING ACTIVE: Showing ONLY ${completedCredentialIds.size} verified passkeys")
                Napier.w("✅ Orphaned passkeys will be HIDDEN from the selector")

                completedCredentialIds.map { credentialId ->
                    PublicKeyCredentialDescriptor(
                        type = "public-key",
                        id = credentialId,
                        transports = listOf(AuthenticatorTransport.INTERNAL)
                    )
                }
            } else {
                // IMPORTANT: No successful registrations = Hide ALL passkeys including orphaned
                // This forces users to register properly instead of seeing failed passkeys
                Napier.w("🔒 NO PASSKEYS SHOWN: No verified passkeys in storage")
                Napier.w("🔒 All passkeys (including orphaned) are HIDDEN")
                Napier.w("💡 User must register a new account first")

                // Create a fake/impossible credential ID that will never match
                // This ensures NO passkeys will be shown in the selector
                listOf(
                    PublicKeyCredentialDescriptor(
                        type = "public-key",
                        id = "IMPOSSIBLE_CREDENTIAL_ID_THAT_WILL_NEVER_MATCH".encodeToByteArray(),
                        transports = listOf(AuthenticatorTransport.INTERNAL)
                    )
                )
            }

            Napier.w("🔍 DEBUG: Final filtered credentials count: ${filteredAllowCredentials.size}")
            Napier.w("🔍 DEBUG: Backend original credentials count: ${authOptions.allowCredentials.size}")
            Napier.d("AuthenticationFlowManager: Got auth options - challenge length: ${authOptions.challenge.size}, rpId: ${authOptions.rpId}")

            // Check if we're blocking all passkeys
            if (!hasCompletedPasskeys) {
                Napier.w("⚠️ Authentication will likely fail - no verified passkeys available")
            }

            Napier.d("AuthenticationFlowManager: Calling passkeyManager.authenticate()")
            val authResult = passkeyManager.authenticate(
                challenge = authOptions.challenge,
                rpId = authOptions.rpId,
                allowCredentials = filteredAllowCredentials  // Use filtered list
            )
            Napier.d("AuthenticationFlowManager: passkeyManager.authenticate() returned: $authResult")
            
            // Get the full credential data from the passkey manager
            val credential = passkeyManager.getLastAuthenticationCredential()
                ?: throw PasskeyException.UnknownError("Failed to retrieve authentication credential")
            
            val verificationResult = passkeyRepository.verifyAuthentication(
                credential = credential,
                challenge = authOptions.challenge
            )
            
            if (verificationResult.verified && verificationResult.token != null && verificationResult.refreshToken != null) {
                // Calculate expiry based on backend's session duration (30 days default)
                val sessionDuration = 30L * 24 * 60 * 60 * 1000 // 30 days in milliseconds
                val session = AuthSession(
                    userId = verificationResult.userId ?: userId ?: "",
                    username = currentSession?.username, // Preserve the username from previous session
                    token = AuthToken(
                        accessToken = verificationResult.token!!,
                        refreshToken = verificationResult.refreshToken!!,
                        expiresAt = Clock.System.now().toEpochMilliseconds() + sessionDuration
                    ),
                    authMethod = AuthMethod.PASSKEY
                )

                sessionManager.saveSession(session)

                // Save this passkey as completed if not already saved
                val credentialId = authResult.credentialId
                Napier.d("AuthenticationFlowManager: Saving authenticated passkey to completed storage: $credentialId")
                completedPasskeyStorage.saveCompletedPasskey(
                    credentialId = credentialId,
                    userId = session.userId
                )

                _authState.value = AuthState.Authenticated.WithPasskey(
                    userId = session.userId,
                    credentialId = credentialId
                )
                Result.success(Unit)
            } else {
                _authState.value = AuthState.Error.PasskeyError(
                    message = verificationResult.message ?: "Authentication failed"
                )
                Result.failure(PasskeyException.UnknownError(verificationResult.message ?: "Authentication failed"))
            }
        } catch (e: PasskeyException) {
            _authState.value = when (e) {
                is PasskeyException.UserCancelled -> AuthState.NotAuthenticated
                is PasskeyException.NotSupported -> AuthState.Error.PasskeyError(
                    message = "Passkey not supported",
                    canRetry = false
                )
                else -> AuthState.Error.PasskeyError(
                    message = e.message ?: "Passkey authentication failed"
                )
            }
            Result.failure(e)
        } catch (e: Exception) {
            Napier.e("Passkey authentication failed", e)
            _authState.value = AuthState.Error.UnknownError(
                message = e.message ?: "Unknown error occurred"
            )
            Result.failure(e)
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
    
    
    suspend fun registerPasskey(
        userId: String,
        userName: String,
        userDisplayName: String
    ): Boolean {
        _authState.value = AuthState.Loading
        
        return try {
            val registrationOptions = passkeyRepository.getRegistrationOptions(userId, userName)
            
            // CRITICAL FIX: Use the Base64 UUID from backend, NOT the email
            val userIdToUse = registrationOptions.user.originalId ?: userId
            
            val credential = passkeyManager.register(
                userId = userIdToUse,  // Use Base64 UUID instead of email
                challenge = registrationOptions.challenge,
                rpId = registrationOptions.rp.id,
                rpName = registrationOptions.rp.name,
                userName = registrationOptions.user.name,  // Use backend's user name
                userDisplayName = registrationOptions.user.displayName  // Use backend's display name
            )
            
            val verificationResult = passkeyRepository.verifyRegistration(
                credential = credential,
                userId = userId
            )
            
            // Handle auto-login if tokens are returned from registration
            if (verificationResult.verified) {
                // Save this passkey as completed ONLY if verification succeeded
                val credentialId = verificationResult.credentialId ?: credential.id
                Napier.e("🚨 CRITICAL: Registration SUCCESS - Backend returned verified=true")
                Napier.e("🚨 CRITICAL: Now saving passkey to completed storage")
                Napier.e("🚨 CRITICAL: Credential ID to save: $credentialId")
                Napier.e("🚨 CRITICAL: User ID to save: $userId")

                try {
                    completedPasskeyStorage.saveCompletedPasskey(
                        credentialId = credentialId,
                        userId = userId
                    )
                    Napier.e("✅ CRITICAL: Passkey SAVED successfully to completed storage")

                    // Verify it was saved
                    val allSaved = completedPasskeyStorage.getCompletedPasskeys()
                    Napier.e("✅ CRITICAL: Total passkeys now in storage: ${allSaved.size}")
                    allSaved.forEach { pk ->
                        Napier.e("  - Saved: userId=${pk.userId}, credentialId=${pk.credentialId.take(20)}...")
                    }
                } catch (e: Exception) {
                    Napier.e("❌ CRITICAL: Failed to save passkey to storage", e)
                }

                if (verificationResult.token != null && verificationResult.refreshToken != null) {
                    // Calculate expiry based on backend's session duration (30 days default)
                    val sessionDuration = 30L * 24 * 60 * 60 * 1000 // 30 days in milliseconds
                    val session = AuthSession(
                        userId = verificationResult.userId ?: userId,
                        username = userName, // Store the username
                        token = AuthToken(
                            accessToken = verificationResult.token!!,
                            refreshToken = verificationResult.refreshToken!!,
                            expiresAt = Clock.System.now().toEpochMilliseconds() + sessionDuration
                        ),
                        authMethod = AuthMethod.PASSKEY
                    )

                    sessionManager.saveSession(session)
                    _authState.value = AuthState.Authenticated.WithPasskey(
                        userId = session.userId,
                        credentialId = credentialId
                    )
                }
            } else {
                // Registration failed - DO NOT save passkey to completed storage
                Napier.d("AuthenticationFlowManager: Registration verification failed, not saving passkey")
            }

            verificationResult.verified
        } catch (e: Exception) {
            Napier.e("Passkey registration failed", e)
            throw e
        }
    }
    
    suspend fun logout() {
        try {
            val userId = sessionManager.getCurrentUserId()
            if (userId != null) {
                authRepository.logout(userId)
                // Clear completed passkeys for this user on logout
                Napier.d("AuthenticationFlowManager: Clearing completed passkeys for user: $userId")
                completedPasskeyStorage.clearUserPasskeys(userId)
            }
            sessionManager.clearSession()
            _authState.value = AuthState.NotAuthenticated
        } catch (e: Exception) {
            Napier.e("Logout failed", e)
        }
    }
    
    fun resetAuthState() {
        _authState.value = AuthState.NotAuthenticated
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
            Napier.d("AuthenticationFlowManager: tryPasskeyAuthentication called with userId: $userId")
            val result = authenticateWithPasskey(userId)
            if (result.isSuccess) {
                Napier.d("AuthenticationFlowManager: authenticateWithPasskey returned success")
                sessionManager.sessionState.value
            } else {
                Napier.d("AuthenticationFlowManager: authenticateWithPasskey returned failure: ${result.exceptionOrNull()?.message}")
                null
            }
        } catch (e: Exception) {
            Napier.d("AuthenticationFlowManager: tryPasskeyAuthentication caught exception: ${e.message}")
            e.printStackTrace()
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
                credentialId = "" // Would need to be stored/retrieved
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