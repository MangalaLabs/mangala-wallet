package com.mangala.wallet.pin.domain.attempt

import com.mangala.wallet.pin.domain.security.SecurePIN
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

/**
 * Tracks failed PIN attempts and enforces lockout policy
 * Max 5 attempts, then 30-minute lockout
 */
class AttemptTracker(
    private val storage: AttemptStorage,
    private val deviceId: String
) {
    companion object {
        private const val MAX_ATTEMPTS = 5
        private val LOCKOUT_DURATION = 30.minutes
    }

    /**
     * Checks if account is locked and returns unlock time if locked
     */
    fun checkLockout(): LockoutResult {
        val data = storage.load() ?: return LockoutResult.NotLocked

        // Verify device ID to detect tampering
        if (!verifyIntegrity(data)) {
            // Clear potentially tampered data
            storage.clear()
            return LockoutResult.NotLocked
        }

        val now = Clock.System.now()

        if (data.isLocked) {
            val lockoutRemaining = data.lockoutUntil - now
            if (lockoutRemaining.isPositive()) {
                return LockoutResult.Locked(
                    unlockTime = data.lockoutUntil,
                    remainingTime = lockoutRemaining
                )
            } else {
                // Lockout expired, reset
                reset()
                return LockoutResult.NotLocked
            }
        }

        return LockoutResult.NotLocked
    }

    /**
     * Records a failed attempt and checks if lockout should be triggered
     */
    fun recordFailedAttempt(): AttemptResult {
        val data = storage.load()
        val now = Clock.System.now()

        val newFailedCount = if (data == null) {
            1
        } else {
            data.failedAttempts + 1
        }

        val remainingAttempts = MAX_ATTEMPTS - newFailedCount

        if (remainingAttempts <= 0) {
            // Trigger lockout
            val lockoutUntil = now + LOCKOUT_DURATION
            val lockedData = AttemptData(
                failedAttempts = newFailedCount,
                lastAttemptTime = now,
                isLocked = true,
                lockoutUntil = lockoutUntil,
                deviceId = deviceId,
                integrityHash = ""
            )

            // Add integrity hash
            val dataWithHash = lockedData.copy(
                integrityHash = computeIntegrityHash(lockedData)
            )

            storage.save(dataWithHash)

            return AttemptResult.Locked(lockoutUntil)
        }

        // Not locked yet, just record attempt
        val updatedData = AttemptData(
            failedAttempts = newFailedCount,
            lastAttemptTime = now,
            isLocked = false,
            lockoutUntil = now, // Not used when not locked
            deviceId = deviceId,
            integrityHash = ""
        )

        val dataWithHash = updatedData.copy(
            integrityHash = computeIntegrityHash(updatedData)
        )

        storage.save(dataWithHash)

        return AttemptResult.AttemptsRemaining(remainingAttempts)
    }

    /**
     * Records a successful attempt and resets counter
     */
    fun recordSuccessfulAttempt() {
        reset()
    }

    /**
     * Resets attempt tracking
     */
    fun reset() {
        storage.clear()
    }

    /**
     * Gets remaining attempts
     */
    fun getRemainingAttempts(): Int {
        val data = storage.load() ?: return MAX_ATTEMPTS
        return (MAX_ATTEMPTS - data.failedAttempts).coerceAtLeast(0)
    }

    /**
     * Computes integrity hash to detect tampering
     */
    private fun computeIntegrityHash(data: AttemptData): String {
        val content = "${data.failedAttempts}|${data.lastAttemptTime}|${data.isLocked}|${data.deviceId}"
        return content.hashCode().toString()
    }

    /**
     * Verifies data integrity
     */
    private fun verifyIntegrity(data: AttemptData): Boolean {
        val expectedHash = computeIntegrityHash(data)
        return data.integrityHash == expectedHash && data.deviceId == deviceId
    }
}

/**
 * Lockout check result
 */
sealed class LockoutResult {
    object NotLocked : LockoutResult()
    data class Locked(
        val unlockTime: Instant,
        val remainingTime: kotlin.time.Duration
    ) : LockoutResult()
}

/**
 * Attempt record result
 */
sealed class AttemptResult {
    data class AttemptsRemaining(val remaining: Int) : AttemptResult()
    data class Locked(val unlockTime: Instant) : AttemptResult()
}

/**
 * Attempt tracking data
 */
data class AttemptData(
    val failedAttempts: Int,
    val lastAttemptTime: Instant,
    val isLocked: Boolean,
    val lockoutUntil: Instant,
    val deviceId: String,
    val integrityHash: String
)

/**
 * Storage interface for attempt data
 */
interface AttemptStorage {
    fun save(data: AttemptData)
    fun load(): AttemptData?
    fun clear()
}
