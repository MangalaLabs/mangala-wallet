package com.mangala.wallet.websocket.chat.persistence

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.mangala.wallet.websocket.chat.WebSocketChatDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = WebSocketChatDatabase.Schema,
            context = context,
            name = "websocket_chat.db"
        )
    }
}