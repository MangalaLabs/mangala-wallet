package com.mangala.wallet.passkey.data.config

/**
 * Configuration for passkey operations
 */
object PasskeyConfig {
    // API Configuration
    const val DEFAULT_BASE_URL = "https://gateway.taman2h.fun"
    const val DEFAULT_RP_NAME = "Mangala Wallet"
    const val DEFAULT_TIMEOUT = 60000L // 60 seconds
    
    // HTTP Client Configuration
    const val HTTP_REQUEST_TIMEOUT = 60000L // 60 seconds
    const val HTTP_CONNECT_TIMEOUT = 30000L // 30 seconds
    const val HTTP_SOCKET_TIMEOUT = 60000L // 60 seconds
    
    // Retry Configuration
    const val MAX_RETRY_COUNT = 3
    const val RETRY_DELAY_MS = 1000L
    
    // WebAuthn Configuration
    const val AUTHENTICATOR_ATTACHMENT_PLATFORM = "platform"
    const val AUTHENTICATOR_ATTACHMENT_CROSS_PLATFORM = "cross-platform"
    const val PUBLIC_KEY_TYPE = "public-key"
    
    // Logging Configuration
    const val ENABLE_DEBUG_LOGGING = true // Should be false in production
    
    /**
     * Get base URL from environment or use default
     */
    fun getBaseUrl(): String {
        // In a real app, this would read from build config or environment
        return DEFAULT_BASE_URL
    }
    
    /**
     * Get RP ID from base URL
     */
    fun getRpId(baseUrl: String = getBaseUrl()): String {
        return try {
            val url = io.ktor.http.Url(baseUrl)
            url.host
        } catch (e: Exception) {
            "localhost"
        }
    }
}