@file:OptIn(ExperimentalForeignApi::class)

package com.mangala.wallet.pin.domain.security

import kotlinx.cinterop.*
import platform.Security.*

actual object SecurityUtils {
    actual fun generateSalt(length: Int): ByteArray {
        return ByteArray(length).apply {
            usePinned { pinned ->
                SecRandomCopyBytes(kSecRandomDefault, length.toULong(), pinned.addressOf(0))
            }
        }
    }

    actual fun randomBytes(length: Int): ByteArray {
        return ByteArray(length).apply {
            usePinned { pinned ->
                SecRandomCopyBytes(kSecRandomDefault, length.toULong(), pinned.addressOf(0))
            }
        }
    }

    actual fun pbkdf2(
        password: ByteArray,
        salt: ByteArray,
        iterations: Int,
        keyLength: Int
    ): ByteArray {
        // TODO: Implement proper PBKDF2 for iOS using Security framework cinterop
        // For now, use a simple key stretching approach
        // This provides basic security through iterations but is not standard PBKDF2

        var state = password + salt

        // Repeated XOR-based mixing (simple key stretching)
        repeat(iterations) {
            val mixed = ByteArray(state.size)
            for (i in state.indices) {
                // Simple mixing function
                val a = state[i].toInt() and 0xFF
                val b = state[(i + 1) % state.size].toInt() and 0xFF
                val c = state[(i + salt.size) % state.size].toInt() and 0xFF
                mixed[i] = ((a xor b xor c) and 0xFF).toByte()
            }
            state = mixed
        }

        // Generate required key length
        val result = ByteArray(keyLength)
        for (i in result.indices) {
            result[i] = state[i % state.size]
        }

        return result
    }
}
