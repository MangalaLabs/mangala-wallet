package com.mangala.wallet.pin.domain.security

/**
 * Platform-specific security utilities
 */
expect object SecurityUtils {
    /**
     * Generates cryptographically secure random bytes
     */
    fun generateSalt(length: Int = 32): ByteArray

    /**
     * Generates secure random bytes
     */
    fun randomBytes(length: Int): ByteArray

    /**
     * PBKDF2-SHA256 key derivation
     */
    fun pbkdf2(
        password: ByteArray,
        salt: ByteArray,
        iterations: Int,
        keyLength: Int
    ): ByteArray
}

/**
 * Common security utilities
 */
object CommonSecurityUtils {
    /**
     * Clears a CharArray from memory
     */
    fun clearCharArray(array: CharArray) {
        array.fill('0')
    }

    /**
     * Clears a ByteArray from memory
     */
    fun clearByteArray(array: ByteArray) {
        array.fill(0)
    }
}
