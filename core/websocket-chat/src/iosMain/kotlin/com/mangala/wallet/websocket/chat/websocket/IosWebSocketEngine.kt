package com.mangala.wallet.websocket.chat.websocket

import io.github.aakira.napier.Napier
import com.mangala.wallet.websocket.chat.websocket.models.ChatFrame
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUUID
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

actual class PlatformWebSocketEngine actual constructor(
    private val url: String,
    private val json: Json
) : WebSocketEngine {
    
    private val tag = "IosWebSocketEngine"
    
    private val client = HttpClient(Darwin) {
        install(WebSockets) {
            pingInterval = 30.seconds
            maxFrameSize = Long.MAX_VALUE
        }
    }
    
    private val sessionState = MutableStateFlow<WebSocketSession?>(null)
    private val receiveChannel = Channel<String>(Channel.UNLIMITED)
    
    override suspend fun connect() {
        Napier.d("Connecting to WebSocket: $url", tag = tag)
        
        try {
            client.webSocket(url) {
                sessionState.value = this
                Napier.i("WebSocket connected", tag = tag)
                
                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                Napier.d("Received text frame: ${text.take(100)}...", tag = tag)
                                receiveChannel.send(text)
                            }
                            is Frame.Binary -> {
                                Napier.w("Received unexpected binary frame", tag = tag)
                            }
                            is Frame.Close -> {
                                Napier.d("Received close frame: ${frame.readReason()}", tag = tag)
                                break
                            }
                            is Frame.Ping -> {
                                Napier.d("Received ping frame", tag = tag)
                            }
                            is Frame.Pong -> {
                                Napier.d("Received pong frame", tag = tag)
                            }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    Napier.d("WebSocket receive channel closed", tag = tag)
                } catch (e: Exception) {
                    Napier.e("Error in WebSocket receive loop", e, tag = tag)
                    throw e
                }
            }
        } finally {
            sessionState.value = null
            Napier.d("WebSocket disconnected", tag = tag)
        }
    }
    
    override suspend fun disconnect() {
        Napier.d("Disconnecting WebSocket", tag = tag)
        
        val session = sessionState.value
        if (session != null) {
            try {
                session.close(CloseReason(CloseReason.Codes.NORMAL, "Client disconnect"))
            } catch (e: Exception) {
                Napier.e("Error closing WebSocket session", e, tag = tag)
            }
        }
        
        sessionState.value = null
        receiveChannel.close()
    }
    
    override suspend fun sendHeartbeat() {
        val heartbeat = ChatFrame.Heartbeat(
            id = NSUUID().UUIDString,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        
        send(json.encodeToString(heartbeat))
    }
    
    override suspend fun send(data: String) {
        val session = sessionState.first { it != null }
            ?: throw IllegalStateException("WebSocket not connected")
        
        try {
            Napier.d("Sending data: ${data.take(100)}...", tag = tag)
            session.send(Frame.Text(data))
        } catch (e: Exception) {
            Napier.e("Failed to send data", e, tag = tag)
            throw e
        }
    }
    
    override suspend fun receive(): String? {
        return try {
            receiveChannel.tryReceive().getOrNull()
        } catch (e: Exception) {
            Napier.e("Error receiving from channel", e, tag = tag)
            null
        }
    }
    
    override fun isConnected(): Boolean {
        return sessionState.value != null
    }
    
    override fun observeConnectionState(): Flow<Boolean> {
        return sessionState.map { it != null }
    }
}