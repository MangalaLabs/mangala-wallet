package com.mangala.wallet.twofactorauth.data

import com.mangala.wallet.twofactorauth.data.model.BackupData
import com.mangala.wallet.twofactorauth.domain.exception.BackupCorruptedException
import com.mangala.wallet.utils.ByteBuffer
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Manager for backup and restore operations of 2FA data
 */
class BackupManager {
    companion object {
        private const val CURRENT_VERSION = 1
        private const val SALT_SIZE = 16
        private const val BACKUP_CODE_LENGTH = 8
        private const val NUM_BACKUP_CODES = 5
    }

    /**
     * Export 2FA data as an encrypted backup
     */
    suspend fun exportBackup(totpSecret: ByteArray, backupCodes: ByteArray?, password: String): ByteArray =
        withContext(Dispatchers.Default) {
            // Create backup data
            val backupData = BackupData(
                version = CURRENT_VERSION,
                totpSecret = totpSecret,
                backupCodes = backupCodes,
                timestamp = localDateTimeToMillis(localDateTimeNow())
            )

            // Serialize backup data
            val serializedData = serializeBackupData(backupData)

            // Generate salt for key derivation
            val salt = EncryptionUtils.generateSecureRandomBytes(SALT_SIZE)

            // Derive encryption key from password
            val encryptionKey = EncryptionUtils.deriveKeyFromPassword(password, salt)

            // Encrypt the serialized data
            val encryptedData = EncryptionUtils.encryptWithAesGcm(serializedData, encryptionKey)

            // Combine salt and encrypted data
            return@withContext combineBackupComponents(salt, encryptedData)
        }

    /**
     * Import 2FA data from an encrypted backup
     */
    suspend fun importBackup(data: ByteArray, password: String): BackupData =
        withContext(Dispatchers.Default) {
            try {
                // Split components
                val components = extractBackupComponents(data)
                val salt = components.first
                val encryptedData = components.second

                // Derive decryption key
                val decryptionKey = EncryptionUtils.deriveKeyFromPassword(password, salt)

                // Decrypt data
                val decryptedData = EncryptionUtils.decryptWithAesGcm(encryptedData, decryptionKey)

                // Deserialize backup data
                val backupData = deserializeBackupData(decryptedData)

                // Validate version
                if (backupData.version > CURRENT_VERSION) {
                    throw BackupCorruptedException()
                }

                return@withContext backupData
            } catch (e: Exception) {
                throw BackupCorruptedException()
            }
        }

    /**
     * Generate backup codes for recovery
     */
    fun generateBackupCodes(count: Int = NUM_BACKUP_CODES): List<String> {
        val codes = mutableListOf<String>()
        repeat(count) {
            // Generate random code
            val codeValue = Random.nextInt(10000000, 99999999)

            // Format as 8-digit code with dashes
            val codeString = codeValue.toString()
                .takeLast(BACKUP_CODE_LENGTH)
                .padStart(BACKUP_CODE_LENGTH, '0')
                .chunked(4)
                .joinToString("-")

            codes.add(codeString)
        }
        return codes
    }

    /**
     * Validate a backup code against stored codes
     */
    fun validateBackupCode(inputCode: String, storedCodes: List<String>): Boolean {
        // Normalize input (remove dashes, spaces, etc.)
        val normalizedInput = inputCode.replace(Regex("[^0-9]"), "")

        // Check against each stored code
        return storedCodes.any {
            val normalizedStored = it.replace(Regex("[^0-9]"), "")
            normalizedInput == normalizedStored
        }
    }

    // Private utility functions

    private fun serializeBackupData(backupData: BackupData): ByteArray {
        val buffer = ByteBuffer.allocate(
            4 + // version (Int)
                    4 + // totpSecret length (Int)
                    backupData.totpSecret.size +
                    4 + // backupCodes length (Int), can be 0
                    (backupData.backupCodes?.size ?: 0) +
                    8 // timestamp (Long)
        )

        buffer.putInt(backupData.version)

        // Write totpSecret
        buffer.putInt(backupData.totpSecret.size)
        buffer.put(backupData.totpSecret)

        // Write backupCodes if present
        if (backupData.backupCodes != null) {
            buffer.putInt(backupData.backupCodes.size)
            buffer.put(backupData.backupCodes)
        } else {
            buffer.putInt(0)
        }

        buffer.putLong(backupData.timestamp)

        return buffer.array()
    }

    private fun deserializeBackupData(data: ByteArray): BackupData {
        val buffer = ByteBuffer.wrap(data)

        val version = buffer.getInt()

        // Read totpSecret
        val secretLength = buffer.getInt()
        val totpSecret = ByteArray(secretLength)
        buffer.get(totpSecret)

        // Read backupCodes if present
        val backupCodesLength = buffer.getInt()
        val backupCodes = if (backupCodesLength > 0) {
            ByteArray(backupCodesLength).also { buffer.get(it) }
        } else {
            null
        }

        val timestamp = buffer.getLong()

        return BackupData(
            version = version,
            totpSecret = totpSecret,
            backupCodes = backupCodes,
            timestamp = timestamp
        )
    }

    private fun combineBackupComponents(salt: ByteArray, encryptedData: ByteArray): ByteArray {
        val result = ByteArray(salt.size + encryptedData.size)
        salt.copyInto(result, 0)
        encryptedData.copyInto(result, salt.size)
        return result
    }

    private fun extractBackupComponents(combinedData: ByteArray): Pair<ByteArray, ByteArray> {
        if (combinedData.size <= SALT_SIZE) {
            throw BackupCorruptedException()
        }

        val salt = combinedData.copyOfRange(0, SALT_SIZE)
        val encryptedData = combinedData.copyOfRange(SALT_SIZE, combinedData.size)
        return Pair(salt, encryptedData)
    }
}