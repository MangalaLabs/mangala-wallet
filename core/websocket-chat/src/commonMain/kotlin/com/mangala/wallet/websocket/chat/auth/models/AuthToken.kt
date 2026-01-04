package com.mangala.wallet.websocket.chat.auth.models

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class AuthToken(
    val token: String,
    val expiresAt: Long,
    val issuedAt: Long = Clock.System.now().toEpochMilliseconds()
) {
    val isExpired: Boolean
        get() = Clock.System.now().toEpochMilliseconds() >= expiresAt
    
    val shouldRefresh: Boolean
        get() = Clock.System.now().toEpochMilliseconds() >= (expiresAt - REFRESH_THRESHOLD_MILLIS)
    
    companion object {
        private const val REFRESH_THRESHOLD_MILLIS = 60_000L // Refresh 1 minute before expiry
    }
}