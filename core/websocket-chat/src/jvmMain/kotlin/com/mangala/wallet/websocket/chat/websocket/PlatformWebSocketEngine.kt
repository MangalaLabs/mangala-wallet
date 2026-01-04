package com.mangala.wallet.websocket.chat.websocket

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import io.github.aakira.napier.Napier

actual class PlatformWebSocketEngine actual constructor(
    private val url: String,
    private val json: Json
) : WebSocketEngine {
    
    private var webSocketSession: DefaultWebSocketSession? = null
    private val _isConnected = MutableStateFlow(false)
    private val incomingMessages = Channel<String>(Channel.UNLIMITED)
    
    private val client = HttpClient(OkHttp) {
        install(WebSockets) {
            maxFrameSize = Long.MAX_VALUE
        }
    }
    
    override suspend fun connect() {
        try {
            client.webSocket(urlString = url) {
                webSocketSession = this
                _isConnected.value = true
                
                // Handle incoming messages
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            incomingMessages.send(frame.readText())
                        }
                        is Frame.Binary -> {
                            // Handle binary if needed
                        }
                        is Frame.Close -> {
                            _isConnected.value = false
                        }
                        else -> {}
                    }
                }
            }
        } catch (e: Exception) {
            Napier.e("WebSocket connection failed", e)
            _isConnected.value = false
            throw e
        }
    }
    
    override suspend fun disconnect() {
        try {
            webSocketSession?.close(CloseReason(CloseReason.Codes.NORMAL, "Client disconnect"))
            webSocketSession = null
            _isConnected.value = false
        } catch (e: Exception) {
            Napier.e("Error disconnecting WebSocket", e)
        }
    }
    
    override suspend fun sendHeartbeat() {
        send("""{"type":"heartbeat"}""")
    }
    
    override suspend fun send(data: String) {
        webSocketSession?.send(Frame.Text(data))
            ?: throw IllegalStateException("WebSocket not connected")
    }
    
    override suspend fun receive(): String? {
        return incomingMessages.tryReceive().getOrNull()
    }
    
    override fun isConnected(): Boolean = _isConnected.value
    
    override fun observeConnectionState(): Flow<Boolean> = _isConnected.asStateFlow()
}