package com.mangala.wallet.core.auth

import com.mangala.wallet.core.auth.domain.model.AuthSession
import com.mangala.wallet.core.auth.domain.model.AuthToken
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {

    val sessionState: StateFlow<AuthSession?>

    suspend fun hasSession(): Boolean
    suspend fun isSessionActive(): Boolean
    fun loadSession(): AuthSession?
    fun saveSession(session: AuthSession)
    fun clearSession()
    fun shouldRefreshToken(): Boolean
    fun updateToken(newToken: AuthToken)
    fun getCurrentUserId(): String?
    fun extendSessionOnActivity()
    fun isSessionValid(): Boolean
}