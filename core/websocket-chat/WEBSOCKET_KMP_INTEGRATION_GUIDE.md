# WebSocket KMP Integration Guide

## Introduction

This document provides integration guidelines for WebSocket with Kotlin Multiplatform (KMP) applications using the conversation-ui-service system. The system uses STOMP over WebSocket with Keycloak JWT authentication.

## Traefik Gateway WebSocket Configuration Issue

### Issue: "Invalid Upgrade header: null"

The backend is receiving WebSocket upgrade requests without the necessary headers. This is likely because Traefik is not properly forwarding WebSocket-specific headers.

### Root Cause Analysis

1. **Traefik Configuration**: The current Traefik configuration only has path rewriting middleware for WebSocket routes, but doesn't explicitly handle WebSocket upgrade headers.

2. **Missing Headers**: The `Connection: Upgrade` and `Upgrade: websocket` headers are being stripped by Traefik before reaching the backend.

### Solution: Update Traefik Configuration

#### 1. Add WebSocket Headers Middleware

Create a new middleware in `/Users/ethannguyen/Data/WorkspaceAI/traefik-gateway/traefik/dynamic.yml`:

```yaml
middlewares:
  # Existing middlewares...
  
  # WebSocket headers middleware
  websocket-headers:
    headers:
      customRequestHeaders:
        X-Forwarded-Proto: "ws"
      # Ensure these headers are passed through
      accessControlAllowHeaders:
        - "Connection"
        - "Upgrade"
        - "Sec-WebSocket-Key"
        - "Sec-WebSocket-Version"
        - "Sec-WebSocket-Extensions"
        - "Authorization"
```

#### 2. Update WebSocket Router Configuration

Modify the WebSocket router to include the headers middleware:

```yaml
routers:
  # Your Project WebSocket Router (Port 8089)
  project-websocket-router:
    rule: "PathPrefix(`/conversation-ui/ws`)"
    service: "project-websocket-service"
    entryPoints:
      - "web"
    middlewares:
      - "websocket-headers"  # Add this BEFORE path rewrite
      - "websocket-path-rewrite"
```

#### 3. Alternative: Use Traefik v3 WebSocket Auto-Detection

Traefik v3 should automatically detect WebSocket connections. Ensure your docker-compose uses:

```yaml
services:
  traefik:
    image: traefik:v3.0  # Already using v3.0
    command:
      # Existing commands...
      # No special WebSocket configuration needed in v3
```

#### 4. Complete Working Configuration

Here's the complete working configuration for `dynamic.yml`:

```yaml
http:
  routers:
    # Your Project API Router (Port 8089)
    project-api-router:
      rule: "PathPrefix(`/auth`)"
      service: "project-api-service"
      entryPoints:
        - "web"
      middlewares:
        - "cors-headers"
        - "rate-limit"
        - "auth-path-rewrite"
    
    # Your Project WebSocket Router (Port 8089)
    project-websocket-router:
      rule: "PathPrefix(`/conversation-ui/ws`)"
      service: "project-websocket-service"
      entryPoints:
        - "web"
      middlewares:
        - "websocket-headers"
        - "websocket-path-rewrite"

  services:
    # Your Project Service (Port 8089)
    project-api-service:
      loadBalancer:
        servers:
          - url: "http://host.docker.internal:8089"
    
    # Your Project WebSocket Service (Port 8089)
    project-websocket-service:
      loadBalancer:
        servers:
          - url: "http://host.docker.internal:8089"

  middlewares:
    # CORS middleware for REST API
    cors-headers:
      headers:
        accessControlAllowMethods:
          - GET
          - POST
          - PUT
          - DELETE
          - OPTIONS
        accessControlAllowOriginList:
          - "*"
        accessControlAllowHeaders:
          - "*"
        accessControlMaxAge: 100
        addVaryHeader: true
    
    # WebSocket headers middleware
    websocket-headers:
      headers:
        customRequestHeaders:
          X-Forwarded-Proto: "ws"
        accessControlAllowHeaders:
          - "Connection"
          - "Upgrade"
          - "Sec-WebSocket-Key"
          - "Sec-WebSocket-Version"
          - "Sec-WebSocket-Extensions"
          - "Authorization"
          - "X-Authorization"
          - "X-Auth-Token"
    
    # Rate limiting
    rate-limit:
      rateLimit:
        burst: 100
    
    # Path rewrite for auth endpoints
    auth-path-rewrite:
      replacePathRegex:
        regex: "^/auth(.*)"
        replacement: "/api/v1/auth$1"
    
    # Path rewrite for websocket endpoints
    websocket-path-rewrite:
      replacePathRegex:
        regex: "^/conversation-ui/ws(.*)"
        replacement: "/api/v1/ws$1"
```

