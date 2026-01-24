package com.mangala.wallet.pin.data.storage

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.pin.domain.attempt.AttemptData
import com.mangala.wallet.pin.domain.attempt.AttemptStorage
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Implementation of AttemptStorage using SecureStorageWrapper
 */
class AttemptStorageImpl(
    private val secureStorage: SecureStorageWrapper,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : AttemptStorage {

    companion object {
        private const val KEY_ATTEMPT_DATA = "pin_attempt_data_v2"
    }

    override fun save(data: AttemptData) {
        val serializable = AttemptDataSerializable(
            failedAttempts = data.failedAttempts,
            lastAttemptTimeMs = data.lastAttemptTime.toEpochMilliseconds(),
            isLocked = data.isLocked,
            lockoutUntilMs = data.lockoutUntil.toEpochMilliseconds(),
            deviceId = data.deviceId,
            integrityHash = data.integrityHash
        )
        val jsonString = json.encodeToString(serializable)
        secureStorage.saveValue(KEY_ATTEMPT_DATA, jsonString)
    }

    override fun load(): AttemptData? {
        val jsonString = secureStorage.getValue(KEY_ATTEMPT_DATA) ?: return null

        return try {
            val serializable = json.decodeFromString<AttemptDataSerializable>(jsonString)
            AttemptData(
                failedAttempts = serializable.failedAttempts,
                lastAttemptTime = Instant.fromEpochMilliseconds(serializable.lastAttemptTimeMs),
                isLocked = serializable.isLocked,
                lockoutUntil = Instant.fromEpochMilliseconds(serializable.lockoutUntilMs),
                deviceId = serializable.deviceId,
                integrityHash = serializable.integrityHash
            )
        } catch (e: Exception) {
            null
        }
    }

    override fun clear() {
        secureStorage.remove(KEY_ATTEMPT_DATA)
    }

    @Serializable
    private data class AttemptDataSerializable(
        val failedAttempts: Int,
        val lastAttemptTimeMs: Long,
        val isLocked: Boolean,
        val lockoutUntilMs: Long,
        val deviceId: String,
        val integrityHash: String
    )
}
