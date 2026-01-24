package com.mangala.wallet.pin.data

/**
 * Provides unique device identifier for device binding
 */
expect class DeviceIdProvider {
    suspend fun getDeviceId(): String
}
