package com.mangala.wallet.websocket.chat.platform

import com.mangala.wallet.websocket.chat.websocket.ConnectionManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope

class DesktopPlatformHandler(
    private val connectionManager: ConnectionManager,
    private val coroutineScope: CoroutineScope
) : PlatformHandler {
    
    override fun initialize() {
        Napier.d("Initializing Desktop platform handler", tag = "DesktopPlatformHandler")
        // Desktop doesn't have special power management constraints
        // Connection can run normally
    }
    
    override fun requestOptimalPermissions() {
        Napier.d("No special permissions needed for Desktop", tag = "DesktopPlatformHandler")
        // Desktop doesn't require special permissions
    }
    
    override fun hasOptimalPermissions(): Boolean {
        // Desktop always has optimal permissions
        return true
    }
    
    override fun cleanup() {
        Napier.d("Cleaning up Desktop platform handler", tag = "DesktopPlatformHandler")
        // No special cleanup needed for desktop
    }
}

actual class PlatformHandlerFactory {
    actual fun createHandler(
        connectionManager: ConnectionManager,
        coroutineScope: CoroutineScope
    ): PlatformHandler {
        return DesktopPlatformHandler(
            connectionManager = connectionManager,
            coroutineScope = coroutineScope
        )
    }
}