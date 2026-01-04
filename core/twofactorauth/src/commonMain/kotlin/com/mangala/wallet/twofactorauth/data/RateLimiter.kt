package com.mangala.wallet.twofactorauth.data

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.twofactorauth.domain.exception.RateLimitException
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis

/**
 * Rate limiter to prevent brute-force attacks
 */
class RateLimiter(private val secureStorage: SecureStorageWrapper) {
    companion object {
        private const val KEY_FAILED_ATTEMPTS = "two_factor_failed_attempts"
        private const val KEY_LAST_ATTEMPT_TIME = "two_factor_last_attempt_time"
        private const val MAX_ATTEMPTS_BEFORE_SHORT_DELAY = 3
        private const val MAX_ATTEMPTS_BEFORE_LONG_DELAY = 5
        private const val SHORT_DELAY_MS = 5_000 // 5 seconds
        private const val LONG_DELAY_MS = 30_000 // 30 seconds
    }

    /**
     * Get the current number of failed attempts
     */
    fun getFailedAttempts(): Int {
        return secureStorage.getValue(KEY_FAILED_ATTEMPTS)?.toIntOrNull() ?: 0
    }

    /**
     * Get the timestamp of the last authentication attempt
     */
    fun getLastAttemptTime(): Long {
        return secureStorage.getValue(KEY_LAST_ATTEMPT_TIME)?.toLongOrNull() ?: 0L
    }

    /**
     * Increment the failed attempts counter
     */
    fun incrementFailedAttempts() {
        val currentAttempts = getFailedAttempts()
        secureStorage.saveValue(KEY_FAILED_ATTEMPTS, (currentAttempts + 1).toString())
        val now = localDateTimeToMillis(localDateTimeNow())
        secureStorage.saveValue(KEY_LAST_ATTEMPT_TIME, now.toString())
    }

    /**
     * Reset the failed attempts counter
     */
    fun resetAttempts() {
        secureStorage.saveValue(KEY_FAILED_ATTEMPTS, "0")
    }

    /**
     * Check if the current request is rate limited
     * @throws RateLimitException if rate limited
     */
    fun checkRateLimiting() {
        val failedAttempts = getFailedAttempts()
        val lastAttemptTime = getLastAttemptTime()
        val currentTime = localDateTimeToMillis(localDateTimeNow())
        val timeSinceLastAttempt = currentTime - lastAttemptTime

        when {
            failedAttempts >= MAX_ATTEMPTS_BEFORE_LONG_DELAY -> {
                if (timeSinceLastAttempt < LONG_DELAY_MS) {
                    val waitTime = (LONG_DELAY_MS - timeSinceLastAttempt) / 1000
                    throw RateLimitException(
                        "Too many failed attempts. Please wait $waitTime seconds before trying again."
                    )
                }
            }
            failedAttempts >= MAX_ATTEMPTS_BEFORE_SHORT_DELAY -> {
                if (timeSinceLastAttempt < SHORT_DELAY_MS) {
                    val waitTime = (SHORT_DELAY_MS - timeSinceLastAttempt) / 1000
                    throw RateLimitException(
                        "Please wait $waitTime seconds before trying again."
                    )
                }
            }
        }

        // Update last attempt time
        secureStorage.saveValue(KEY_LAST_ATTEMPT_TIME, currentTime.toString())
    }
}