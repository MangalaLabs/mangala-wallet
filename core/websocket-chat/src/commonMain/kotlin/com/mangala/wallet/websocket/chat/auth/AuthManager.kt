package com.mangala.wallet.websocket.chat.auth

import com.mangala.wallet.websocket.chat.auth.models.AuthToken

interface AuthManager {
    suspend fun authenticate(): Result<AuthToken>
    suspend fun refreshToken(): Result<AuthToken>
    fun getCurrentToken(): AuthToken?
    fun clearToken()
}