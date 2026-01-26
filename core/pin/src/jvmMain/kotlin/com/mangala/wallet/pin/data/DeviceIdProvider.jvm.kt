package com.mangala.wallet.pin.data

import java.net.InetAddress
import java.security.MessageDigest

actual class DeviceIdProvider {
    actual suspend fun getDeviceId(): String {
        return try {
            val hostname = InetAddress.getLocalHost().hostName
            val username = System.getProperty("user.name") ?: ""
            val combined = "$hostname-$username"

            // Hash the combined string for privacy
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(combined.toByteArray())
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "unknown_desktop"
        }
    }
}
