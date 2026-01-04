package com.mangala.wallet.auth.presentation

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.auth.AuthenticationFlowManager
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.auth.navigation.AuthNavigationHandler
import com.mangala.wallet.auth.repository.AuthRepository
import com.mangala.wallet.auth.repository.AuthRepositoryImpl
import com.mangala.wallet.core.auth.SessionManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Extended screen model for demo purposes with backend integration features
 */
class AuthDemoScreenModel(
    private val authFlowManager: AuthenticationFlowManager,
    val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
    private val navigationHandler: AuthNavigationHandler
) : StateScreenModel<AuthState>(AuthState.Initial) {
    
    val authState: StateFlow<AuthState> = authFlowManager.authState
    
    fun authenticate(userId: String? = null) {
        screenModelScope.launch {
            authFlowManager.authenticate(userId)
        }
    }
    
    fun authenticateWithPasskey(userId: String? = null) {
        screenModelScope.launch {
            val result = authFlowManager.authenticateWithPasskey(userId)
            // AuthFlowManager already handles setting the AuthState,
            // so we don't need to do anything with the result here
        }
    }
    
    fun authenticateWithBiometric() {
        screenModelScope.launch {
            authFlowManager.authenticateWithBiometric()
        }
    }
    
    
    fun logout() {
        screenModelScope.launch {
            authFlowManager.logout()
        }
    }
    
    suspend fun registerPasskey(userId: String, userName: String): Boolean {
        return authFlowManager.registerPasskey(
            userId = userId,
            userName = userName,
            userDisplayName = userName
        )
    }
    
    
    // Demo methods for testing backend integration
    
    fun extendSession() {
        screenModelScope.launch {
            try {
                sessionManager.extendSessionOnActivity()
                Napier.i("Session extended successfully")
            } catch (e: Exception) {
                Napier.e("Failed to extend session", e)
            }
        }
    }
    
    fun validateToken() {
        screenModelScope.launch {
            try {
                val session = sessionManager.sessionState.value
                val token = session?.token?.accessToken
                if (token != null) {
                    Napier.i("Validating token...")
                    Napier.i("Token (first 50 chars): ${token.take(50)}...")
                    val isValid = authRepository.validateSession(token)
                    Napier.i("Token validation result: $isValid")
                    
                    // Also check expiry time from session
                    val expiresAt = session.token.expiresAt
                    val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
                    val sessionExpired = currentTime > expiresAt
                    Napier.i("Session expiry check - Expires at: $expiresAt, Current time: $currentTime, Expired: $sessionExpired")
                } else {
                    Napier.w("No token available to validate")
                }
            } catch (e: Exception) {
                Napier.e("Failed to validate token", e)
                e.printStackTrace()
            }
        }
    }
    
    fun decodeJwt() {
        screenModelScope.launch {
            try {
                val session = sessionManager.sessionState.value
                val token = session?.token?.accessToken
                if (token != null && authRepository is AuthRepositoryImpl) {
                    Napier.i("Attempting to decode JWT token...")
                    val jwtInfo = authRepository.decodeJwt(token)
                    if (jwtInfo != null) {
                        Napier.i("JWT decoded successfully!")
                        Napier.i("User ID: ${jwtInfo.userId}")
                        Napier.i("Email: ${jwtInfo.email}")
                        Napier.i("Username: ${jwtInfo.preferredUsername}")
                        Napier.i("Expires at: ${jwtInfo.expiresAt}")
                        Napier.i("Issued at: ${jwtInfo.issuedAt}")
                        Napier.i("Full JWT content:\n${jwtInfo.raw}")
                    } else {
                        Napier.w("Failed to decode JWT - decodeJwt returned null")
                    }
                } else {
                    Napier.w("No token available to decode or wrong repository type")
                    Napier.w("Session: $session")
                    Napier.w("Token: $token")
                    Napier.w("Repository type: ${authRepository::class.simpleName}")
                }
            } catch (e: Exception) {
                Napier.e("Failed to decode JWT", e)
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Navigate to ConversationUi after successful authentication
     */
    fun navigateToConversationUi() {
        navigationHandler.navigateToConversationUi()
    }
}