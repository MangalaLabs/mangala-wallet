package com.mangala.wallet.features.conversationui.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.mangala.wallet.features.conversationui.database.ConversationUiDatabase

class IosDatabaseDriverFactory {
    fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = ConversationUiDatabase.Schema,
            name = "conversation_ui.db"
        )
    }
}