package com.mangala.wallet.auth.repository

import com.mangala.wallet.core.auth.domain.model.AuthToken

interface AuthRepository {
    suspend fun refreshToken(refreshToken: String): AuthToken
    
    suspend fun logout(userId: String)
    
    suspend fun validateSession(token: String): Boolean
}