package com.mangala.wallet.pin.domain.ratelimit

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Rate limiter with exponential backoff to prevent brute force attacks
 * Pattern: 1s → 2s → 4s → 8s → 5min
 */
class RateLimiter(
    private val storage: RateLimitStorage
) {
    private val backoffSequence = listOf(
        1.seconds,
        2.seconds,
        4.seconds,
        8.seconds,
        5.minutes
    )

    private val resetAfter = 1.hours

    /**
     * Checks if action is allowed and returns retry duration if rate limited
     */
    fun checkRateLimit(): RateLimitResult {
        val data = storage.load() ?: return RateLimitResult.Allowed

        val now = Clock.System.now()

        // Reset if last attempt was more than 1 hour ago
        if ((now - data.lastAttempt) > resetAfter) {
            storage.clear()
            return RateLimitResult.Allowed
        }

        // Check if still in cooldown
        val cooldownRemaining = data.cooldownUntil - now
        if (cooldownRemaining.isPositive()) {
            return RateLimitResult.RateLimited(cooldownRemaining)
        }

        return RateLimitResult.Allowed
    }

    /**
     * Records a failed attempt and applies backoff
     */
    fun recordFailedAttempt() {
        val now = Clock.System.now()
        val data = storage.load()

        val newAttemptCount = if (data == null || (now - data.lastAttempt) > resetAfter) {
            1
        } else {
            data.attemptCount + 1
        }

        val backoffDuration = backoffSequence.getOrElse(newAttemptCount - 1) { backoffSequence.last() }
        val cooldownUntil = now + backoffDuration

        storage.save(
            RateLimitData(
                attemptCount = newAttemptCount,
                lastAttempt = now,
                cooldownUntil = cooldownUntil
            )
        )
    }

    /**
     * Resets rate limit state
     */
    fun reset() {
        storage.clear()
    }
}

/**
 * Rate limit check result
 */
sealed class RateLimitResult {
    object Allowed : RateLimitResult()
    data class RateLimited(val retryAfter: Duration) : RateLimitResult()
}

/**
 * Rate limit data
 */
data class RateLimitData(
    val attemptCount: Int,
    val lastAttempt: Instant,
    val cooldownUntil: Instant
)

/**
 * Storage interface for rate limit data
 */
interface RateLimitStorage {
    fun save(data: RateLimitData)
    fun load(): RateLimitData?
    fun clear()
}
