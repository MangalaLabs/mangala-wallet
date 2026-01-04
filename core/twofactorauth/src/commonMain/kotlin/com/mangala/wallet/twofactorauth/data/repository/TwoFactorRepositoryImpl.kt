package com.mangala.wallet.twofactorauth.data.repository

import androidx.compose.ui.text.TextGranularity.Companion.Character
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.twofactorauth.data.BackupManager
import com.mangala.wallet.twofactorauth.data.EncryptionUtils
import com.mangala.wallet.twofactorauth.data.RateLimiter
import com.mangala.wallet.twofactorauth.data.local.TotpGeneratorDataSource
import com.mangala.wallet.twofactorauth.data.model.AuthResult
import com.mangala.wallet.twofactorauth.data.model.TotpSetupResult
import com.mangala.wallet.twofactorauth.data.model.Transaction
import com.mangala.wallet.twofactorauth.domain.exception.RateLimitException
import com.mangala.wallet.twofactorauth.domain.repository.TwoFactorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Implementation of the TwoFactorRepository
 */
class TwoFactorRepositoryImpl(
    private val secureStorage: SecureStorageWrapper,
    private val totpGenerator: TotpGeneratorDataSource,
    private val backupManager: BackupManager,
    private val rateLimiter: RateLimiter,
    private val encryptionUtils: EncryptionUtils = EncryptionUtils
) : TwoFactorRepository {

    companion object {
        private const val KEY_TOTP_SECRET = "two_factor_totp_secret"
        private const val KEY_BACKUP_CODES = "two_factor_backup_codes"
        private const val KEY_USED_CODES = "two_factor_used_codes"
        private const val TOTP_SECRET_SIZE = 20 // 160 bits
    }

    // StateFlow for tracking authentication state
    private val _authStateFlow = MutableStateFlow(false)

    init {
        // Initialize auth state based on whether 2FA is set up
        _authStateFlow.value = secureStorage.containsKey(KEY_TOTP_SECRET)
    }

    override suspend fun setup2FA(walletAddress: String): TotpSetupResult = withContext(Dispatchers.Default) {
        // Generate a new TOTP secret
        val totpSecret = encryptionUtils.generateSecureRandomBytes(TOTP_SECRET_SIZE)

        // Generate backup codes
        val backupCodes = backupManager.generateBackupCodes()
        val backupCodesString = backupCodes.joinToString(",")

        // Store in secure storage
        secureStorage.saveValue(KEY_TOTP_SECRET, totpSecret.toHexString())
        secureStorage.saveValue(KEY_BACKUP_CODES, backupCodesString)
        secureStorage.saveValue(KEY_USED_CODES, "")

        // Generate QR code URI
        val qrCodeUri = encryptionUtils.buildOtpAuthUri(
            secret = totpSecret,
            label = walletAddress,
            issuer = "BlockchainWallet"
        )
        println("qrCodeUri: $qrCodeUri")

        // Update auth state
        _authStateFlow.value = true

        return@withContext TotpSetupResult(
            secret = EncryptionUtils.Base32Encoder.encode(totpSecret),
            qrCodeUri = qrCodeUri,
            backupCodes = backupCodes
        )
    }

    override suspend fun verifyCode(code: String): Boolean = withContext(Dispatchers.Default) {
        try {
            println("verifying 2fa code: $code")
            // Check rate limiting
            rateLimiter.checkRateLimiting()

            // Get TOTP secret
            val totpSecretHex = secureStorage.getValue(KEY_TOTP_SECRET) ?: return@withContext false
            println("totpSecretHex: $totpSecretHex")

            if (totpSecretHex.isEmpty()) {
                println("Error: TOTP secret is null or empty")
                return@withContext false
            }

            val totpSecret = try {
                totpSecretHex.hexToByteArray()
            } catch (e: Exception) {
                println("Error converting hex to bytes: ${e.message}")
                println("Hex string length: ${totpSecretHex.length}")
                return@withContext false
            }
            println("== pass through hex conversion ==")

            // Verify the code
            val isValid = totpGenerator.validateTOTP(totpSecret, code)

            if (isValid) {
                // Reset failed attempts on success
                rateLimiter.resetAttempts()

                // Store used code to prevent replay attacks
                addUsedCode(code)
            } else {
                // Increment failed attempts
                rateLimiter.incrementFailedAttempts()
            }

            return@withContext isValid
        } catch (e: RateLimitException) {
            throw e
        } catch (e: Exception) {
            rateLimiter.incrementFailedAttempts()
            println("exception: ${e.message}")
            return@withContext false
        }
    }

    override suspend fun disable2FA(confirmationCode: String): Boolean = withContext(Dispatchers.Default) {
        // First verify the code
        if (!verifyCode(confirmationCode)) {
            return@withContext false
        }

        // Remove 2FA data
        secureStorage.remove(KEY_TOTP_SECRET)
        secureStorage.remove(KEY_BACKUP_CODES)
        secureStorage.remove(KEY_USED_CODES)

        // Update auth state
        _authStateFlow.value = false

        return@withContext true
    }

    override suspend fun authenticateTransaction(transaction: Transaction, code: String): AuthResult =
        withContext(Dispatchers.Default) {
            // Check if 2FA is enabled
            if (!is2FAEnabled()) {
                return@withContext AuthResult.NOT_REQUIRED
            }

            try {
                // Verify code
                val isValid = verifyCode(code)

                return@withContext if (isValid) {
                    AuthResult.SUCCESS
                } else {
                    AuthResult.FAILED
                }
            } catch (e: RateLimitException) {
                return@withContext AuthResult.RATE_LIMITED
            } catch (e: Exception) {
                return@withContext AuthResult.FAILED
            }
        }

    override suspend fun is2FAEnabled(): Boolean = withContext(Dispatchers.Default) {
        secureStorage.containsKey(KEY_TOTP_SECRET)
    }

    override suspend fun exportBackup(password: String): ByteArray = withContext(Dispatchers.Default) {
        // Get TOTP secret
        val totpSecretHex = secureStorage.getValue(KEY_TOTP_SECRET)
            ?: throw IllegalStateException("2FA is not set up")
        val totpSecret = totpSecretHex.hexToByteArray()

        // Get backup codes if available
        val backupCodesString = secureStorage.getValue(KEY_BACKUP_CODES)
        val backupCodes = backupCodesString?.encodeToByteArray()

        // Create and export backup
        return@withContext backupManager.exportBackup(totpSecret, backupCodes, password)
    }

    override suspend fun importBackup(data: ByteArray, password: String): Boolean =
        withContext(Dispatchers.Default) {
            try {
                // Import backup
                val backupData = backupManager.importBackup(data, password)

                // Store data
                secureStorage.saveValue(KEY_TOTP_SECRET, backupData.totpSecret.toHexString())
                backupData.backupCodes?.let {
                    secureStorage.saveValue(KEY_BACKUP_CODES, it.decodeToString())
                }
                secureStorage.saveValue(KEY_USED_CODES, "")

                // Update auth state
                _authStateFlow.value = true

                return@withContext true
            } catch (e: Exception) {
                return@withContext false
            }
        }

    override suspend fun verifyBackupCode(code: String): Boolean = withContext(Dispatchers.Default) {
        try {
            // Check rate limiting
            rateLimiter.checkRateLimiting()

            // Get backup codes
            val backupCodesString = secureStorage.getValue(KEY_BACKUP_CODES) ?: return@withContext false
            val backupCodes = backupCodesString.split(",")

            // Get used codes
            val usedCodesString = secureStorage.getValue(KEY_USED_CODES) ?: ""
            val usedCodes = usedCodesString.split(",").filter { it.isNotEmpty() }

            // Normalize input code
            val normalizedCode = code.replace(Regex("[^0-9]"), "")

            // Check if code is valid and not used
            for (backupCode in backupCodes) {
                val normalizedBackupCode = backupCode.replace(Regex("[^0-9]"), "")

                if (normalizedCode == normalizedBackupCode && !usedCodes.contains(normalizedCode)) {
                    // Mark code as used
                    val newUsedCodes = usedCodes.toMutableList()
                    newUsedCodes.add(normalizedCode)
                    secureStorage.saveValue(KEY_USED_CODES, newUsedCodes.joinToString(","))

                    // Reset failed attempts
                    rateLimiter.resetAttempts()

                    return@withContext true
                }
            }

            // Increment failed attempts
            rateLimiter.incrementFailedAttempts()
            return@withContext false
        } catch (e: RateLimitException) {
            throw e
        } catch (e: Exception) {
            rateLimiter.incrementFailedAttempts()
            return@withContext false
        }
    }

    override fun getAuthStateFlow(): Flow<Boolean> = _authStateFlow.asStateFlow()

    // Helper methods

    private fun addUsedCode(code: String) {
        // Get currently used codes
        val usedCodesString = secureStorage.getValue(KEY_USED_CODES) ?: ""
        val usedCodes = usedCodesString.split(",").filter { it.isNotEmpty() }.toMutableList()

        // Add the new code
        usedCodes.add(code)

        // Keep only the last 10 used codes to prevent unlimited growth
        val trimmedCodes = if (usedCodes.size > 10) {
            usedCodes.takeLast(10)
        } else {
            usedCodes
        }

        // Save back to storage
        secureStorage.saveValue(KEY_USED_CODES, trimmedCodes.joinToString(","))
    }

    // Extension functions for byte array conversions

    private fun ByteArray.toHexString(): String {
        return joinToString("") {
            val i = it.toInt() and 0xFF
            (i / 16).toString(16) + (i % 16).toString(16)
        }
    }

    private fun String.hexToByteArray(): ByteArray {
        val len = length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((this[i].digitToInt(16) shl 4) +
                    this[i + 1].digitToInt(16)).toByte()
            i += 2
        }
        return data
    }
}