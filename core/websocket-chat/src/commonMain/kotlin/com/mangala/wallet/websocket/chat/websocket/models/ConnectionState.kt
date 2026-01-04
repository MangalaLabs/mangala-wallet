package com.mangala.wallet.websocket.chat.websocket.models

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    AUTHENTICATED,
    CONNECTED,
    RECONNECTING,
    FAILED
}