### Testing the Fix

1. **Restart Traefik** after updating the configuration
2. **Check Headers**: Use browser developer tools to verify headers are being sent
3. **Backend Logs**: Check if the backend now receives the Upgrade headers

### Working Web Client Configuration

The web client at `/Users/ethannguyen/Data/WorkspaceAI/conversation-ui-web/websocket-chat.html` successfully connects because:

1. It uses SockJS which handles the WebSocket upgrade automatically
2. It sends the Authorization header in the STOMP connect frame
3. The endpoint URL is `http://localhost:8090/conversation-ui/ws` (not `ws://`)

### KMP Client Implementation with SockJS

For the Kotlin Multiplatform client, ensure:

1. **Use SockJS Client**: Similar to the web implementation
2. **Send Authorization Header**: In the STOMP connect frame
3. **Correct Endpoint**: Use HTTP protocol, not WS protocol for SockJS

Example KMP WebSocket connection:

```kotlin
class WebSocketClient {
    private val client = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    
    fun connect(token: String) {
        // For SockJS, use HTTP endpoint
        val sockJsUrl = "http://localhost:8090/conversation-ui/ws"
        
        // Headers for STOMP connection
        val headers = mapOf(
            "Authorization" to "Bearer $token",
            "X-Authorization" to "Bearer $token"
        )
        
        // Connect using SockJS/STOMP client
        // Implementation depends on the specific library used
    }
}
```

### Additional Debugging

If issues persist:

1. **Enable Traefik Debug Logs**:
   ```yaml
   command:
     - --log.level=DEBUG
   ```

2. **Check Backend Headers**:
   Add logging in `WebSocketConnectionInterceptor.beforeHandshake()`:
   ```java
   log.debug("All headers: {}", request.getHeaders());
   ```

3. **Test Direct Connection**:
   Bypass Traefik temporarily by connecting directly to backend port 8089

### Summary

The issue is that Traefik is not forwarding WebSocket-specific headers. The solution is to add a `websocket-headers` middleware that explicitly preserves these headers. Traefik v3 should handle WebSocket connections automatically, but the explicit middleware ensures headers are properly forwarded.

## WebSocket Architecture

### System Overview
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   KMP Client    │    │   WebSocket API   │    │   Core Domain   │
│   (iOS/Android) │◄──►│   (STOMP/JWT)    │◄──►│   (Business)    │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
    ┌────▼───┐              ┌────▼───┐              ┌────▼───┐
    │ STOMP  │              │ Spring │              │ Redis  │
    │ Client │              │ WebSkt │              │ Cache  │
    └────────┘              └────────┘              └────────┘
```

### Endpoints and Destinations

#### WebSocket Connection
- **Production Gateway**: `ws://gateway.taman2h.fun/conversation-ui/ws` (via Traefik gateway)
- **Development Local**: `ws://localhost:8089/api/v1/ws` (direct to service)
- **Native Endpoint**: `ws://gateway.taman2h.fun/conversation-ui/ws/native` (native WebSocket)

#### Message Destinations

**Client Send (Publish)**:
- `/app/conversation.send` - Send conversation message
- `/app/conversation.stream` - Send streaming message
- `/app/conversation.start` - Start new conversation
- `/app/conversation.end` - End conversation
- `/app/conversation.history` - Get conversation history
- `/app/conversation.typing` - Send typing status
- `/app/conversation.detectLanguage` - Language detection
- `/app/conversation.setLanguage` - Set user language

