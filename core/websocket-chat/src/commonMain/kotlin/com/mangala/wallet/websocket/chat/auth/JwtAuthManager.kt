package com.mangala.wallet.websocket.chat.auth

import com.mangala.wallet.core.auth.SessionManager
import com.mangala.wallet.websocket.chat.auth.models.AuthToken
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

/**
 * JWT-based AuthManager implementation that uses the JWT token from SessionManager
 */
class JwtAuthManager(
    private val sessionManager: SessionManager
) : AuthManager {
    
    override suspend fun authenticate(): Result<AuthToken> {
        return try {
            // Get the current session from SessionManager
            val session = sessionManager.sessionState.first()
            
            if (session == null) {
                Napier.e("No active session found", tag = "JwtAuthManager")
                return Result.failure(AuthenticationException("No active session"))
            }
            
            // Check if token is valid
            if (!sessionManager.isSessionValid()) {
                Napier.e("Session token expired", tag = "JwtAuthManager")
                return Result.failure(AuthenticationException("Session token expired"))
            }
            
            // Create AuthToken from the JWT token
            val authToken = AuthToken(
                token = session.token.accessToken,
                expiresAt = session.token.expiresAt,
                issuedAt = Clock.System.now().toEpochMilliseconds()
            )
            
            Napier.i("JWT authentication successful", tag = "JwtAuthManager")
            Result.success(authToken)
        } catch (e: Exception) {
            Napier.e("JWT authentication failed", e, tag = "JwtAuthManager")
            Result.failure(e)
        }
    }
    
    override suspend fun refreshToken(): Result<AuthToken> {
        // In this implementation, token refresh should be handled by the auth module
        // For now, we'll just re-authenticate
        return authenticate()
    }
    
    override fun getCurrentToken(): AuthToken? {
        val session = sessionManager.sessionState.value ?: return null
        
        return if (sessionManager.isSessionValid()) {
            AuthToken(
                token = session.token.accessToken,
                expiresAt = session.token.expiresAt,
                issuedAt = Clock.System.now().toEpochMilliseconds()
            )
        } else {
            null
        }
    }
    
    override fun clearToken() {
        // Token clearing is handled by SessionManager
        Napier.d("Token clear requested - handled by SessionManager", tag = "JwtAuthManager")
    }
}