package com.mangala.wallet.auth.storage

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.passkey.util.PasskeyLogger
import com.mangala.wallet.utils.device.getDeviceId
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Manages storage of successfully registered passkey credentials.
 * This helps filter the passkey selector to only show passkeys that have been
 * successfully registered with the backend, hiding orphaned passkeys.
 */
class CompletedPasskeyStorage(
    private val secureStorage: SecureStorageWrapper
) {
    private val STORAGE_KEY = "completed_passkey_ids"
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Save a successfully registered passkey credential
     */
    suspend fun saveCompletedPasskey(
        credentialId: String,
        userId: String,
        deviceName: String = "Device"
    ) {
        try {
            Napier.e("🔵 STORAGE DEBUG: Starting to save passkey")
            Napier.e("🔵 STORAGE DEBUG: credentialId=$credentialId, userId=$userId")

            val existing = getCompletedPasskeys()
            Napier.e("🔵 STORAGE DEBUG: Found ${existing.size} existing passkeys")

            // Check if already exists
            if (existing.any { it.credentialId == credentialId }) {
                Napier.e("🔵 STORAGE DEBUG: Passkey already exists, skipping: $credentialId")
                return
            }

            val new = CompletedPasskey(
                credentialId = credentialId,
                userId = userId,
                deviceId = try { getDeviceId() } catch (e: Exception) { "unknown" },
                deviceName = deviceName,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )

            val updated = existing + new
            val jsonString = json.encodeToString(updated)

            Napier.e("🔵 STORAGE DEBUG: About to save JSON: $jsonString")
            secureStorage.saveValue(STORAGE_KEY, jsonString)
            Napier.e("🔵 STORAGE DEBUG: Storage.saveValue() completed")

            // Verify save by reading it back immediately
            val verification = secureStorage.getValue(STORAGE_KEY)
            Napier.e("🔵 STORAGE DEBUG: Immediate read-back verification: ${verification?.length ?: 0} chars")

            if (verification != null) {
                val verifiedPasskeys = json.decodeFromString<List<CompletedPasskey>>(verification)
                Napier.e("🔵 STORAGE DEBUG: ✅ VERIFIED: ${verifiedPasskeys.size} passkeys persisted")
                verifiedPasskeys.forEach { p ->
                    Napier.e("🔵 STORAGE DEBUG:   - userId=${p.userId}, credId=${p.credentialId.take(20)}...")
                }
            } else {
                Napier.e("🔵 STORAGE DEBUG: ❌ FAILED: Immediate read-back returned null!")
            }

        } catch (e: Exception) {
            Napier.e("❌ CRITICAL: Failed to save completed passkey: ${e.message}", e)
            e.printStackTrace()
        }
    }

    /**
     * Get all completed passkeys stored locally
     */
    suspend fun getCompletedPasskeys(): List<CompletedPasskey> {
        return try {
            Napier.e("🔵 STORAGE DEBUG: Reading completed passkeys from storage")
            val jsonString = secureStorage.getValue(STORAGE_KEY)
            Napier.e("🔵 STORAGE DEBUG: Raw JSON from storage: ${jsonString?.length ?: 0} chars")

            if (jsonString.isNullOrEmpty()) {
                Napier.e("🔵 STORAGE DEBUG: ❌ Storage returned null/empty - no passkeys found")
                return emptyList()
            }

            val passkeys = json.decodeFromString<List<CompletedPasskey>>(jsonString)
            Napier.e("🔵 STORAGE DEBUG: ✅ Loaded ${passkeys.size} completed passkeys from storage")
            passkeys.forEach { p ->
                Napier.e("🔵 STORAGE DEBUG:   - userId=${p.userId}, credId=${p.credentialId.take(20)}...")
            }
            passkeys
        } catch (e: Exception) {
            Napier.e("❌ CRITICAL: Failed to load completed passkeys: ${e.message}", e)
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Get credential IDs as ByteArray for WebAuthn allowCredentials
     */
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getCredentialIdsForUser(userId: String?): List<ByteArray> {
        return try {
            val passkeys = getCompletedPasskeys()
                .filter { userId == null || it.userId == userId }

            PasskeyLogger.d("Found ${passkeys.size} passkeys for user: $userId")

            passkeys.map { passkey ->
                try {
                    // The credential ID might be base64url encoded
                    val normalized = passkey.credentialId
                        .replace('-', '+')
                        .replace('_', '/')

                    // Add padding if needed
                    val padded = when (normalized.length % 4) {
                        2 -> normalized + "=="
                        3 -> normalized + "="
                        else -> normalized
                    }

                    Base64.decode(padded)
                } catch (e: Exception) {
                    // If decoding fails, use the string as bytes
                    PasskeyLogger.e("Failed to decode credential ID: ${passkey.credentialId}", e)
                    passkey.credentialId.encodeToByteArray()
                }
            }
        } catch (e: Exception) {
            Napier.e("Failed to get credential IDs for user", e)
            emptyList()
        }
    }

    /**
     * Remove a passkey from the completed list
     */
    suspend fun removePasskey(credentialId: String) {
        try {
            PasskeyLogger.d("Removing passkey: $credentialId")

            val existing = getCompletedPasskeys()
            val filtered = existing.filter { it.credentialId != credentialId }

            if (filtered.size < existing.size) {
                val jsonString = json.encodeToString(filtered)
                secureStorage.saveValue(STORAGE_KEY, jsonString)
                PasskeyLogger.d("Passkey removed. Remaining: ${filtered.size}")
            } else {
                PasskeyLogger.d("Passkey not found: $credentialId")
            }
        } catch (e: Exception) {
            Napier.e("Failed to remove passkey", e)
        }
    }

    /**
     * Clear all completed passkeys (useful for logout)
     */
    suspend fun clearAll() {
        try {
            Napier.e("🔵 STORAGE DEBUG: Clearing all completed passkeys")
            secureStorage.remove(STORAGE_KEY)

            // Verify clearing worked
            val verification = secureStorage.getValue(STORAGE_KEY)
            if (verification == null) {
                Napier.e("🔵 STORAGE DEBUG: ✅ All completed passkeys cleared successfully")
            } else {
                Napier.e("🔵 STORAGE DEBUG: ❌ Failed to clear - still contains: ${verification.length} chars")
            }
        } catch (e: Exception) {
            Napier.e("❌ CRITICAL: Failed to clear completed passkeys: ${e.message}", e)
            e.printStackTrace()
        }
    }

    /**
     * Remove passkeys for a specific user
     */
    suspend fun clearUserPasskeys(userId: String) {
        try {
            PasskeyLogger.d("Clearing passkeys for user: $userId")

            val existing = getCompletedPasskeys()
            val filtered = existing.filter { it.userId != userId }

            if (filtered.size < existing.size) {
                val jsonString = json.encodeToString(filtered)
                secureStorage.saveValue(STORAGE_KEY, jsonString)
                PasskeyLogger.d("Cleared ${existing.size - filtered.size} passkeys for user")
            }
        } catch (e: Exception) {
            Napier.e("Failed to clear user passkeys", e)
        }
    }
}

/**
 * Data class representing a successfully registered passkey
 */
@Serializable
data class CompletedPasskey(
    val credentialId: String,
    val userId: String,
    val deviceId: String,
    val deviceName: String,
    val createdAt: Long
)