package com.mangala.wallet.auth

import com.mangala.wallet.core.auth.domain.model.AuthSession
import com.mangala.wallet.core.auth.domain.model.AuthToken
import com.mangala.wallet.core.auth.SessionManager
import com.mangala.wallet.core.auth.domain.model.AuthMethod
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

class SessionManagerImpl(
    private val secureStorage: SecureStorageWrapper
) : SessionManager {
    private val _sessionState = MutableStateFlow<AuthSession?>(null)
    override val sessionState: StateFlow<AuthSession?> = _sessionState.asStateFlow()
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_AUTH_METHOD = "auth_method"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val KEY_SESSION_START = "session_start"
        private const val KEY_LAST_ACTIVITY = "last_activity"
        private const val TOKEN_REFRESH_THRESHOLD = 5 * 60 * 1000L // 5 minutes
        private const val SESSION_BASE_DURATION = 30L * 24 * 60 * 60 * 1000 // 30 days
        private const val ACTIVITY_EXTENSION = 30L * 24 * 60 * 60 * 1000 // +30 days per activity
        private const val MAX_SESSION_DURATION = 180L * 24 * 60 * 60 * 1000 // 180 days max
    }
    
    override fun saveSession(session: AuthSession) {
        try {
            val currentTime = Clock.System.now().toEpochMilliseconds()
            
            // Save session data
            secureStorage.saveValue(KEY_AUTH_TOKEN, session.token.accessToken)
            secureStorage.saveValue(KEY_REFRESH_TOKEN, session.token.refreshToken)
            secureStorage.saveValue(KEY_USER_ID, session.userId)
            session.username?.let { secureStorage.saveValue(KEY_USERNAME, it) }
            secureStorage.saveValue(KEY_AUTH_METHOD, session.authMethod.name)
            secureStorage.saveValue(KEY_TOKEN_EXPIRY, session.token.expiresAt.toString())
            
            // Track session start time (if new session)
            val existingSessionStart = secureStorage.getValue(KEY_SESSION_START)
            if (existingSessionStart == null) {
                secureStorage.saveValue(KEY_SESSION_START, currentTime.toString())
            }
            
            // Update last activity time
            secureStorage.saveValue(KEY_LAST_ACTIVITY, currentTime.toString())
            
            _sessionState.value = session
            Napier.d("Session saved successfully")
        } catch (e: Exception) {
            Napier.e("Failed to save session", e)
            throw e
        }
    }

    override suspend fun hasSession(): Boolean {
        return loadSession() != null
    }

    override fun loadSession(): AuthSession? {
        return try {
            val accessToken = secureStorage.getValue(KEY_AUTH_TOKEN) ?: return null
            val refreshToken = secureStorage.getValue(KEY_REFRESH_TOKEN) ?: return null
            val userId = secureStorage.getValue(KEY_USER_ID) ?: return null
            val authMethodStr = secureStorage.getValue(KEY_AUTH_METHOD) ?: return null
            val expiresAtStr = secureStorage.getValue(KEY_TOKEN_EXPIRY) ?: return null
            val expiresAt = expiresAtStr.toLongOrNull() ?: return null
            
            val authMethod = AuthMethod.valueOf(authMethodStr)
            
            val username = secureStorage.getValue(KEY_USERNAME) // Load username if available
            
            val session = AuthSession(
                userId = userId,
                username = username,
                token = AuthToken(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresAt = expiresAt
                ),
                authMethod = authMethod
            )
            
            _sessionState.value = session
            session
        } catch (e: Exception) {
            Napier.e("Failed to load session", e)
            null
        }
    }
    
    override fun clearSession() {
        try {
            // Clear all session data
            secureStorage.remove(KEY_AUTH_TOKEN)
            secureStorage.remove(KEY_REFRESH_TOKEN)
            secureStorage.remove(KEY_USER_ID)
            secureStorage.remove(KEY_USERNAME)
            secureStorage.remove(KEY_AUTH_METHOD)
            secureStorage.remove(KEY_TOKEN_EXPIRY)
            
            // Clear session timing data (previously missing)
            secureStorage.remove(KEY_SESSION_START)
            secureStorage.remove(KEY_LAST_ACTIVITY)
            
            _sessionState.value = null
            Napier.d("Session cleared successfully")
        } catch (e: Exception) {
            Napier.e("Failed to clear session", e)
        }
    }
    
    override fun isSessionValid(): Boolean {
        val session = _sessionState.value ?: return false
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return session.token.expiresAt > currentTime
    }
    
    override fun shouldRefreshToken(): Boolean {
        val session = _sessionState.value ?: return false
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return (session.token.expiresAt - currentTime) <= TOKEN_REFRESH_THRESHOLD
    }
    
    override fun updateToken(newToken: AuthToken) {
        val currentSession = _sessionState.value ?: return
        
        val updatedSession = currentSession.copy(token = newToken)
        saveSession(updatedSession)
    }
    
    override fun getCurrentUserId(): String? {
        return _sessionState.value?.userId
    }
    
    fun getCurrentAuthMethod(): AuthMethod? {
        return _sessionState.value?.authMethod
    }
    
    /**
     * Extends the session on app activity
     * Rolling sessions: Active users get extended sessions up to MAX_SESSION_DURATION
     */
    override fun extendSessionOnActivity() {
        val session = _sessionState.value ?: return
        if (!isSessionValid()) return
        
        try {
            val currentTime = Clock.System.now().toEpochMilliseconds()
            val sessionStartStr = secureStorage.getValue(KEY_SESSION_START) ?: return
            val sessionStart = sessionStartStr.toLongOrNull() ?: return
            
            // Calculate new expiry with rolling session logic
            val timeSinceStart = currentTime - sessionStart
            val maxAllowedExpiry = sessionStart + MAX_SESSION_DURATION
            val proposedExpiry = currentTime + ACTIVITY_EXTENSION
            
            // Don't exceed max session duration
            val newExpiry = minOf(proposedExpiry, maxAllowedExpiry)
            
            // Only extend if it actually extends the session
            if (newExpiry > session.token.expiresAt) {
                val extendedToken = session.token.copy(expiresAt = newExpiry)
                val extendedSession = session.copy(token = extendedToken)
                
                // Update stored values
                secureStorage.saveValue(KEY_TOKEN_EXPIRY, newExpiry.toString())
                secureStorage.saveValue(KEY_LAST_ACTIVITY, currentTime.toString())
                
                _sessionState.value = extendedSession
                Napier.d("Session extended to: ${(newExpiry - currentTime) / (24 * 60 * 60 * 1000)} days")
            }
        } catch (e: Exception) {
            Napier.e("Failed to extend session", e)
        }
    }
    
    /**
     * Check if the session has been inactive for too long
     */
    override suspend fun isSessionActive(): Boolean {
        val session = _sessionState.value ?: return false
        if (!isSessionValid()) return false
        
        try {
            val lastActivityStr = secureStorage.getValue(KEY_LAST_ACTIVITY) ?: return false
            val lastActivity = lastActivityStr.toLongOrNull() ?: return false
            val currentTime = Clock.System.now().toEpochMilliseconds()
            
            // Consider session inactive if no activity for 30 days
            val inactivityThreshold = 30L * 24 * 60 * 60 * 1000
            return (currentTime - lastActivity) < inactivityThreshold
        } catch (e: Exception) {
            Napier.e("Failed to check session activity", e)
            return false
        }
    }
    
    private fun minOf(a: Long, b: Long): Long = if (a < b) a else b
}