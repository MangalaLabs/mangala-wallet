package com.mangala.wallet.pin.domain

import com.mangala.wallet.pin.domain.attempt.AttemptResult
import com.mangala.wallet.pin.domain.attempt.AttemptTracker
import com.mangala.wallet.pin.domain.attempt.LockoutResult
import com.mangala.wallet.pin.domain.ratelimit.RateLimiter
import com.mangala.wallet.pin.domain.ratelimit.RateLimitResult
import com.mangala.wallet.pin.domain.repository.PINRepository
import com.mangala.wallet.pin.domain.security.*
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Main coordinator for PIN operations
 * Orchestrates: Repository + AttemptTracker + RateLimiter
 */
class PINManager(
    private val repository: PINRepository,
    private val attemptTracker: AttemptTracker,
    private val rateLimiter: RateLimiter
) {

    /**
     * Sets up a new PIN
     */
    fun setupPIN(pin: CharArray, confirmPin: CharArray): Result<Unit> {
        if (pin.size != 6) {
            return Result.failure(InvalidPINLengthException())
        }

        if (!pin.all { it.isDigit() }) {
            return Result.failure(InvalidPINFormatException())
        }

        if (!pin.contentEquals(confirmPin)) {
            return Result.failure(PINMismatchException())
        }

        return try {
            SecurePIN(pin).use { securePIN ->
                val result = repository.storePIN(securePIN)
                if (result.isSuccess) {
                    // Reset any existing attempt tracking
                    attemptTracker.reset()
                    rateLimiter.reset()
                }
                result
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            // Clear sensitive data
            CommonSecurityUtils.clearCharArray(pin)
            CommonSecurityUtils.clearCharArray(confirmPin)
        }
    }

    /**
     * Validates a PIN
     */
    fun validatePIN(pin: CharArray): Result<PINValidationResult> {
        return try {
            // Check lockout first
            when (val lockout = attemptTracker.checkLockout()) {
                is LockoutResult.Locked -> {
                    return Result.success(
                        PINValidationResult.Locked(
                            unlockTime = lockout.unlockTime,
                            remainingDuration = lockout.remainingTime
                        )
                    )
                }
                is LockoutResult.NotLocked -> {
                    // Continue to rate limit check
                }
            }

            // Check rate limit
            when (val rateLimit = rateLimiter.checkRateLimit()) {
                is RateLimitResult.RateLimited -> {
                    return Result.success(
                        PINValidationResult.RateLimited(rateLimit.retryAfter)
                    )
                }
                is RateLimitResult.Allowed -> {
                    // Continue to validation
                }
            }

            // Validate PIN length and format
            if (pin.size != 6 || !pin.all { it.isDigit() }) {
                rateLimiter.recordFailedAttempt()
                attemptTracker.recordFailedAttempt()
                return Result.success(
                    PINValidationResult.Invalid(attemptTracker.getRemainingAttempts())
                )
            }

            // Verify PIN
            val isValid = SecurePIN(pin).use { securePIN ->
                repository.verifyPIN(securePIN).getOrThrow()
            }

            if (isValid) {
                // Success - reset tracking
                attemptTracker.recordSuccessfulAttempt()
                rateLimiter.reset()
                Result.success(PINValidationResult.Success)
            } else {
                // Failed - record attempt
                rateLimiter.recordFailedAttempt()
                when (val attemptResult = attemptTracker.recordFailedAttempt()) {
                    is AttemptResult.Locked -> {
                        Result.success(
                            PINValidationResult.Locked(
                                unlockTime = attemptResult.unlockTime,
                                remainingDuration = kotlin.time.Duration.ZERO // Will be calculated by UI
                            )
                        )
                    }
                    is AttemptResult.AttemptsRemaining -> {
                        Result.success(
                            PINValidationResult.Invalid(attemptResult.remaining)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            // Clear sensitive data
            CommonSecurityUtils.clearCharArray(pin)
        }
    }

    /**
     * Changes an existing PIN
     */
    fun changePIN(
        currentPin: CharArray,
        newPin: CharArray,
        confirmNewPin: CharArray
    ): Result<Unit> {
        return try {
            // First validate current PIN
            val validationResult = validatePIN(currentPin)
            if (validationResult.isFailure) {
                return Result.failure(validationResult.exceptionOrNull()!!)
            }

            when (val result = validationResult.getOrNull()) {
                is PINValidationResult.Success -> {
                    // Current PIN is valid, set up new PIN
                    setupPIN(newPin, confirmNewPin)
                }
                else -> {
                    Result.failure(Exception("Current PIN validation failed: $result"))
                }
            }
        } finally {
            CommonSecurityUtils.clearCharArray(currentPin)
            CommonSecurityUtils.clearCharArray(newPin)
            CommonSecurityUtils.clearCharArray(confirmNewPin)
        }
    }

    /**
     * Checks if PIN is set up
     */
    fun isPINSetup(): Boolean {
        return repository.hasPIN()
    }

    /**
     * Clears PIN and all tracking data
     */
    fun clearPIN() {
        repository.clearPIN()
        attemptTracker.reset()
        rateLimiter.reset()
    }

    /**
     * Gets remaining attempts before lockout
     */
    fun getRemainingAttempts(): Int {
        return attemptTracker.getRemainingAttempts()
    }
}

/**
 * Result of PIN validation
 */
sealed class PINValidationResult {
    object Success : PINValidationResult()

    data class Invalid(val remainingAttempts: Int) : PINValidationResult()

    data class Locked(
        val unlockTime: Instant,
        val remainingDuration: Duration
    ) : PINValidationResult()

    data class RateLimited(val retryAfter: Duration) : PINValidationResult()
}
