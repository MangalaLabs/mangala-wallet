package com.mangala.wallet.pin.data

import platform.UIKit.UIDevice

actual class DeviceIdProvider {
    actual suspend fun getDeviceId(): String {
        return try {
            UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "unknown_ios"
        } catch (e: Exception) {
            "unknown_ios"
        }
    }
}
