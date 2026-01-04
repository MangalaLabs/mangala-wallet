package com.mangala.wallet.features.conversationui.domain.service

import com.mangala.wallet.core.auth.SessionManager
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ClientRequestException
import com.mangala.wallet.websocket.chat.websocket.exceptions.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.random.Random
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * STOMP WebSocket service for conversation UI
 * Based on the WebSocket KMP Integration Guide
 */
@OptIn(ExperimentalEncodingApi::class)
class StompWebSocketService(
    private val httpClient: HttpClient,
    private val sessionManager: SessionManager,
    private val json: Json,
    private val environment: WebSocketConfig.Environment = WebSocketConfig.Environment.PRODUCTION
) {
    
    private var session: DefaultClientWebSocketSession? = null
    private var job: Job? = null
    private val subscriptions = mutableMapOf<String, suspend (String) -> Unit>()
    private val messageChannel = Channel<String>()
    
    private var connectedUserId: String? = null
    private var connectedSessionId: String? = null
    
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val _messages = MutableSharedFlow<ConversationResponse>()
    val messages: SharedFlow<ConversationResponse> = _messages.asSharedFlow()
    
    private val _streamChunks = MutableSharedFlow<StreamChunk>()
    val streamChunks: SharedFlow<StreamChunk> = _streamChunks.asSharedFlow()
    
    private val _errors = MutableSharedFlow<ConversationResponse>()
    val errors: SharedFlow<ConversationResponse> = _errors.asSharedFlow()
    
    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        AUTHENTICATED,
        FAILED
    }
    
    suspend fun connect(): Boolean {
        return try {
            val session = sessionManager.sessionState.value
            if (session == null) {
                Napier.e("No active session for WebSocket connection")
                return false
            }
            
            val token = session.token.accessToken
            
            _connectionState.value = ConnectionState.CONNECTING
            
            // Try direct backend connection first (development)
            val isDevelopment = environment == WebSocketConfig.Environment.DEVELOPMENT
            val endpoints = if (isDevelopment) {
                listOf(
                    "ws://10.0.2.2:8089/api/v1/ws/native",  // Direct to backend
                    "ws://10.0.2.2:8090/conversation-ui/ws/native"  // Through local Traefik
                )
            } else {
                listOf(
                    // Only try native WebSocket in production
                    // SockJS won't work through Traefik with current middleware
                    "wss://gateway.taman2h.fun/conversation-ui/ws/native"
                )
            }
            
            // Extract user ID from JWT token for subscriptions
            val userId = extractUserIdFromToken()
            Napier.i("Extracted user ID for subscriptions: $userId")
            
            var lastException: Exception? = null
            
            for (endpoint in endpoints) {
                try {
                    Napier.i("Attempting connection to: $endpoint")
                    
                    this.session = httpClient.webSocketSession(
                        urlString = endpoint,
                        block = {
                            headers {
                                // Use same headers as working web client
                                append("Authorization", "Bearer $token")
                                append("X-Authorization", "Bearer $token")  // Backup header
                                append("X-Auth-Token", token)  // Another backup header
                                if (userId != null) {
                                    append("X-User-ID", userId)  // User ID header
                                }
                                append("Origin", if (isDevelopment) "http://localhost:8090" else "https://gateway.taman2h.fun")
                                
                                // Only add WebSocket protocol for native endpoints
                                if (endpoint.contains("/native")) {
                                    append("Sec-WebSocket-Protocol", "v10.stomp, v11.stomp, v12.stomp")
                                }
                                
                                append("User-Agent", "MangalaWallet/1.0 (Mobile; KMP)")
                                
                                // Log all headers being sent
                                Napier.d("WebSocket headers for $endpoint:")
                                headers.entries().forEach { (key, values) ->
                                    Napier.d("  $key: ${values.joinToString(", ")}")
                                }
                            }
                        }
                    )
                    
                    // If we got here, connection succeeded
                    Napier.i("Successfully connected to: $endpoint")
                    break

                } catch (e: WebSocketException) {
                    Napier.e("Failed to connect to $endpoint: ${e.message}")
                    
                    if (e.message?.contains("401") == true) {
                        lastException = WebSocketAuthenticationException("WebSocket authentication failed during handshake: ${e.message}")
                    } else {
                        lastException = e
                    }
                } catch (e: ClientRequestException) {
                    Napier.e("HTTP error during WebSocket connection to $endpoint: ${e.response.status}")
                    if (e.response.status.value == 401) {
                        lastException = WebSocketAuthenticationException("HTTP 401 Unauthorized during WebSocket connection attempt")
                    } else {
                        lastException = e
                    }
                } catch (e: Exception) {
                    Napier.e("Failed to connect to $endpoint: ${e.message}")
                    lastException = e
                }
            }
            
            // Check if we have a session after trying all endpoints
            this.session?.let { ws ->
                _connectionState.value = ConnectionState.CONNECTED
                
                Napier.i("WebSocket successfully connected")
                Napier.d("Connection upgrade completed - now processing STOMP frames")
                
                sendConnectFrame(token)
                
                setupSubscriptions()
                
                // Start message processing
                job = CoroutineScope(Dispatchers.IO).launch {
                    try {
                        for (frame in ws.incoming) {
                            when (frame) {
                                is Frame.Text -> {
                                    val message = frame.readText()
                                    Napier.d(tag = "StompWebSocketService", message = "${this@StompWebSocketService} Received: $message")
                                    processMessage(message)
                                }
                                is Frame.Close -> {
                                    Napier.i("WebSocket closed: ${frame.readReason()}")
                                    _connectionState.value = ConnectionState.DISCONNECTED
                                    break
                                }
                                else -> { /* Ignore other frame types */ }
                            }
                        }
                    } catch (e: Exception) {
                        Napier.e("Error in WebSocket session", e)
                        _connectionState.value = ConnectionState.FAILED
                    }
                }
                
                Napier.i("WebSocket connection and STOMP setup completed")
                true
            } ?: run {
                // All endpoints failed
                Napier.e("Failed to connect to any WebSocket endpoint")
                throw lastException ?: Exception("Failed to connect to any WebSocket endpoint")
            }
            
        } catch (e: Exception) {
            Napier.e("Failed to connect WebSocket", e)
            when (e) {
                is WebSocketAuthenticationException -> {
                    throw e
                }
                else -> {
                    Napier.e("Unknown error: ${e.message}")
                    Napier.e("Error type: ${e::class.simpleName}")
                    _connectionState.value = ConnectionState.FAILED
                }
            }
            false
        }
    }
    
    suspend fun disconnect() {
        try {
            session?.send(Frame.Text("DISCONNECT\n\n\u0000"))
            job?.cancel()
            session?.close()
            session = null
            _connectionState.value = ConnectionState.DISCONNECTED
            subscriptions.clear()
            
            connectedUserId = null
            connectedSessionId = null
            
            Napier.i("WebSocket disconnected")
        } catch (e: Exception) {
            Napier.e("Error disconnecting WebSocket", e)
        }
    }
    
    fun sendMessage(
        content: String,
        conversationId: String? = null,
        type: MessageType = MessageType.TEXT,
        metadata: Map<String, JsonElement>? = null
    ) {
        val message = ConversationMessage(
            id = "msg_${Clock.System.now().toEpochMilliseconds()}",
            content = content,
            type = type,
            conversationId = conversationId,
            timestamp = Clock.System.now(),
            metadata = buildMap {
                putAll(metadata ?: emptyMap())
                put("clientTimestamp", JsonPrimitive(Clock.System.now().toString()))
                put("source", JsonPrimitive("passkey_authenticated_mobile"))
            }
        )
        
        sendStompMessage("/app/conversation.send", message)
    }
    
    fun startConversation(
        title: String? = null,
        metadata: Map<String, JsonElement>? = null
    ) {
        val request = StartConversationRequest(
            title = title,
            metadata = metadata
        )
        
        sendStompMessage("/app/conversation.start", request)
    }
    
    private inline fun <reified T> sendStompMessage(destination: String, message: T) {
        val messageId = generateId()
        val jsonMessage = when (message) {
            is String -> message
            is ConversationMessage -> json.encodeToString(ConversationMessage.serializer(), message)
            is StartConversationRequest -> json.encodeToString(StartConversationRequest.serializer(), message)
            else -> json.encodeToString(message)
        }
        
        val stompFrame = buildString {
            appendLine("SEND")
            appendLine("destination:$destination")
            appendLine("content-type:application/json")
            appendLine("content-length:${jsonMessage.encodeToByteArray().size}")
            appendLine("message-id:$messageId")
            appendLine()
            append(jsonMessage)
            append("\u0000")
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                session?.send(Frame.Text(stompFrame))
                Napier.d(tag = "StompWebSocketService", message = "Sent message to $destination: $jsonMessage")
            } catch (e: Exception) {
                Napier.e(tag = "StompWebSocketService", message = "Failed to send message to $destination", throwable = e)
            }
        }
    }
    
    private fun subscribe(destination: String, callback: suspend (String) -> Unit) {
        val subscriptionId = generateId()
        subscriptions[destination] = callback
        
        val subscribeFrame = buildString {
            appendLine("SUBSCRIBE")
            appendLine("id:$subscriptionId")
            appendLine("destination:$destination")
            appendLine()
            append("\u0000")
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                session?.send(Frame.Text(subscribeFrame))
                Napier.i(tag = "StompWebSocketService", message = "Subscribed to $destination with id $subscriptionId")
            } catch (e: Exception) {
                Napier.e(tag = "StompWebSocketService", message = "Failed to subscribe to $destination", throwable = e)
            }
        }
    }
    
    private suspend fun sendConnectFrame(token: String) {
        val connectFrame = buildString {
            appendLine("CONNECT")
            appendLine("accept-version:1.0,1.1,1.2")
            appendLine("heart-beat:10000,10000")
            appendLine("host:gateway.taman2h.fun")
            // Use same auth headers as working web client
            appendLine("Authorization:Bearer $token")
            appendLine("X-Authorization:Bearer $token")
            appendLine("X-Auth-Token:$token")
            appendLine()
            append("\u0000")
        }
        
        session?.send(Frame.Text(connectFrame))
        Napier.i("Sent CONNECT frame with auth headers (matching web client)")
        Napier.d("CONNECT frame content: ${connectFrame.replace("\u0000", "<NULL>")}")
    }
    
    private suspend fun setupSubscriptions() {
        // Extract user ID from JWT token for user-specific subscriptions
        val userId = extractUserIdFromToken()
        Napier.i("Setting up subscriptions for user: $userId")
        
        // Subscribe to generic user queue
        subscribe("/user/queue/messages") { messageJson ->
            try {
                Napier.d(tag = "StompWebSocketService", message = "$this Received message on /user/queue/messages: $messageJson")
                val response = json.decodeFromString<ConversationResponse>(messageJson)
                if (response.type == ResponseType.ERROR) {
                    _errors.emit(response)
                } else {
                    _messages.emit(response)
                }
            } catch (e: Exception) {
                Napier.e(tag = "StompWebSocketService", message = "Failed to parse message: $messageJson", throwable = e)
            }
        }
        
        // Subscribe to user-specific queue (critical for receiving messages)
        if (userId != null) {
            subscribe("/user/$userId/queue/messages") { messageJson ->
                try {
                    Napier.d("Received message on /user/$userId/queue/messages: $messageJson")
                    val response = json.decodeFromString<ConversationResponse>(messageJson)
                    if (response.type == ResponseType.ERROR) {
                        _errors.emit(response)
                    } else {
                        _messages.emit(response)
                    }
                } catch (e: Exception) {
                    Napier.e("Failed to parse user-specific message: $messageJson", e)
                }
            }
        }
        
        // Subscribe to streaming messages
        subscribe("/user/queue/stream") { chunkJson ->
            try {
                val chunk = json.decodeFromString<StreamChunk>(chunkJson)
                _streamChunks.tryEmit(chunk)
            } catch (e: Exception) {
                Napier.e("Failed to parse stream chunk: $chunkJson", e)
            }
        }
        
        // Subscribe to error messages
        subscribe("/user/queue/errors") { errorJson ->
            try {
                Napier.w("Received error message: $errorJson")
                val response = json.decodeFromString<ConversationResponse>(errorJson)
                _errors.tryEmit(response)
            } catch (e: Exception) {
                Napier.e("Failed to parse error message: $errorJson", e)
            }
        }
        
        // Subscribe to conversation updates
        subscribe("/user/queue/conversations") { responseJson ->
            try {
                val response = json.decodeFromString<ConversationResponse>(responseJson)
                _messages.tryEmit(response)
            } catch (e: Exception) {
                Napier.e("Failed to parse conversation update: $responseJson", e)
            }
        }
    }
    
    private fun setupSessionBasedSubscriptions() {
        // Subscribe to the new session-based endpoint format
        val userId = connectedUserId
        val sessionId = connectedSessionId
        
        if (userId != null && sessionId != null) {
            val sessionEndpoint = "/users/$userId/sessions/$sessionId/queue/messages"
            
            Napier.i("Setting up session-based subscription to: $sessionEndpoint")
            
            subscribe(sessionEndpoint) { messageJson ->
                try {
                    Napier.d("Received message on $sessionEndpoint: $messageJson")
                    val response = json.decodeFromString<ConversationResponse>(messageJson)
                    if (response.type == ResponseType.ERROR) {
                        _errors.emit(response)
                    } else {
                        _messages.emit(response)
                    }
                } catch (e: Exception) {
                    Napier.e("Failed to parse session-based message: $messageJson", e)
                }
            }
            
            // Also subscribe to other session-based endpoints
            subscribe("/users/$userId/sessions/$sessionId/queue/stream") { chunkJson ->
                try {
                    val chunk = json.decodeFromString<StreamChunk>(chunkJson)
                    _streamChunks.tryEmit(chunk)
                } catch (e: Exception) {
                    Napier.e("Failed to parse session-based stream chunk: $chunkJson", e)
                }
            }
            
            subscribe("/users/$userId/sessions/$sessionId/queue/errors") { errorJson ->
                try {
                    Napier.w("Received session-based error message: $errorJson")
                    val response = json.decodeFromString<ConversationResponse>(errorJson)
                    _errors.tryEmit(response)
                } catch (e: Exception) {
                    Napier.e("Failed to parse session-based error message: $errorJson", e)
                }
            }
            
            subscribe("/users/$userId/sessions/$sessionId/queue/conversations") { responseJson ->
                try {
                    val response = json.decodeFromString<ConversationResponse>(responseJson)
                    _messages.tryEmit(response)
                } catch (e: Exception) {
                    Napier.e("Failed to parse session-based conversation update: $responseJson", e)
                }
            }
        } else {
            Napier.e("Cannot setup session-based subscriptions - missing userId or sessionId")
        }
    }
    
    private fun extractUserIdFromToken(): String? {
        return try {
            val token = sessionManager.sessionState.value?.token?.accessToken ?: return null
            val parts = token.split(".")
            if (parts.size != 3) return null
            
            val payload = parts[1]
            val paddedPayload = when (payload.length % 4) {
                2 -> "$payload=="
                3 -> "$payload="
                else -> payload
            }
            val decodedPayload = Base64.decode(paddedPayload.replace('-', '+').replace('_', '/'))
            val payloadJson = json.parseToJsonElement(decodedPayload.decodeToString()).jsonObject
            
            // Try different fields where user ID might be stored
            payloadJson["sub"]?.jsonPrimitive?.content
                ?: payloadJson["user_id"]?.jsonPrimitive?.content
                ?: payloadJson["userId"]?.jsonPrimitive?.content
                ?: payloadJson["email"]?.jsonPrimitive?.content
                ?: payloadJson["username"]?.jsonPrimitive?.content
        } catch (e: Exception) {
            Napier.e("Failed to extract user ID from token", e)
            null
        }
    }
    
    private suspend fun processMessage(message: String) {
        try {
            val lines = message.split('\n')
            if (lines.isEmpty()) return
            
            val command = lines[0]
            val headers = mutableMapOf<String, String>()
            var bodyStartIndex = 1
            
            // Parse headers
            for (i in 1 until lines.size) {
                val line = lines[i]
                if (line.isEmpty()) {
                    bodyStartIndex = i + 1
                    break
                }
                val parts = line.split(':', limit = 2)
                if (parts.size == 2) {
                    headers[parts[0]] = parts[1]
                }
            }
            
            // Extract body
            val body = if (bodyStartIndex < lines.size) {
                lines.subList(bodyStartIndex, lines.size)
                    .joinToString("\n")
                    .removeSuffix("\u0000")
            } else ""
            
            when (command) {
                "CONNECTED" -> {
                    Napier.i("WebSocket connected successfully")
                    
                    // Parse user-name header to extract userId and sessionId
                    val userName = headers["user-name"]
                    if (userName != null) {
                        try {
                            val userInfo = json.parseToJsonElement(userName).jsonObject
                            connectedUserId = userInfo["userId"]?.jsonPrimitive?.content
                            connectedSessionId = userInfo["sessionId"]?.jsonPrimitive?.content
                            
                            Napier.i("Parsed from CONNECTED message - userId: $connectedUserId, sessionId: $connectedSessionId")
                            
                            // Now setup subscriptions with the new endpoint format
                            if (connectedUserId != null && connectedSessionId != null) {
                                setupSessionBasedSubscriptions()
                            }
                        } catch (e: Exception) {
                            Napier.e("Failed to parse user-name header: $userName", e)
                        }
                    }
                    
                    _connectionState.value = ConnectionState.AUTHENTICATED
                }
                "MESSAGE" -> {
                    val destination = headers["destination"]
                    if (destination != null) {
                        // Find matching subscription
                        subscriptions.entries.find { (subDest, _) ->
                            destination.startsWith(subDest) || 
                            destination.matches(subDest.replace("*", ".*").toRegex())
                        }?.value?.invoke(body)
                    }
                }
                "ERROR" -> {
                    Napier.e("STOMP error: ${headers["message"]} - $body")
                }
                "RECEIPT" -> {
                    Napier.d("Received receipt: ${headers["receipt-id"]}")
                }
            }
        } catch (e: Exception) {
            Napier.e("Error processing message: $message", e)
        }
    }
    
    private fun generateId(): String {
        return "msg-${Clock.System.now().toEpochMilliseconds()}-${(0..999).random()}"
    }
    
    private fun generateSockJSSessionId(): String {
        // Generate SockJS session ID format: 8 random characters
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        return (1..8).map { chars.random() }.joinToString("")
    }
    
}

