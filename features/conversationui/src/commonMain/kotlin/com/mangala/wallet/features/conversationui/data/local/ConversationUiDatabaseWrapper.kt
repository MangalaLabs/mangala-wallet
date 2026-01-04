package com.mangala.wallet.features.conversationui.data.local

import app.cash.sqldelight.db.SqlDriver
import com.mangala.wallet.features.conversationui.database.ConversationUiDatabase

class ConversationUiDatabaseWrapper(sqlDriver: SqlDriver) {
    val database: ConversationUiDatabase = ConversationUiDatabase(
        driver = sqlDriver
    )
    
    fun getChatHistoryLocalDataSource(): ChatHistoryLocalDataSource {
        return SqlDelightChatHistoryLocalDataSource(database)
    }
}