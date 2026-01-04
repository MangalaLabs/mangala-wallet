package com.mangala.wallet.core.auth.domain.model

sealed interface AuthState {
    data object Initial : AuthState
    data object Loading : AuthState

    sealed interface Authenticated : AuthState {
        val userId: String
        val authMethod: AuthMethod

        data class WithPasskey(
            override val userId: String,
            val credentialId: String
        ) : Authenticated {
            override val authMethod = AuthMethod.PASSKEY
        }

        data class WithBiometric(
            override val userId: String
        ) : Authenticated {
            override val authMethod = AuthMethod.BIOMETRIC
        }

        data class WithPin(
            override val userId: String
        ) : Authenticated {
            override val authMethod = AuthMethod.PIN
        }
    }

    sealed interface Error : AuthState {
        val message: String
        val canRetry: Boolean

        data class PasskeyError(
            override val message: String,
            override val canRetry: Boolean = true
        ) : Error

        data class BiometricError(
            override val message: String,
            override val canRetry: Boolean = true
        ) : Error

        data class PinError(
            override val message: String,
            override val canRetry: Boolean = true,
            val attemptsRemaining: Int? = null
        ) : Error

        data class NetworkError(
            override val message: String,
            override val canRetry: Boolean = true
        ) : Error

        data class UnknownError(
            override val message: String,
            override val canRetry: Boolean = false
        ) : Error
    }

    data object NotAuthenticated : AuthState
}

enum class AuthMethod {
    PASSKEY,
    BIOMETRIC,
    PIN
}

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long
)

data class AuthSession(
    val userId: String,
    val username: String? = null,
    val token: AuthToken,
    val authMethod: AuthMethod
)