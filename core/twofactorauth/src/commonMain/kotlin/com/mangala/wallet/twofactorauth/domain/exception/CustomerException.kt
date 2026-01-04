package com.mangala.wallet.twofactorauth.domain.exception

/**
 * Custom exceptions
 */
sealed class TwoFactorException(message: String, cause: Throwable? = null) : Exception(message, cause)
class RateLimitException(message: String) : TwoFactorException(message)
class BackupCorruptedException : TwoFactorException("Backup data is corrupted or invalid")
class AuthenticationFailedException : TwoFactorException("Authentication failed")