**Client Receive (Subscribe)**:
- `/user/queue/messages` - Receive personal messages
- `/user/queue/stream` - Receive streaming chunks
- `/user/queue/conversations` - Receive conversation updates
- `/user/queue/history` - Receive conversation history
- `/user/queue/language` - Receive language information
- `/user/queue/errors` - Receive error notifications
- `/topic/typing.{conversationId}` - Receive typing status

## JWT Authentication

### Authentication Flow
1. Client login via Keycloak to get JWT token
2. When connecting WebSocket, send JWT token in header `Authorization: Bearer {token}`
3. Server validates token via `JwtUseCase`
4. If valid, create `WebSocketUserPrincipal` and save session information

### Authentication Headers
```
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

Or alternative headers:
```
X-Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
X-Auth-Token: eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Data Transfer Objects

### ConversationMessage
```kotlin
data class ConversationMessage(
    val id: String? = null,
    val content: String,
    val type: MessageType,
    val conversationId: String? = null,
    val userId: String? = null,
    val timestamp: Instant? = null,
    val metadata: Map<String, Any>? = null,
    val intent: String? = null,
    val entities: Map<String, Any>? = null,
    val confidenceScore: Double? = null,
    val language: String? = null,
    val isStreaming: Boolean? = null,
    val parentMessageId: String? = null
)

enum class MessageType {
    TEXT, INTENT, SYSTEM, ERROR, CONFIRMATION, STATUS
}
```

### ConversationResponse
```kotlin
data class ConversationResponse(
    val id: String? = null,
    val conversationId: String? = null,
    val content: String? = null,
    val type: ResponseType,
    val timestamp: Instant? = null,
    val metadata: Map<String, Any>? = null,
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

enum class ResponseType {
    TEXT, RICH_TEXT, CARD, LIST, QUICK_REPLY, 
    CONFIRMATION, ERROR, STREAM_CHUNK, STREAM_COMPLETE
}
```

### StreamChunk
```kotlin
data class StreamChunk(
    val id: String? = null,
    val conversationId: String? = null,
    val streamId: String? = null,
    val sequence: Int? = null,
    val content: String? = null,
    val type: ChunkType,
    val timestamp: Instant? = null,
    val metadata: Map<String, Any>? = null,
    val deltaContent: String? = null,
    val isFinal: Boolean? = null,
    val tokenCount: Int? = null,
    val processingTimeMs: Long? = null,
    val confidenceScore: Double? = null,
    val finishReason: FinishReason? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null
)

enum class ChunkType {
    START, CONTENT, DELTA, END, ERROR, HEARTBEAT
}

enum class FinishReason {
    STOP, LENGTH, TIMEOUT, ERROR, CANCELLED, CONTENT_FILTER
}
```

## KMP Implementation

### Dependencies (build.gradle.kts)

```kotlin
commonMain {
    dependencies {
        // WebSocket and STOMP
        implementation("io.ktor:ktor-client-websockets:2.3.7")
        implementation("io.ktor:ktor-client-cio:2.3.7")
        
        // JSON serialization
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
        
        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        
        // DateTime
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
        
        // Logging
        implementation("io.github.oshai:kotlin-logging:5.1.1")
    }
}

androidMain {
    dependencies {
        implementation("io.ktor:ktor-client-android:2.3.7")
    }
}

iosMain {
    dependencies {
        implementation("io.ktor:ktor-client-ios:2.3.7")
    }
}
```

### STOMP Client Interface

```kotlin
interface StompClient {
    suspend fun connect(token: String): Boolean
    suspend fun disconnect()
    fun sendMessage(destination: String, message: Any)
    fun subscribe(destination: String, callback: (String) -> Unit)
    fun unsubscribe(destination: String)
    val isConnected: Boolean
}
```

### Environment Configuration

