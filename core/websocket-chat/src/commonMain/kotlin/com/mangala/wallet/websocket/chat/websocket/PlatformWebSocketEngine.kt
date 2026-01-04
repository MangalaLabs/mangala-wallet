package com.mangala.wallet.websocket.chat.websocket

import kotlinx.serialization.json.Json

expect class PlatformWebSocketEngine(
    url: String,
    json: Json
) : WebSocketEngine