package com.mangala.wallet.cryptography

import java.security.SecureRandom

actual fun generateSecureRandomBytes(bytesLength: Int): ByteArray {
    val random = SecureRandom()
    val data = ByteArray(bytesLength)
    random.nextBytes(data)
    return data
}