```kotlin
object WebSocketConfig {
    enum class Environment {
        DEVELOPMENT, PRODUCTION
    }
    
    fun getWebSocketUrl(env: Environment): String {
        return when (env) {
            Environment.DEVELOPMENT -> "ws://localhost:8089/api/v1"
            Environment.PRODUCTION -> "ws://gateway.taman2h.fun/conversation-ui"
        }
    }
    
    fun getWebSocketEndpoint(env: Environment): String {
        return "${getWebSocketUrl(env)}/ws"
    }
}
```

### STOMP Client Implementation

```kotlin
class KtorStompClient(
    private val environment: WebSocketConfig.Environment = WebSocketConfig.Environment.PRODUCTION
) : StompClient {
    
    private val baseUrl = WebSocketConfig.getWebSocketUrl(environment)
    
    private val logger = KotlinLogging.logger {}
    private val client = HttpClient(CIO) {
        install(WebSockets)
    }
    
    private var session: DefaultClientWebSocketSession? = null
    private var job: Job? = null
    private val subscriptions = mutableMapOf<String, (String) -> Unit>()
    private val messageChannel = Channel<String>()
    
    override var isConnected: Boolean = false
        private set
    
    override suspend fun connect(token: String): Boolean {
        return try {
            val wsEndpoint = WebSocketConfig.getWebSocketEndpoint(environment)
            logger.info { "Connecting to WebSocket at $wsEndpoint" }
            
            session = client.webSocketSession {
                url(wsEndpoint)
                headers {
                    append("Authorization", "Bearer $token")
                }
            }
            
            session?.let { ws ->
                isConnected = true
                job = CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Send CONNECT frame
                        sendConnectFrame(token)
                        
                        // Start message processing
                        for (frame in ws.incoming) {
                            when (frame) {
                                is Frame.Text -> {
                                    val message = frame.readText()
                                    logger.debug { "Received: $message" }
                                    processMessage(message)
                                }
                                is Frame.Close -> {
                                    logger.info { "WebSocket closed: ${frame.readReason()}" }
                                    isConnected = false
                                    break
                                }
                                else -> { /* Ignore other frame types */ }
                            }
                        }
                    } catch (e: Exception) {
                        logger.error(e) { "Error in WebSocket session" }
                        isConnected = false
                    }
                }
                
                true
            } ?: false
            
        } catch (e: Exception) {
            logger.error(e) { "Failed to connect WebSocket" }
            isConnected = false
            false
        }
    }
    
    override suspend fun disconnect() {
        try {
            session?.send(Frame.Text("DISCONNECT\n\n\u0000"))
            job?.cancel()
            session?.close()
            session = null
            isConnected = false
            subscriptions.clear()
            logger.info { "WebSocket disconnected" }
        } catch (e: Exception) {
            logger.error(e) { "Error disconnecting WebSocket" }
        }
    }
    
    override fun sendMessage(destination: String, message: Any) {
        val messageId = generateId()
        val jsonMessage = when (message) {
            is String -> message
            else -> Json.encodeToString(kotlinx.serialization.serializer(), message)
        }
        
        val stompFrame = buildString {
            appendLine("SEND")
            appendLine("destination:$destination")
            appendLine("content-type:application/json")
            appendLine("content-length:${jsonMessage.toByteArray().size}")
            appendLine("message-id:$messageId")
            appendLine()
            append(jsonMessage)
            append('\u0000')
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                session?.send(Frame.Text(stompFrame))
                logger.debug { "Sent message to $destination: $jsonMessage" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to send message to $destination" }
            }
        }
    }
    
    override fun subscribe(destination: String, callback: (String) -> Unit) {
        val subscriptionId = generateId()
        subscriptions[destination] = callback
        
        val subscribeFrame = buildString {
            appendLine("SUBSCRIBE")
            appendLine("id:$subscriptionId")
            appendLine("destination:$destination")
            appendLine()
            append('\u0000')
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                session?.send(Frame.Text(subscribeFrame))
                logger.info { "Subscribed to $destination with id $subscriptionId" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to subscribe to $destination" }
            }
        }
    }
    
    override fun unsubscribe(destination: String) {
        subscriptions.remove(destination)
        
        val unsubscribeFrame = buildString {
            appendLine("UNSUBSCRIBE")
            appendLine("destination:$destination")
            appendLine()
            append('\u0000')
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                session?.send(Frame.Text(unsubscribeFrame))
                logger.info { "Unsubscribed from $destination" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to unsubscribe from $destination" }
            }
        }
    }
    
    private suspend fun sendConnectFrame(token: String) {
        val connectFrame = buildString {
            appendLine("CONNECT")
            appendLine("accept-version:1.2")
            appendLine("host:gateway.taman2h.fun")
            appendLine("Authorization:Bearer $token")
            appendLine()
            append('\u0000')
        }
        
        session?.send(Frame.Text(connectFrame))
        logger.info { "Sent CONNECT frame" }
    }
    
    private fun processMessage(message: String) {
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
                    logger.info { "WebSocket connected successfully" }
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
                    logger.error { "STOMP error: ${headers["message"]} - $body" }
                }
                "RECEIPT" -> {
                    logger.debug { "Received receipt: ${headers["receipt-id"]}" }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error processing message: $message" }
        }
    }
    
    private fun generateId(): String {
        return "msg-${System.currentTimeMillis()}-${(0..999).random()}"
    }
}
```

