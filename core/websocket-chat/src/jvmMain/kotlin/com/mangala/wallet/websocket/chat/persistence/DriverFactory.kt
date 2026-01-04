package com.mangala.wallet.websocket.chat.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.mangala.wallet.websocket.chat.WebSocketChatDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        WebSocketChatDatabase.Schema.create(driver)
        return driver
    }
}