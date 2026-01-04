package com.mangala.wallet.websocket.chat.websocket

import com.mangala.wallet.websocket.chat.websocket.models.ChatFrame
import com.mangala.wallet.websocket.chat.websocket.models.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WebSocketClient {
    val connectionState: StateFlow<ConnectionState>
    suspend fun connect()
    suspend fun disconnect()
    suspend fun send(message: ChatFrame): Result<Unit>
    fun observeMessages(): Flow<ChatFrame>
}