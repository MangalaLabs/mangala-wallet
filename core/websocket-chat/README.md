# WebSocket Chat Module

This module provides WebSocket infrastructure for the Mangala Wallet chat functionality.

## Overview

The module handles:
- Secure WebSocket connections (WSS only)
- Challenge-response authentication using wallet's private key
- Automatic reconnection with exponential backoff
- Message queuing for offline scenarios
- Heartbeat/ping-pong mechanism

## Architecture

### Core Components

1. **WebSocketClient** - Main interface for WebSocket operations
2. **ConnectionManager** - Manages connection state and reconnection logic
3. **AuthManager** - Handles JWT authentication flow
4. **MessageQueue** - Queues messages when offline
5. **WebSocketEngine** - Platform-specific WebSocket implementation

### Connection States

- `DISCONNECTED` - Not connected
- `CONNECTING` - Establishing connection
- `AUTHENTICATED` - Connected and authenticated
- `CONNECTED` - Connected but not authenticated
- `RECONNECTING` - Attempting to reconnect
- `FAILED` - Connection failed

## Usage

### Setup

```kotlin
// In your app's Koin module
val appModule = module {
    // Provide WalletKeyProvider implementation
    single<WalletKeyProvider> {
        MyWalletKeyProvider(walletManager = get())
    }
}

// Start Koin with both modules
startKoin {
    modules(webSocketChatModule, appModule)
}
```

### Basic Usage

```kotlin
class ChatViewModel(
    private val webSocketClient: WebSocketClient
) : ViewModel() {
    
    init {
        // Observe connection state
        webSocketClient.connectionState
            .onEach { state ->
                println("Connection state: $state")
            }
            .launchIn(viewModelScope)
        
        // Observe incoming messages
        webSocketClient.observeMessages()
            .filterIsInstance<ChatFrame.Message>()
            .onEach { message ->
                handleIncomingMessage(message)
            }
            .launchIn(viewModelScope)
    }
    
    fun connect() {
        viewModelScope.launch {
            webSocketClient.connect()
        }
    }
    
    fun sendMessage(encryptedData: ByteArray, recipient: String) {
        viewModelScope.launch {
            val message = ChatFrame.Message(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                payload = EncryptedPayload(encryptedData),
                recipientAddress = recipient,
                senderAddress = myAddress
            )
            
            webSocketClient.send(message)
        }
    }
}
```

## Configuration

The module uses these configuration properties:
- `websocket.url` - WebSocket server URL (default: wss://chat-dev.example.com/ws)
- `websocket.auth.endpoint` - Authentication endpoint (default: https://chat-dev.example.com/auth)

## Platform Requirements

- Android: Min SDK 26
- iOS: iOS 13.0+
- Desktop: JVM 17+

## Dependencies

- Ktor Client with WebSockets
- Kotlinx Coroutines
- Kotlinx Serialization
- Koin for DI
- Kermit for logging