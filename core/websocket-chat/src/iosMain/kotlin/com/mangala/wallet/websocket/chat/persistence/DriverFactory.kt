package com.mangala.wallet.websocket.chat.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.mangala.wallet.websocket.chat.WebSocketChatDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = WebSocketChatDatabase.Schema,
            name = "websocket_chat.db"
        )
    }
}