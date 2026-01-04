package com.mangala.wallet.features.conversationui.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.mangala.wallet.features.conversationui.database.ConversationUiDatabase

class AndroidDatabaseDriverFactory(private val context: Context) {
    fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = ConversationUiDatabase.Schema,
            context = context,
            name = "conversation_ui.db"
        )
    }
}