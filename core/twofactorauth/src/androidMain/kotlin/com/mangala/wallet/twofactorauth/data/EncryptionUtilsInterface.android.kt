package com.mangala.wallet.twofactorauth.data

import com.mangala.wallet.utils.ByteBuffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Android-specific implementation of EncryptionUtilsInterface
 */
private object PlatformEncryptionUtilsImpl : EncryptionUtilsInterface {
    // These will be implemented in android-specific source set
    override suspend fun generateSecureRandomBytes(length: Int): ByteArray =
        withContext(Dispatchers.Default) {
            val secureRandom = SecureRandom()
            val bytes = ByteArray(length)
            secureRandom.nextBytes(bytes)
            bytes
        }

    override suspend fun encryptWithAesGcm(data: ByteArray, key: ByteArray, iv: ByteArray?): ByteArray =
        withContext(Dispatchers.Default) {
            try {
                // Create AES-GCM cipher
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")

                // Generate IV if not provided
                val initializationVector = iv ?: ByteArray(12).apply {
                    SecureRandom().nextBytes(this)
                }

                // Create GCM parameter spec with a 128-bit authentication tag length
                val parameterSpec = GCMParameterSpec(128, initializationVector)

                // Create secret key
                val secretKey = SecretKeySpec(key, "AES")

                // Initialize cipher for encryption
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)

                // Encrypt data
                val encryptedData = cipher.doFinal(data)

                // Combine IV and encrypted data into one array using your custom ByteBuffer
                val combinedData = ByteArray(initializationVector.size + encryptedData.size)
                System.arraycopy(initializationVector, 0, combinedData, 0, initializationVector.size)
                System.arraycopy(encryptedData, 0, combinedData, initializationVector.size, encryptedData.size)

                combinedData
            } catch (e: Exception) {
                println("AES-GCM encryption error: ${e.message}")
                throw e
            }
        }

    override suspend fun decryptWithAesGcm(encryptedData: ByteArray, key: ByteArray): ByteArray =
        withContext(Dispatchers.Default) {
            try {
                // Extract IV from the beginning of the encrypted data
                val iv = ByteArray(12)
                val actualEncryptedData = ByteArray(encryptedData.size - 12)

                System.arraycopy(encryptedData, 0, iv, 0, 12)
                System.arraycopy(encryptedData, 12, actualEncryptedData, 0, actualEncryptedData.size)

                // Create AES-GCM cipher
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")

                // Create GCM parameter spec with 128-bit authentication tag length
                val parameterSpec = GCMParameterSpec(128, iv)

                // Create secret key
                val secretKey = SecretKeySpec(key, "AES")

                // Initialize cipher for decryption
                cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)

                // Decrypt data
                cipher.doFinal(actualEncryptedData)
            } catch (e: Exception) {
                println("AES-GCM decryption error: ${e.message}")
                throw e
            }
        }

    override suspend fun deriveKeyFromPassword(password: String, salt: ByteArray, iterations: Int): ByteArray =
        withContext(Dispatchers.Default) {
            try {
                // Create PBKDF2 parameters
                val spec = PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    256 // Key length in bits (AES-256)
                )

                // Create key factory with PBKDF2 algorithm
                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")

                // Generate key
                factory.generateSecret(spec).encoded
            } catch (e: Exception) {
                println("Key derivation error: ${e.message}")
                throw e
            }
        }

    override suspend fun hmacSha1(key: ByteArray, data: ByteArray): ByteArray =
        withContext(Dispatchers.Default) {
            try {
                val mac = Mac.getInstance("HmacSHA1")
                val secretKey = SecretKeySpec(key, "HmacSHA1")
                mac.init(secretKey)
                mac.doFinal(data)
            } catch (e: Exception) {
                println("HMAC-SHA1 error: ${e.message}")
                throw e
            }
        }
}

actual val PlatformEncryptionUtils: EncryptionUtilsInterface = PlatformEncryptionUtilsImpl