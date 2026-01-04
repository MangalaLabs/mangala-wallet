package com.mangala.wallet.auth.repository

import com.mangala.wallet.auth.di.createHttpClient
import com.mangala.wallet.core.auth.domain.model.AuthToken
import io.ktor.client.*
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class AuthRepositoryImpl(
    private val baseUrl: String = "http://localhost:8089/api/v1",
    private val httpClient: HttpClient = createHttpClient()
) : AuthRepository {
    
    override suspend fun refreshToken(refreshToken: String): AuthToken {
        // The backend doesn't have a specific refresh endpoint yet
        // For now, we'll simulate refresh by returning a new token
        // In production, this should call the actual refresh endpoint
        return AuthToken(
            accessToken = "refreshed_${Clock.System.now().epochSeconds}",
            refreshToken = refreshToken,
            expiresAt = Clock.System.now().toEpochMilliseconds() + (60 * 60 * 1000) // 1 hour
        )
    }
    
    override suspend fun logout(userId: String) {
        // The backend doesn't have a logout endpoint yet
        // In production, this should invalidate the session on the server
        // For now, we'll just simulate logout locally
    }
    
    override suspend fun validateSession(token: String): Boolean {
        return try {
            println("AuthRepository: Validating token: ${token.take(50)}...")
            
            // Use a proper validation endpoint or decode the token locally
            // For now, we can check if the token is properly formatted
            val parts = token.split(".")
            if (parts.size != 3) {
                println("AuthRepository: Invalid JWT format - expected 3 parts, got ${parts.size}")
                return false
            }
            
            // Try to decode the payload to check if it's valid
            val payload = parts[1]
            val decodedPayload = try {
                // Base64 URL decode - replace URL-safe chars and add padding
                val base64Payload = payload
                    .replace('-', '+')
                    .replace('_', '/')
                val paddedPayload = when (base64Payload.length % 4) {
                    2 -> base64Payload + "=="
                    3 -> base64Payload + "="
                    else -> base64Payload
                }
                Base64.decode(paddedPayload).decodeToString()
            } catch (e: Exception) {
                println("AuthRepository: Failed to decode JWT payload: ${e.message}")
                return false
            }
            
            println("AuthRepository: Decoded JWT payload: $decodedPayload")
            
            // Check if the token has expired
            val json = Json { ignoreUnknownKeys = true }
            val tokenData = try {
                json.decodeFromString<JwtPayload>(decodedPayload)
            } catch (e: Exception) {
                println("AuthRepository: Failed to parse JWT payload: ${e.message}")
                return false
            }
            
            val currentTime = Clock.System.now().epochSeconds
            val isExpired = tokenData.exp < currentTime
            
            println("AuthRepository: Token exp: ${tokenData.exp}, current time: $currentTime, expired: $isExpired")
            
            return !isExpired
        } catch (e: Exception) {
            println("AuthRepository: Error validating token: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Decode JWT to extract user information
     */
    suspend fun decodeJwt(token: String): JwtDecodeResponse? {
        return try {
            println("AuthRepository: Decoding JWT token: ${token.take(50)}...")
            
            // Decode JWT locally
            val parts = token.split(".")
            if (parts.size != 3) {
                println("AuthRepository: Invalid JWT format - expected 3 parts, got ${parts.size}")
                return null
            }
            
            // Decode header
            val header = parts[0]
            val decodedHeader = try {
                // Base64 URL decode - replace URL-safe chars and add padding
                val base64Header = header
                    .replace('-', '+')
                    .replace('_', '/')
                val paddedHeader = when (base64Header.length % 4) {
                    2 -> base64Header + "=="
                    3 -> base64Header + "="
                    else -> base64Header
                }
                Base64.decode(paddedHeader).decodeToString()
            } catch (e: Exception) {
                println("AuthRepository: Failed to decode JWT header: ${e.message}")
                ""
            }
            
            // Decode payload
            val payload = parts[1]
            val decodedPayload = try {
                // Base64 URL decode - replace URL-safe chars and add padding
                val base64Payload = payload
                    .replace('-', '+')
                    .replace('_', '/')
                val paddedPayload = when (base64Payload.length % 4) {
                    2 -> base64Payload + "=="
                    3 -> base64Payload + "="
                    else -> base64Payload
                }
                Base64.decode(paddedPayload).decodeToString()
            } catch (e: Exception) {
                println("AuthRepository: Failed to decode JWT payload: ${e.message}")
                return null
            }
            
            println("AuthRepository: Decoded JWT header: $decodedHeader")
            println("AuthRepository: Decoded JWT payload: $decodedPayload")
            
            // Parse the payload to extract user info
            val json = Json { ignoreUnknownKeys = true }
            val tokenData = try {
                json.decodeFromString<JwtPayload>(decodedPayload)
            } catch (e: Exception) {
                println("AuthRepository: Failed to parse JWT payload: ${e.message}")
                return null
            }
            
            JwtDecodeResponse(
                userId = tokenData.sub,
                raw = "Header: $decodedHeader\n\nPayload: $decodedPayload",
                email = tokenData.email,
                preferredUsername = tokenData.preferredUsername,
                expiresAt = tokenData.exp,
                issuedAt = tokenData.iat
            )
        } catch (e: Exception) {
            println("AuthRepository: Error decoding JWT: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}

data class JwtDecodeResponse(
    val userId: String,
    val raw: String, // Raw decoded JWT content
    val email: String? = null,
    val preferredUsername: String? = null,
    val expiresAt: Long? = null,
    val issuedAt: Long? = null
)

@Serializable
data class JwtPayload(
    val exp: Long,
    val iat: Long,
    val jti: String? = null,
    val iss: String? = null,
    val aud: String? = null,
    val sub: String,
    val typ: String? = null,
    val azp: String? = null,
    val sid: String? = null,
    val acr: String? = null,
    @SerialName("allowed-origins")
    val allowedOrigins: List<String>? = null,
    @SerialName("realm_access")
    val realmAccess: RealmAccess? = null,
    @SerialName("resource_access")
    val resourceAccess: Map<String, ResourceAccess>? = null,
    val scope: String? = null,
    @SerialName("email_verified")
    val emailVerified: Boolean? = null,
    @SerialName("preferred_username")
    val preferredUsername: String? = null,
    val email: String? = null
)

@Serializable
data class RealmAccess(
    val roles: List<String>? = null
)

@Serializable
data class ResourceAccess(
    val roles: List<String>? = null
)