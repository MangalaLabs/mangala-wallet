package com.mangala.wallet.websocket.chat.di

import com.mangala.wallet.websocket.chat.auth.WalletKeyProvider
import com.mangala.wallet.websocket.chat.lifecycle.AppLifecycleManager
import com.mangala.wallet.websocket.chat.lifecycle.AppLifecycleManagerImpl
import com.mangala.wallet.websocket.chat.network.AndroidNetworkMonitor
import com.mangala.wallet.websocket.chat.network.NetworkMonitor
import com.mangala.wallet.websocket.chat.device.AndroidDeviceStateMonitor
import com.mangala.wallet.websocket.chat.device.DeviceStateMonitor
import com.mangala.wallet.websocket.chat.persistence.DriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformWebSocketModule(): Module = module {
    // Override the DriverFactory to use Android context
    single { DriverFactory(androidContext()) }
    
    // Platform-specific implementations
    single<NetworkMonitor> { AndroidNetworkMonitor(androidContext()) }
    single<DeviceStateMonitor> { AndroidDeviceStateMonitor(androidContext()) }
    single<AppLifecycleManager> { AppLifecycleManagerImpl(androidApplication()) }
    
    // Android-specific wallet key provider implementation
    // This should be provided by the main app module
    // For now, we'll create a placeholder that throws an error
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