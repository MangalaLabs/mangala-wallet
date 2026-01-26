package com.mangala.wallet.pin.domain.repository

import com.mangala.wallet.pin.domain.security.SecurePIN
import com.mangala.wallet.pin.domain.security.SecurityUtils

/**
 * Repository for PIN storage and verification
 */
class PINRepository(
    private val storage: PINRepositoryStorage
) {
    companion object {
        private const val PBKDF2_ITERATIONS = 100_000
    }

    /**
     * Stores a new PIN with hashing
     */
    fun storePIN(pin: SecurePIN): Result<Unit> {
        return try {
            val salt = SecurityUtils.generateSalt(32)
            val hash = pin.hash(salt, PBKDF2_ITERATIONS)

            storage.save(
                StoredPIN(
                    hash = hash,
                    salt = salt,
                    iterations = PBKDF2_ITERATIONS
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifies a PIN against stored hash
     */
    fun verifyPIN(pin: SecurePIN): Result<Boolean> {
        return try {
            val stored = storage.load()
                ?: return Result.success(false)

            val computedHash = pin.hash(stored.salt, stored.iterations)
            val matches = SecurePIN.constantTimeEquals(computedHash, stored.hash)

            Result.success(matches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Checks if a PIN is currently stored
     */
    fun hasPIN(): Boolean {
        return storage.load() != null
    }

    /**
     * Clears stored PIN
     */
    fun clearPIN() {
        storage.clear()
    }
}

/**
 * Stored PIN data
 */
data class StoredPIN(
    val hash: ByteArray,
    val salt: ByteArray,
    val iterations: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StoredPIN

        if (!hash.contentEquals(other.hash)) return false
        if (!salt.contentEquals(other.salt)) return false
        if (iterations != other.iterations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hash.contentHashCode()
        result = 31 * result + salt.contentHashCode()
        result = 31 * result + iterations
        return result
    }
}

/**
 * Storage interface for PIN data
 */
interface PINRepositoryStorage {
    fun save(pin: StoredPIN)
    fun load(): StoredPIN?
    fun clear()
}
