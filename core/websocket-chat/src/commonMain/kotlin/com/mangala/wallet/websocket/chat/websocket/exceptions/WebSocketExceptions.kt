package com.mangala.wallet.websocket.chat.websocket.exceptions

/**
 * Exception thrown when WebSocket authentication fails (401 Unauthorized)
 */
class WebSocketAuthenticationException(message: String) : Exception(message)

/**
 * Exception thrown when WebSocket connection fails due to various reasons
 */
class WebSocketConnectionException(message: String, cause: Throwable? = null) : Exception(message, cause)