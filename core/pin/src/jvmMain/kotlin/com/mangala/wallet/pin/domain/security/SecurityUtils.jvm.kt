package com.mangala.wallet.pin.domain.security

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

actual object SecurityUtils {
    private val secureRandom = SecureRandom()

    actual fun generateSalt(length: Int): ByteArray {
        return ByteArray(length).apply {
            secureRandom.nextBytes(this)
        }
    }

    actual fun randomBytes(length: Int): ByteArray {
        return ByteArray(length).apply {
            secureRandom.nextBytes(this)
        }
    }

    actual fun pbkdf2(
        password: ByteArray,
        salt: ByteArray,
        iterations: Int,
        keyLength: Int
    ): ByteArray {
        val passwordChars = password.map { it.toInt().toChar() }.toCharArray()
        try {
            val spec = PBEKeySpec(passwordChars, salt, iterations, keyLength * 8)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            return factory.generateSecret(spec).encoded
        } finally {
            passwordChars.fill('0')
        }
    }
}
