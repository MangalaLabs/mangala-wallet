package com.mangala.wallet.websocket.chat.platform

import com.mangala.wallet.websocket.chat.websocket.ConnectionManager
import kotlinx.coroutines.CoroutineScope

/**
 * Platform-specific handler interface for managing WebSocket connections
 * with respect to platform constraints (Doze mode, background tasks, etc.)
 */
interface PlatformHandler {
    /**
     * Initialize platform-specific handlers
     */
    fun initialize()
    
    /**
     * Request necessary permissions for optimal WebSocket operation
     */
    fun requestOptimalPermissions()
    
    /**
     * Check if the app has optimal permissions for WebSocket operation
     */
    fun hasOptimalPermissions(): Boolean
    
    /**
     * Clean up platform-specific resources
     */
    fun cleanup()
}

/**
 * Factory for creating platform-specific handlers
 */
expect class PlatformHandlerFactory {
    fun createHandler(
        connectionManager: ConnectionManager,
        coroutineScope: CoroutineScope
    ): PlatformHandler
}