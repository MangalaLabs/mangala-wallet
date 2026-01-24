package com.mangala.wallet.pin.data

import android.content.Context
import android.provider.Settings

actual class DeviceIdProvider(private val context: Context) {
    actual suspend fun getDeviceId(): String {
        return try {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "unknown_android"
        } catch (e: Exception) {
            "unknown_android"
        }
    }
}