### Conversation Service

```kotlin
class ConversationService(
    private val stompClient: StompClient,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    
    private val logger = KotlinLogging.logger {}
    
    // Flows for different message types
    private val _messages = MutableSharedFlow<ConversationResponse>()
    val messages: SharedFlow<ConversationResponse> = _messages.asSharedFlow()
    
    private val _streamChunks = MutableSharedFlow<StreamChunk>()
    val streamChunks: SharedFlow<StreamChunk> = _streamChunks.asSharedFlow()
    
    private val _typingStatus = MutableSharedFlow<TypingStatus>()
    val typingStatus: SharedFlow<TypingStatus> = _typingStatus.asSharedFlow()
    
    private val _errors = MutableSharedFlow<ConversationResponse>()
    val errors: SharedFlow<ConversationResponse> = _errors.asSharedFlow()
    
    suspend fun initialize(jwtToken: String): Boolean {
        val connected = stompClient.connect(jwtToken)
        if (connected) {
            setupSubscriptions()
        }
        return connected
    }
    
    private fun setupSubscriptions() {
        // Subscribe to personal messages
        stompClient.subscribe("/user/queue/messages") { messageJson ->
            try {
                val response = json.decodeFromString<ConversationResponse>(messageJson)
                if (response.type == ConversationResponse.ResponseType.ERROR) {
                    _errors.tryEmit(response)
                } else {
                    _messages.tryEmit(response)
                }
            } catch (e: Exception) {
                logger.error(e) { "Failed to parse message: $messageJson" }
            }
        }
        
        // Subscribe to streaming messages
        stompClient.subscribe("/user/queue/stream") { chunkJson ->
            try {
                val chunk = json.decodeFromString<StreamChunk>(chunkJson)
                _streamChunks.tryEmit(chunk)
            } catch (e: Exception) {
                logger.error(e) { "Failed to parse stream chunk: $chunkJson" }
            }
        }
        
        // Subscribe to conversation updates
        stompClient.subscribe("/user/queue/conversations") { responseJson ->
            try {
                val response = json.decodeFromString<ConversationResponse>(responseJson)
                _messages.tryEmit(response)
            } catch (e: Exception) {
                logger.error(e) { "Failed to parse conversation update: $responseJson" }
            }
        }
    }
    
    fun sendMessage(
        content: String,
        conversationId: String? = null,
        type: ConversationMessage.MessageType = ConversationMessage.MessageType.TEXT,
        metadata: Map<String, Any>? = null
    ) {
        val message = ConversationMessage(
            content = content,
            type = type,
            conversationId = conversationId,
            timestamp = Clock.System.now(),
            metadata = metadata
        )
        
        stompClient.sendMessage("/app/conversation.send", message)
    }
    
    fun sendStreamingMessage(
        content: String,
        conversationId: String? = null,
        metadata: Map<String, Any>? = null
    ) {
        val message = ConversationMessage(
            content = content,
            type = ConversationMessage.MessageType.TEXT,
            conversationId = conversationId,
            timestamp = Clock.System.now(),
            isStreaming = true,
            metadata = metadata
        )
        
        stompClient.sendMessage("/app/conversation.stream", message)
    }
    
    fun startConversation(
        title: String? = null,
        metadata: Map<String, Any>? = null
    ) {
        val request = buildMap {
            title?.let { put("title", it) }
            metadata?.let { put("metadata", it) }
        }
        
        stompClient.sendMessage("/app/conversation.start", request)
    }
    
    fun endConversation(conversationId: String) {
        val request = mapOf("conversationId" to conversationId)
        stompClient.sendMessage("/app/conversation.end", request)
    }
    
    fun getConversationHistory(
        conversationId: String,
        limit: Int = 50,
        beforeMessageId: String? = null
    ) {
        val request = buildMap {
            put("conversationId", conversationId)
            put("limit", limit)
            beforeMessageId?.let { put("beforeMessageId", it) }
        }
        
        stompClient.sendMessage("/app/conversation.history", request)
    }
    
    fun sendTypingStatus(conversationId: String, isTyping: Boolean) {
        val request = mapOf(
            "conversationId" to conversationId,
            "isTyping" to isTyping
        )
        
        stompClient.sendMessage("/app/conversation.typing", request)
        
        // Subscribe to typing updates for this conversation
        stompClient.subscribe("/topic/typing.$conversationId") { statusJson ->
            try {
                val status = json.decodeFromString<TypingStatus>(statusJson)
                _typingStatus.tryEmit(status)
            } catch (e: Exception) {
                logger.error(e) { "Failed to parse typing status: $statusJson" }
            }
        }
    }
    
    fun detectLanguage(text: String, conversationId: String? = null) {
        val request = buildMap {
            put("text", text)
            conversationId?.let { put("conversationId", it) }
        }
        
        stompClient.sendMessage("/app/conversation.detectLanguage", request)
    }
    
    fun setUserLanguage(language: String, conversationId: String? = null) {
        val request = buildMap {
            put("language", language)
            conversationId?.let { put("conversationId", it) }
        }
        
        stompClient.sendMessage("/app/conversation.setLanguage", request)
    }
    
    suspend fun disconnect() {
        stompClient.disconnect()
    }
}

data class TypingStatus(
    val userId: String,
    val isTyping: Boolean,
    val timestamp: String
)
```

