package com.mangala.wallet.websocket.chat.di

import com.mangala.wallet.websocket.chat.auth.WalletKeyProvider
import com.mangala.wallet.websocket.chat.device.DesktopDeviceStateMonitor
import com.mangala.wallet.websocket.chat.device.DeviceStateMonitor
import com.mangala.wallet.websocket.chat.lifecycle.AppLifecycleManager
import com.mangala.wallet.websocket.chat.lifecycle.AppLifecycleManagerImpl
import com.mangala.wallet.websocket.chat.network.DesktopNetworkMonitor
import com.mangala.wallet.websocket.chat.network.NetworkMonitor
import com.mangala.wallet.websocket.chat.persistence.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformWebSocketModule(): Module = module {
    // JVM-specific implementations
    single { DriverFactory() }
    single<NetworkMonitor> { DesktopNetworkMonitor() }
    single<DeviceStateMonitor> { DesktopDeviceStateMonitor() }
    single<AppLifecycleManager> { AppLifecycleManagerImpl() }
    
    // Desktop-specific wallet key provider implementation
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