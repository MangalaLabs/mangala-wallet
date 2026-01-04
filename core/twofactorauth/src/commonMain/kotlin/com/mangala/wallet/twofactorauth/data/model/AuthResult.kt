package com.mangala.wallet.twofactorauth.data.model

/**
 * Represents the result of a 2FA authentication attempt
 */
enum class AuthResult {
    SUCCESS,
    FAILED,
    NOT_REQUIRED,
    RATE_LIMITED
}