// WebSocket configuration
object WebSocketConfig {
    enum class Environment {
        DEVELOPMENT, PRODUCTION
    }
    
    private fun getWebSocketUrl(env: Environment): String {
        return when (env) {
            Environment.DEVELOPMENT -> "ws://10.0.2.2:8090" // Android emulator localhost
            Environment.PRODUCTION -> "wss://gateway.taman2h.fun" // Back to HTTPS
        }
    }
    
    fun getWebSocketEndpoint(env: Environment, useNative: Boolean = false): String {
        // Use /api/v1/ws/native endpoint for native STOMP clients (mobile apps)
        val suffix = if (useNative) "/api/v1/ws/native" else "/api/v1/ws"
        return "${getWebSocketUrl(env)}${suffix}"
    }
}

// Data classes for messages
@Serializable
data class StartConversationRequest(
    val title: String? = null,
    val metadata: Map<String, JsonElement>? = null
)

@Serializable
data class ConversationMessage(
    val id: String? = null,
    val content: String,
    val type: MessageType,
    val conversationId: String? = null,
    val userId: String? = null,
    val timestamp: kotlinx.datetime.Instant? = null,
    val metadata: Map<String, JsonElement>? = null,
    val intent: String? = null,
    val entities: Map<String, JsonElement>? = null,
    val confidenceScore: Double? = null,
    val language: String? = null,
    val isStreaming: Boolean? = null,
    val parentMessageId: String? = null
)

