package com.mangala.wallet.websocket.chat.di

import com.mangala.wallet.websocket.chat.auth.WalletKeyProvider
import com.mangala.wallet.websocket.chat.device.DeviceStateMonitor
import com.mangala.wallet.websocket.chat.platform.IosDeviceStateMonitor
import com.mangala.wallet.websocket.chat.lifecycle.AppLifecycleManager
import com.mangala.wallet.websocket.chat.lifecycle.IosAppLifecycleManager
import com.mangala.wallet.websocket.chat.network.IosNetworkMonitor
import com.mangala.wallet.websocket.chat.network.NetworkMonitor
import com.mangala.wallet.websocket.chat.persistence.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformWebSocketModule(): Module = module {
    // iOS-specific implementations
    single { DriverFactory() }
    single<NetworkMonitor> { IosNetworkMonitor() }
    single<DeviceStateMonitor> { IosDeviceStateMonitor() }
    single<AppLifecycleManager> { IosAppLifecycleManager() }
    
    // iOS-specific wallet key provider implementation
    // This should be provided by the main app module
    single<WalletKeyProvider> {
        object : WalletKeyProvider {
            override fun getPublicKey(): String {
                throw NotImplementedError("WalletKeyProvider must be provided by the app module")
            }
            
            override suspend fun signMessage(message: String): String? {
                throw NotImplementedError("WalletKeyProvider must be provided by the app module")
            }
        }
    }
}