### Usage Example

```kotlin
class ConversationViewModel {
    private val conversationService = ConversationService(
        stompClient = KtorStompClient(WebSocketConfig.Environment.PRODUCTION)
    )
    
    suspend fun initializeChat(jwtToken: String) {
        val connected = conversationService.initialize(jwtToken)
        if (connected) {
            // Start observing messages
            conversationService.messages.collect { response ->
                // Update UI with new message
                updateChatUI(response)
            }
        }
    }
    
    fun sendChatMessage(text: String, conversationId: String?) {
        conversationService.sendMessage(
            content = text,
            conversationId = conversationId,
            type = ConversationMessage.MessageType.TEXT
        )
    }
    
    fun startNewConversation() {
        conversationService.startConversation(
            title = "New Chat",
            metadata = mapOf("source" to "mobile_app")
        )
    }
    
    private fun updateChatUI(response: ConversationResponse) {
        // Implementation depends on your UI framework
        // (Compose, SwiftUI, etc.)
    }
}
```

## Error Handling

### Error Types and Codes
- `AUTHENTICATION_ERROR` - JWT authentication error
- `VALIDATION_ERROR` - Input validation error
- `PROCESSING_ERROR` - Message processing error
- `CONNECTION_ERROR` - WebSocket connection error
- `CONVERSATION_NOT_FOUND` - Conversation not found
- `MESSAGE_TOO_LONG` - Message too long (>4000 characters)
- `RATE_LIMIT_EXCEEDED` - Rate limit exceeded