@Serializable
enum class MessageType {
    TEXT, INTENT, SYSTEM, ERROR, CONFIRMATION, STATUS
}

@Serializable
data class ConversationResponse(
    val id: String? = null,
    val conversationId: String? = null,
    val content: String? = null,
    val type: ResponseType,
    val timestamp: kotlinx.datetime.Instant? = null,
    val metadata: Map<String, JsonElement>? = null,
    val quickReplies: List<QuickReply>? = null,
    val actions: List<ActionButton>? = null,
    val suggestions: List<String>? = null,
    val isFinal: Boolean? = null,
    val requiresConfirmation: Boolean? = null,
    val confidenceScore: Double? = null,
    val processingTimeMs: Long? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null
)

@Serializable
enum class ResponseType {
    @SerialName("text")
    TEXT, 
    @SerialName("rich_text")
    RICH_TEXT, 
    @SerialName("card")
    CARD, 
    @SerialName("list")
    LIST, 
    @SerialName("quick_reply")
    QUICK_REPLY, 
    @SerialName("confirmation")
    CONFIRMATION, 
    @SerialName("error")
    ERROR, 
    @SerialName("stream_chunk")
    STREAM_CHUNK, 
    @SerialName("stream_complete")
    STREAM_COMPLETE
}

@Serializable
data class StreamChunk(
    val id: String? = null,
    val conversationId: String? = null,
    val streamId: String? = null,
    val sequence: Int? = null,
    val content: String? = null,
    val type: ChunkType,
    val timestamp: kotlinx.datetime.Instant? = null,
    val metadata: Map<String, JsonElement>? = null,
    val deltaContent: String? = null,
    val isFinal: Boolean? = null,
    val tokenCount: Int? = null,
    val processingTimeMs: Long? = null,
    val confidenceScore: Double? = null,
    val finishReason: FinishReason? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null
)

@Serializable
enum class ChunkType {
    START, CONTENT, DELTA, END, ERROR, HEARTBEAT
}

@Serializable
enum class FinishReason {
    STOP, LENGTH, TIMEOUT, ERROR, CANCELLED, CONTENT_FILTER
}

@Serializable
data class QuickReply(
    val text: String,
    val payload: String? = null
)

@Serializable
data class ActionButton(
    val text: String,
    val action: String,
    val payload: Map<String, JsonElement>? = null
)