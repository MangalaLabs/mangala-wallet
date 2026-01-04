package com.mangala.wallet.websocket.chat.auth

import io.github.aakira.napier.Napier
import com.mangala.wallet.websocket.chat.auth.models.AuthToken
import com.mangala.wallet.websocket.chat.websocket.models.ChatFrame
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.benasher44.uuid.uuid4
import kotlinx.datetime.Clock.System

class AuthManagerImpl(
    private val httpClient: HttpClient,
    private val walletKeyProvider: WalletKeyProvider,
    private val authEndpoint: String,
    private val json: Json
) : AuthManager {
    
    private val mutex = Mutex()
    
    private var currentToken: AuthToken? = null
    
    override suspend fun authenticate(): Result<AuthToken> = mutex.withLock {
        try {
            // Check if we have a valid token
            currentToken?.let { token ->
                if (!token.isExpired && !token.shouldRefresh) {
                    Napier.d("Using existing valid token", tag = "AuthManagerImpl")
                    return Result.success(token)
                }
            }
            
            Napier.d("Requesting authentication challenge", tag = "AuthManagerImpl")
            
            // Step 1: Get challenge from server
            val challengeResponse = httpClient.post("$authEndpoint/challenge") {
                contentType(ContentType.Application.Json)
                setBody(ChallengeRequest(
                    publicKey = walletKeyProvider.getPublicKey(),
                    timestamp = System.now().toEpochMilliseconds()
                ))
            }
            
            if (!challengeResponse.status.isSuccess()) {
                return Result.failure(
                    AuthenticationException("Failed to get challenge: ${challengeResponse.status}")
                )
            }
            
            val challenge: ChallengeResponse = challengeResponse.body()
            Napier.d("Received challenge: ${challenge.challenge}", tag = "AuthManagerImpl")
            
            // Step 2: Sign the challenge
            val signature = walletKeyProvider.signMessage(challenge.challenge)
                ?: return Result.failure(
                    AuthenticationException("Failed to sign challenge")
                )
            
            // Step 3: Submit signed challenge
            val authResponse = httpClient.post("$authEndpoint/verify") {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(
                    challenge = challenge.challenge,
                    signature = signature,
                    publicKey = walletKeyProvider.getPublicKey(),
                    timestamp = System.now().toEpochMilliseconds()
                ))
            }
            
            if (!authResponse.status.isSuccess()) {
                return Result.failure(
                    AuthenticationException("Authentication failed: ${authResponse.status}")
                )
            }
            
            val authResult: AuthResponse = authResponse.body()
            
            val token = AuthToken(
                token = authResult.token,
                expiresAt = authResult.expiresAt,
                issuedAt = authResult.issuedAt
            )
            
            currentToken = token
            Napier.i("Authentication successful, token expires at: ${token.expiresAt}", tag = "AuthManagerImpl")
            
            Result.success(token)
        } catch (e: Exception) {
            Napier.e("Authentication failed", e, tag = "AuthManagerImpl")
            Result.failure(e)
        }
    }
    
    override suspend fun refreshToken(): Result<AuthToken> = mutex.withLock {
        val token = currentToken
            ?: return authenticate() // No token to refresh, authenticate instead
        
        try {
            Napier.d("Refreshing authentication token", tag = "AuthManagerImpl")
            
            val refreshResponse = httpClient.post("$authEndpoint/refresh") {
                contentType(ContentType.Application.Json)
                bearerAuth(token.token)
                setBody(RefreshRequest(
                    publicKey = walletKeyProvider.getPublicKey(),
                    timestamp = System.now().toEpochMilliseconds()
                ))
            }
            
            if (!refreshResponse.status.isSuccess()) {
                Napier.w("Token refresh failed, attempting re-authentication", tag = "AuthManagerImpl")
                currentToken = null
                return authenticate()
            }
            
            val refreshResult: AuthResponse = refreshResponse.body()
            
            val newToken = AuthToken(
                token = refreshResult.token,
                expiresAt = refreshResult.expiresAt,
                issuedAt = refreshResult.issuedAt
            )
            
            currentToken = newToken
            Napier.i("Token refreshed successfully, expires at: ${newToken.expiresAt}", tag = "AuthManagerImpl")
            
            Result.success(newToken)
        } catch (e: Exception) {
            Napier.e("Token refresh failed, attempting re-authentication", e, tag = "AuthManagerImpl")
            currentToken = null
            authenticate()
        }
    }
    
    override fun getCurrentToken(): AuthToken? = currentToken
    
    override fun clearToken() {
        currentToken = null
        Napier.d("Authentication token cleared", tag = "AuthManagerImpl")
    }
}

interface WalletKeyProvider {
    fun getPublicKey(): String
    suspend fun signMessage(message: String): String?
}

class AuthenticationException(message: String) : Exception(message)

@Serializable
private data class ChallengeRequest(
    val publicKey: String,
    val timestamp: Long
)

@Serializable
private data class ChallengeResponse(
    val challenge: String,
    val expiresAt: Long
)

@Serializable
private data class AuthRequest(
    val challenge: String,
    val signature: String,
    val publicKey: String,
    val timestamp: Long
)

@Serializable
private data class AuthResponse(
    val token: String,
    val expiresAt: Long,
    val issuedAt: Long
)

@Serializable
private data class RefreshRequest(
    val publicKey: String,
    val timestamp: Long
)