### Error Handling in KMP

```kotlin
class ErrorHandler {
    fun handleConversationError(error: ConversationResponse) {
        when (error.errorCode) {
            "AUTHENTICATION_ERROR" -> {
                // Redirect to login
                redirectToLogin()
            }
            "VALIDATION_ERROR" -> {
                // Show validation message
                showValidationError(error.errorMessage)
            }
            "RATE_LIMIT_EXCEEDED" -> {
                // Show rate limit warning
                showRateLimitWarning()
            }
            else -> {
                // Show generic error
                showGenericError(error.errorMessage)
            }
        }
    }
}
```

## Security Considerations

1. **JWT Token Management**:
   - Store token securely (Keychain/KeyStore)
   - Auto-refresh token when near expiration
   - Clear token on logout

2. **Message Validation**:
   - Validate input before sending
   - Sanitize message content
   - Check message length limits

3. **Connection Security**:
   - Use WSS (WebSocket Secure) in production
   - Verify server certificates
   - Implement connection timeout and retry logic

## Performance Optimization

1. **Message Buffering**:
   - Buffer messages when offline
   - Sync when reconnected
   - Implement exponential backoff for retry

2. **Memory Management**:
   - Limit number of messages in memory
   - Clean up old conversations
   - Proper subscription cleanup

3. **Network Efficiency**:
   - Compress large messages
   - Batch multiple typing events
   - Use heartbeat to maintain connection

## Testing

### Unit Test Example

```kotlin
class ConversationServiceTest {
    
    @Test
    fun shouldSendMessageSuccessfully() = runTest {
        // Given
        val mockStompClient = mockk<StompClient>()
        val service = ConversationService(mockStompClient)
        
        every { mockStompClient.sendMessage(any(), any()) } just Runs
        
        // When
        service.sendMessage("Hello", "conv-123")
        
        // Then
        verify {
            mockStompClient.sendMessage(
                "/app/conversation.send",
                match<ConversationMessage> {
                    it.content == "Hello" && it.conversationId == "conv-123"
                }
            )
        }
    }
}
```

## Gateway Configuration

The system uses Traefik gateway to route traffic:

```yaml
# traefik/dynamic.prod.yml
http:
  routers:
    conversation-websocket:
      rule: "PathPrefix(`/conversation-ui/ws`)"
      service: conversation-ui-websocket
      middlewares:
        - websocket-headers
  
  services:
    conversation-ui-websocket:
      loadBalancer:
        servers:
          - url: "http://localhost:8089/api/v1"
```

### Environment URLs:
- **Production**: `ws://gateway.taman2h.fun/conversation-ui/ws` → `ws://localhost:8089/api/v1/ws`
- **Development**: `ws://localhost:8089/api/v1/ws` (direct)

## Troubleshooting

### Common Issues

1. **Connection Failed**:
   - Check correct URL for environment (production/development)
   - Verify JWT token validity
   - Check network connectivity
   - Ensure Traefik gateway is running (production)

2. **Messages Not Received**:
   - Verify subscription destinations
   - Check user authentication
   - Ensure proper message parsing

3. **Authentication Errors**:
   - Validate JWT token format
   - Check token expiration
   - Verify Keycloak configuration

### Debug Logging

```kotlin
// Enable debug logging
private val logger = KotlinLogging.logger {}

// Log WebSocket events
logger.debug { "WebSocket state: connected=$isConnected" }
logger.debug { "Sending message to $destination: $message" }
logger.debug { "Received message from $source: $content" }
```

## Conclusion

This document provides a complete guide for integrating WebSocket with KMP applications. The system uses STOMP protocol with JWT authentication, supporting real-time messaging, streaming, and language detection.

For additional support, please refer to:
- Source code in `websocket-api/` directory
- Configuration in `WebSocketConfig.java`
- Message DTOs in `share-dto/`