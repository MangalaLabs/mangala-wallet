package com.mangala.wallet.websocket.chat.platform

import android.content.Context
import com.mangala.wallet.websocket.chat.websocket.ConnectionManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidPlatformHandler(
    private val context: Context,
    private val connectionManager: ConnectionManager,
    private val coroutineScope: CoroutineScope
) : PlatformHandler {
    
    private val powerManager = AndroidPowerManager(context)
    private lateinit var webSocketHandler: AndroidWebSocketHandler
    
    override fun initialize() {
        Napier.d("Initializing Android platform handler", tag = "AndroidPlatformHandler")
        
        webSocketHandler = AndroidWebSocketHandler(
            context = context,
            connectionManager = connectionManager,
            powerManager = powerManager,
            coroutineScope = coroutineScope
        )
    }
    
    override fun requestOptimalPermissions() {
        Napier.d("Requesting optimal permissions for Android", tag = "AndroidPlatformHandler")
        
        // Request battery optimization exemption
        if (!powerManager.isIgnoringBatteryOptimizations()) {
            powerManager.requestIgnoreBatteryOptimizations()
        }
    }
    
    override fun hasOptimalPermissions(): Boolean {
        return powerManager.isIgnoringBatteryOptimizations()
    }
    
    override fun cleanup() {
        Napier.d("Cleaning up Android platform handler", tag = "AndroidPlatformHandler")
        // Cleanup is handled by lifecycle observers
    }
}

actual class PlatformHandlerFactory : KoinComponent {
    private val context: Context by inject()
    
    actual fun createHandler(
        connectionManager: ConnectionManager,
        coroutineScope: CoroutineScope
    ): PlatformHandler {
        return AndroidPlatformHandler(
            context = context,
            connectionManager = connectionManager,
            coroutineScope = coroutineScope
        )
    }
}