package com.mangala.wallet.features.addressbook.data.model.enum

/**
 * Hằng số và giá trị cấu hình cho chức năng mã hóa.
 */
object CryptoConstants {
    // Key alias
    const val DEFAULT_ENCRYPTION_KEY_ALIAS = "address_book_master_key"

    // Thuật toán mã hóa
    const val AES_ALGORITHM = "AES"
    const val AES_TRANSFORMATION = "AES/GCM/NoPadding"

    // Kích thước khóa và tham số
    const val KEY_SIZE = 256
    const val GCM_IV_LENGTH = 12
    const val GCM_TAG_LENGTH = 16

    // Cache settings
    const val KEY_CACHE_DURATION_MS = 5 * 60 * 1000L // 5 phút

    // Error messages
    const val ERROR_KEY_NOT_FOUND = "Encryption key not found"
    const val ERROR_KEY_GENERATION = "Failed to generate encryption key"
    const val ERROR_KEYSTORE_UNAVAILABLE = "Secure keystore unavailable"
    const val ERROR_DECRYPTION = "Failed to decrypt data"
    const val ERROR_ENCRYPTION = "Failed to encrypt data"

    // Security settings
    const val AUTH_VALIDITY_DURATION_MS = 30 * 60 * 1000L // 30 phút
    const val MAX_FAILED_AUTH_ATTEMPTS = 5

    // Database encryption
    const val ENCRYPTED_DB_NAME = "address_book_encrypted.db"

    // Các kiểu mã hóa được hỗ trợ
    enum class EncryptionType {
        AES_256_GCM,      // Mặc định, mã hóa AES-256 GCM mode
        CHACHA20_POLY1305 // ChaCha20-Poly1305, thay thế cho iOS và các thiết bị không hỗ trợ tốt AES
    }
}