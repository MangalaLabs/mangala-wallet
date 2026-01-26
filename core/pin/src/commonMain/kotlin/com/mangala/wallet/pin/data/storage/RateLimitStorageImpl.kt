package com.mangala.wallet.pin.data.storage

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.pin.domain.ratelimit.RateLimitData
import com.mangala.wallet.pin.domain.ratelimit.RateLimitStorage
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Implementation of RateLimitStorage using SecureStorageWrapper
 */
class RateLimitStorageImpl(
    private val secureStorage: SecureStorageWrapper,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : RateLimitStorage {

    companion object {
        private const val KEY_RATE_LIMIT = "pin_rate_limit_v2"
    }

    override fun save(data: RateLimitData) {
        val serializable = RateLimitDataSerializable(
            attemptCount = data.attemptCount,
            lastAttemptMs = data.lastAttempt.toEpochMilliseconds(),
            cooldownUntilMs = data.cooldownUntil.toEpochMilliseconds()
        )
        val jsonString = json.encodeToString(serializable)
        secureStorage.saveValue(KEY_RATE_LIMIT, jsonString)
    }

    override fun load(): RateLimitData? {
        val jsonString = secureStorage.getValue(KEY_RATE_LIMIT) ?: return null

        return try {
            val serializable = json.decodeFromString<RateLimitDataSerializable>(jsonString)
            RateLimitData(
                attemptCount = serializable.attemptCount,
                lastAttempt = Instant.fromEpochMilliseconds(serializable.lastAttemptMs),
                cooldownUntil = Instant.fromEpochMilliseconds(serializable.cooldownUntilMs)
            )
        } catch (e: Exception) {
            null
        }
    }

    override fun clear() {
        secureStorage.remove(KEY_RATE_LIMIT)
    }

    @Serializable
    private data class RateLimitDataSerializable(
        val attemptCount: Int,
        val lastAttemptMs: Long,
        val cooldownUntilMs: Long
    )
}
