package com.mangala.wallet.features.conversationui.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.mangala.wallet.features.conversationui.ConversationUiDatabase
import java.io.File

class DesktopDatabaseDriverFactory {
    fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), ".mangala/conversation_ui.db")
        databasePath.parentFile.mkdirs()
        
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")
        ConversationUiDatabase.Schema.create(driver)
        return driver
    }
}