package com.mangala.wallet.utils.device

import com.benasher44.uuid.uuid4

actual suspend fun getDeviceId(): String {
    return try {
        // For desktop/JVM, we'll generate a random UUID as device ID
        uuid4().toString()
    } catch (e: Exception) {
        "unknown_desktop"
    }
}