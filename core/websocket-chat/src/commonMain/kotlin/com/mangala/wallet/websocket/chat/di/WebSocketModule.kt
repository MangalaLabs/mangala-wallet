package com.mangala.wallet.websocket.chat.di

import com.mangala.wallet.domain.reset.usecases.ClearWebSocketChatUseCase
import com.mangala.wallet.websocket.chat.WebSocketChatDatabase
import com.mangala.wallet.websocket.chat.auth.AuthManager
import com.mangala.wallet.websocket.chat.auth.AuthManagerImpl
import com.mangala.wallet.websocket.chat.auth.JwtAuthManager
import com.mangala.wallet.websocket.chat.auth.WalletKeyProvider
import com.mangala.wallet.websocket.chat.persistence.DriverFactory
import com.mangala.wallet.websocket.chat.persistence.MessageRepository
import com.mangala.wallet.websocket.chat.persistence.MessageRepositoryImpl
import com.mangala.wallet.websocket.chat.queue.PersistentMessageQueue
import com.mangala.wallet.websocket.chat.lifecycle.AppLifecycleManager
import com.mangala.wallet.websocket.chat.device.DeviceStateMonitor
import com.mangala.wallet.websocket.chat.network.NetworkMonitor
import com.mangala.wallet.websocket.chat.reconnection.AdaptiveReconnectionStrategy
import com.mangala.wallet.websocket.chat.reconnection.ReconnectionStrategy
import com.mangala.wallet.websocket.chat.usecase.ClearWebSocketChatUseCaseImpl
import com.mangala.wallet.websocket.chat.websocket.*
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val webSocketChatModule = module {
    // JSON configuration
    single<Json>(named("websocket")) {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
            classDiscriminator = "type"
        }
    }
    
    // HTTP client for authentication
    single<HttpClient>(named("websocket_auth")) {
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>(named("websocket")))
            }
            
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
            
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 10_000
            }
        }
    }
    
    // WebSocket configuration
    single<WebSocketConfig> {
        WebSocketConfig(
            url = "wss://gateway.taman2h.fun/conversation-ui/ws", // Production gateway URL
            authEndpoint = "" // Not needed for JWT auth
        )
    }
    
    // Coroutine scope for WebSocket operations
    single<CoroutineScope>(named("websocket")) {
        CoroutineScope(SupervisorJob())
    }
    
    // WebSocket engine (platform-specific)
    single<WebSocketEngine> {
        PlatformWebSocketEngine(
            url = get<WebSocketConfig>().url,
            json = get(named("websocket"))
        )
    }
    
    // Database
    single { 
        WebSocketChatDatabase(
            driver = get<DriverFactory>().createDriver()
        )
    }
    single { get<WebSocketChatDatabase>().chatMessageQueries }
    
    // Persistence
    single<MessageRepository> { 
        MessageRepositoryImpl(
            queries = get(),
            json = get(named("websocket"))
        )
    }
    
    // Message queue with persistence
    factory<MessageQueue> { params ->
        val conversationId: String = params.getOrNull() ?: "default"
        val senderId: String = params.getOrNull() ?: "anonymous"
        val recipientId: String = params.getOrNull() ?: "server"
        
        PersistentMessageQueue(
            repository = get(),
            conversationId = conversationId,
            senderId = senderId,
            recipientId = recipientId,
            coroutineScope = get(named("websocket"))
        )
    }
    
    // Monitoring - implementations provided by platform modules
    
    // Reconnection strategy
    single<ReconnectionStrategy> { AdaptiveReconnectionStrategy() }
    
    // Connection manager
    single<ConnectionManager> {
        ConnectionManager(
            webSocketEngine = get(),
            coroutineScope = get(named("websocket")),
            reconnectionStrategy = get(),
            networkMonitor = get(),
            deviceStateMonitor = get(),
            appLifecycleManager = get()
        )
    }
    
    // Auth manager - Use JWT-based authentication with SessionManager
    single<AuthManager> {
        JwtAuthManager(
            sessionManager = get()
        )
    }
    
    // WebSocket client
    single<WebSocketClient> {
        WebSocketClientImpl(
            connectionManager = get(),
            messageQueue = get(),
            authManager = get(),
            webSocketEngine = get(),
            json = get(named("websocket")),
            coroutineScope = get(named("websocket"))
        )
    }
    
    // Reset use case binding
    factoryOf(::ClearWebSocketChatUseCaseImpl) bind ClearWebSocketChatUseCase::class
    
    // Platform-specific modules
    includes(platformWebSocketModule())
}

data class WebSocketConfig(
    val url: String,
    val authEndpoint: String
)

// This will be implemented per platform
expect fun platformWebSocketModule(): Module