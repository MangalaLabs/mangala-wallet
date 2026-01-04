package com.mangala.wallet.websocket.chat.websocket

import io.github.aakira.napier.Napier
import com.mangala.wallet.websocket.chat.auth.AuthManager
import com.mangala.wallet.websocket.chat.websocket.models.ChatFrame
import com.mangala.wallet.websocket.chat.websocket.models.ConnectionState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.benasher44.uuid.uuid4
import kotlinx.datetime.Clock

class WebSocketClientImpl(
    private val connectionManager: ConnectionManager,
    private val messageQueue: MessageQueue,
    private val authManager: AuthManager,
    private val webSocketEngine: WebSocketEngine,
    private val json: Json,
    private val coroutineScope: CoroutineScope
) : WebSocketClient {
    
    
    private val incomingMessages = Channel<ChatFrame>(Channel.UNLIMITED)
    private var receiveJob: Job? = null
    
    override val connectionState: StateFlow<ConnectionState> = connectionManager.connectionState
    
    override suspend fun connect() {
        Napier.d("Initiating connection", tag = "WebSocketClientImpl")
        
        connectionManager.connect()
        
        connectionState.first { it == ConnectionState.CONNECTED }
        
        val authResult = authManager.authenticate()
        if (authResult.isSuccess) {
            connectionManager.updateState(ConnectionState.AUTHENTICATED)
            startMessageReceiver()
            processQueuedMessages()
        } else {
            Napier.e("Authentication failed: ${authResult.exceptionOrNull()}", tag = "WebSocketClientImpl")
            connectionManager.updateState(ConnectionState.FAILED)
            disconnect()
        }
    }
    
    override suspend fun disconnect() {
        Napier.d("Disconnecting WebSocket client", tag = "WebSocketClientImpl")
        stopMessageReceiver()
        connectionManager.disconnect()
        authManager.clearToken()
    }
    
    override suspend fun send(message: ChatFrame): Result<Unit> {
        return try {
            when (connectionState.value) {
                ConnectionState.AUTHENTICATED -> {
                    sendDirectly(message)
                }
                ConnectionState.DISCONNECTED, 
                ConnectionState.RECONNECTING, 
                ConnectionState.FAILED -> {
                    if (message is ChatFrame.Message) {
                        enqueueMessage(message)
                        Result.success(Unit)
                    } else {
                        Result.failure(IllegalStateException("Cannot send non-message frames while disconnected"))
                    }
                }
                else -> {
                    Result.failure(IllegalStateException("Cannot send message in state: ${connectionState.value}"))
                }
            }
        } catch (e: Exception) {
            Napier.e("Failed to send message", e, tag = "WebSocketClientImpl")
            Result.failure(e)
        }
    }
    
    override fun observeMessages(): Flow<ChatFrame> = incomingMessages.receiveAsFlow()
    
    private suspend fun sendDirectly(message: ChatFrame): Result<Unit> {
        return try {
            val jsonString = json.encodeToString(message)
            webSocketEngine.send(jsonString)
            Napier.d("Message sent successfully: ${message.id}", tag = "WebSocketClientImpl")
            Result.success(Unit)
        } catch (e: Exception) {
            Napier.e("Failed to send message directly", e, tag = "WebSocketClientImpl")
            
            if (message is ChatFrame.Message) {
                enqueueMessage(message)
            }
            
            Result.failure(e)
        }
    }
    
    private suspend fun enqueueMessage(message: ChatFrame.Message) {
        val queuedMessage = com.mangala.wallet.websocket.chat.websocket.models.QueuedMessage(
            id = uuid4().toString(),
            message = message,
            enqueuedAt = Clock.System.now().toEpochMilliseconds()
        )
        
        messageQueue.enqueue(queuedMessage)
        Napier.d("Message enqueued: ${message.id}", tag = "WebSocketClientImpl")
        
        val queueSize = messageQueue.size()
        if (queueSize > MAX_QUEUE_SIZE) {
            Napier.w("Queue size exceeded limit ($queueSize > $MAX_QUEUE_SIZE), removing oldest messages", tag = "WebSocketClientImpl")
            repeat(queueSize - MAX_QUEUE_SIZE) {
                messageQueue.dequeue()
            }
        }
    }
    
    private fun startMessageReceiver() {
        stopMessageReceiver()
        
        receiveJob = coroutineScope.launch {
            while (isActive) {
                try {
                    val rawMessage = webSocketEngine.receive()
                    if (rawMessage != null) {
                        handleIncomingMessage(rawMessage)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Napier.e("Error receiving message", e, tag = "WebSocketClientImpl")
                    connectionManager.handleConnectionLost()
                    break
                }
            }
        }
    }
    
    private fun stopMessageReceiver() {
        receiveJob?.cancel()
        receiveJob = null
    }
    
    private suspend fun handleIncomingMessage(rawMessage: String) {
        try {
            val frame = json.decodeFromString<ChatFrame>(rawMessage)
            Napier.d("Received frame: ${frame::class.simpleName} - ${frame.id}", tag = "WebSocketClientImpl")
            
            when (frame) {
                is ChatFrame.Acknowledgment -> handleAcknowledgment(frame)
                is ChatFrame.AuthChallenge -> handleAuthChallenge(frame)
                is ChatFrame.AuthSuccess -> handleAuthSuccess(frame)
                is ChatFrame.Error -> handleError(frame)
                else -> incomingMessages.send(frame)
            }
        } catch (e: Exception) {
            Napier.e("Failed to parse incoming message: $rawMessage", e, tag = "WebSocketClientImpl")
        }
    }
    
    private suspend fun handleAcknowledgment(ack: ChatFrame.Acknowledgment) {
        when (ack.status) {
            com.mangala.wallet.websocket.chat.websocket.models.DeliveryStatus.DELIVERED -> {
                messageQueue.markAsDelivered(ack.messageId)
                Napier.d("Message delivered: ${ack.messageId}", tag = "WebSocketClientImpl")
            }
            com.mangala.wallet.websocket.chat.websocket.models.DeliveryStatus.FAILED -> {
                messageQueue.markAsFailed(ack.messageId)
                Napier.w("Message delivery failed: ${ack.messageId}", tag = "WebSocketClientImpl")
            }
            com.mangala.wallet.websocket.chat.websocket.models.DeliveryStatus.PENDING -> {
                Napier.d("Message still pending: ${ack.messageId}", tag = "WebSocketClientImpl")
            }
        }
        
        incomingMessages.send(ack)
    }
    
    private suspend fun handleAuthChallenge(challenge: ChatFrame.AuthChallenge) {
        Napier.d("Received auth challenge", tag = "WebSocketClientImpl")
        // Auth flow is handled by AuthManager
        incomingMessages.send(challenge)
    }
    
    private suspend fun handleAuthSuccess(success: ChatFrame.AuthSuccess) {
        Napier.i("Authentication successful", tag = "WebSocketClientImpl")
        connectionManager.updateState(ConnectionState.AUTHENTICATED)
        incomingMessages.send(success)
    }
    
    private suspend fun handleError(error: ChatFrame.Error) {
        Napier.e("Received error frame: ${error.code} - ${error.message}", tag = "WebSocketClientImpl")
        incomingMessages.send(error)
        
        when (error.code) {
            "AUTH_EXPIRED", "AUTH_INVALID" -> {
                Napier.w("Auth error received, attempting re-authentication", tag = "WebSocketClientImpl")
                authManager.refreshToken()
            }
            "CONNECTION_LIMIT" -> {
                Napier.e("Connection limit reached, disconnecting", tag = "WebSocketClientImpl")
                disconnect()
            }
        }
    }
    
    private suspend fun processQueuedMessages() {
        coroutineScope.launch {
            val pendingMessages = messageQueue.getPendingMessages()
            Napier.d("Processing ${pendingMessages.size} queued messages", tag = "WebSocketClientImpl")
            
            pendingMessages.forEach { queuedMessage ->
                delay(100) // Small delay between messages
                
                val result = sendDirectly(queuedMessage.message)
                if (result.isSuccess) {
                    messageQueue.markAsDelivered(queuedMessage.id)
                } else if (queuedMessage.retryCount >= queuedMessage.maxRetries) {
                    messageQueue.markAsFailed(queuedMessage.id)
                }
            }
        }
    }
    
    companion object {
        private const val MAX_QUEUE_SIZE = 1000
    }
}