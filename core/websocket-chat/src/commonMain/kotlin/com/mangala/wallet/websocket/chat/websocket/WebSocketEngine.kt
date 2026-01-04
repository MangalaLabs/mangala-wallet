package com.mangala.wallet.websocket.chat.websocket

import kotlinx.coroutines.flow.Flow

/**
 * Interface for platform-specific WebSocket engine implementations
 */
interface WebSocketEngine {
    /**
     * Connect to the WebSocket server
     */
    suspend fun connect()
    
    /**
     * Disconnect from the WebSocket server
     */
    suspend fun disconnect()
    
    /**
     * Send a heartbeat message
     */
    suspend fun sendHeartbeat()
    
    /**
     * Send data to the server
     */
    suspend fun send(data: String)
    
    /**
     * Receive data from the server
     */
    suspend fun receive(): String?
    
    /**
     * Check if the connection is active
     */
    fun isConnected(): Boolean
    
    /**
     * Observe connection state changes
     */
    fun observeConnectionState(): Flow<Boolean>
}