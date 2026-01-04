package com.mangala.wallet.twofactorauth.data


interface EncryptionUtilsInterface {
    suspend fun generateSecureRandomBytes(length: Int): ByteArray
    suspend fun encryptWithAesGcm(data: ByteArray, key: ByteArray, iv: ByteArray? = null): ByteArray
    suspend fun decryptWithAesGcm(encryptedData: ByteArray, key: ByteArray): ByteArray
    suspend fun deriveKeyFromPassword(password: String, salt: ByteArray, iterations: Int = 100000): ByteArray
    suspend fun hmacSha1(key: ByteArray, data: ByteArray): ByteArray
}

/**
 * Utilities for encryption and cryptographic operations with common functionality
 */
object EncryptionUtils {
    private const val AES_KEY_SIZE = 256
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 16 * 8 // 16 bytes in bits

    /**
     * Generate cryptographically secure random bytes
     */
    suspend fun generateSecureRandomBytes(length: Int): ByteArray {
        return PlatformEncryptionUtils.generateSecureRandomBytes(length)
    }

    /**
     * Encrypt data using AES-GCM
     */
    suspend fun encryptWithAesGcm(data: ByteArray, key: ByteArray, iv: ByteArray? = null): ByteArray {
        val actualIv = iv ?: generateSecureRandomBytes(GCM_IV_LENGTH)
        return PlatformEncryptionUtils.encryptWithAesGcm(data, key, actualIv)
    }

    /**
     * Decrypt data using AES-GCM
     */
    suspend fun decryptWithAesGcm(encryptedData: ByteArray, key: ByteArray): ByteArray {
        return PlatformEncryptionUtils.decryptWithAesGcm(encryptedData, key)
    }

    /**
     * Derive key from password
     */
    suspend fun deriveKeyFromPassword(password: String, salt: ByteArray, iterations: Int = 100000): ByteArray {
        return PlatformEncryptionUtils.deriveKeyFromPassword(password, salt, iterations)
    }

    /**
     * Compute HMAC-SHA1
     */
    suspend fun hmacSha1(key: ByteArray, data: ByteArray): ByteArray {
        println("hmacSha1 called with key length: ${key.size}, data length: ${data.size}")
        try {
            // Implementation details...
            // Return hash bytes
            return PlatformEncryptionUtils.hmacSha1(key, data)
        } catch (e: Exception) {
            println("Error in hmacSha1: ${e.message}")
            println("Stack trace: ${e.stackTraceToString()}")
            throw e
        }
    }

    /**
     * Build the OTP authentication URI for QR code generation
     */
    fun buildOtpAuthUri(secret: ByteArray, label: String, issuer: String = "YourWalletApp"): String {
        val base32Secret = Base32Encoder.encode(secret)
        return "otpauth://totp/$issuer:$label?secret=$base32Secret&issuer=$issuer&algorithm=SHA1&digits=6&period=30"
    }

    /**
     * Convert a long to a byte array
     */
    fun longToByteArray(value: Long): ByteArray {
//        return ByteArray(8).apply {
//            var remaining = value
//            for (i in 7 downTo 0) {
//                this[i] = (remaining and 0xFF).toByte()
//                remaining = remaining shr 8
//            }
//        }
        val result = ByteArray(8)
        for (i in 7 downTo 0) {
            result[7 - i] = (value shr (i * 8)).toByte()
        }
        return result
    }

    /**
     * Simple Base32 encoder for TOTP secrets
     */
    object Base32Encoder {
        private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

        fun encode(data: ByteArray): String {
            val result = StringBuilder()
            var bits = 0
            var value = 0

            for (b in data) {
                value = (value shl 8) or (b.toInt() and 0xFF)
                bits += 8

                while (bits >= 5) {
                    bits -= 5
                    result.append(ALPHABET[(value shr bits) and 0x1F])
                }
            }

            if (bits > 0) {
                result.append(ALPHABET[(value shl (5 - bits)) and 0x1F])
            }

            // Add padding if needed
            while (result.length % 8 != 0) {
                result.append('=')
            }

            return result.toString()
        }
    }
}