package com.mangala.wallet.passkey.domain.model

/**
 * Domain model for passkey credential
 */
data class PasskeyCredentialDomain(
    val id: String,
    val userId: String,
    val userName: String,
    val displayName: String,
    val createdAt: Long
)

/**
 * Domain model for registration request
 */
data class PasskeyRegistrationRequest(
    val userId: String,
    val userName: String,
    val displayName: String
)

/**
 * Domain model for registration result
 */
data class PasskeyRegistrationResult(
    val success: Boolean,
    val credentialId: String? = null,
    val userId: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Long? = null,
    val errorMessage: String? = null
)

/**
 * Domain model for authentication request
 */
data class PasskeyAuthenticationRequest(
    val userId: String? = null,
    val username: String? = null
)

/**
 * Domain model for authentication result
 */
data class PasskeyAuthenticationResult(
    val success: Boolean,
    val userId: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Long? = null,
    val errorMessage: String? = null
)

/**
 * Domain model for passkey configuration
 */
data class PasskeyConfiguration(
    val rpName: String = "Mangala Wallet",
    val rpId: String,
    val baseUrl: String,
    val timeout: Long = 60000L,
    val userVerification: UserVerificationRequirement = UserVerificationRequirement.PREFERRED
)

/**
 * User verification requirement for passkey operations
 */
enum class UserVerificationRequirement {
    REQUIRED,
    PREFERRED,
    DISCOURAGED
}