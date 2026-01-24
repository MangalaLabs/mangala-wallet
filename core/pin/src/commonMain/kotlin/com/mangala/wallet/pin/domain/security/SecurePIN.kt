package com.mangala.wallet.pin.domain.security

/**
 * Secure PIN representation using CharArray instead of String
 * to allow memory clearing after use.
 */
class SecurePIN(private val pin: CharArray) : AutoCloseable {

    init {
        require(pin.size == 6) { "PIN must be exactly 6 digits" }
        require(pin.all { it.isDigit() }) { "PIN must contain only digits" }
    }

    /**
     * Validates if the provided PIN matches this PIN using constant-time comparison
     */
    fun matches(other: CharArray): Boolean {
        if (pin.size != other.size) return false

        var result = 0
        for (i in pin.indices) {
            result = result or (pin[i].code xor other[i].code)
        }
        return result == 0
    }

    /**
     * Hashes the PIN using PBKDF2-SHA256
     */
    fun hash(salt: ByteArray, iterations: Int = 100_000): ByteArray {
        val pinBytes = pin.map { it.code.toByte() }.toByteArray()

        try {
            return SecurityUtils.pbkdf2(
                password = pinBytes,
                salt = salt,
                iterations = iterations,
                keyLength = 32
            )
        } finally {
            // Clear PIN bytes from memory
            pinBytes.fill(0)
        }
    }

    /**
     * Returns a copy of the PIN for temporary use
     * Caller is responsible for clearing the returned array
     */
    fun toCharArray(): CharArray = pin.copyOf()

    override fun close() {
        // Zero out the PIN in memory
        pin.fill('0')
    }

    companion object {
        /**
         * Constant-time comparison of two byte arrays
         */
        fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
            if (a.size != b.size) return false

            var result = 0
            for (i in a.indices) {
                result = result or (a[i].toInt() xor b[i].toInt())
            }
            return result == 0
        }
    }
}

/**
 * Exception thrown when PINs don't match
 */
class PINMismatchException : Exception("PINs do not match")

/**
 * Exception thrown when PIN length is invalid
 */
class InvalidPINLengthException : Exception("PIN must be exactly 6 digits")

/**
 * Exception thrown when PIN contains invalid characters
 */
class InvalidPINFormatException : Exception("PIN must contain only digits")
