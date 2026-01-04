package com.mangala.wallet.websocket.chat.persistence

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}