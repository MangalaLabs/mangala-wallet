package com.mangala.wallet.cryptography

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import platform.Security.SecRandomCopyBytes
import platform.Security.errSecSuccess
import platform.Security.kSecRandomDefault
import platform.posix.errno
import platform.posix.strerror

@OptIn(ExperimentalForeignApi::class)
actual fun generateSecureRandomBytes(bytesLength: Int): ByteArray {
    val data = ByteArray(bytesLength)
    data.usePinned {
        val result = SecRandomCopyBytes(kSecRandomDefault, bytesLength.toUInt().convert(), it.addressOf(0))
        if (result != errSecSuccess) {
            val errorMessage = strerror(errno)?.toKString() ?: "Unknown error"
            throw RuntimeException("Failed to generate random bytes: $errorMessage")
        }
    }
    return data
}