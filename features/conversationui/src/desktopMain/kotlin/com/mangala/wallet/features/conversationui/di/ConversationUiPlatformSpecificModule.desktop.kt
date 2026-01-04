package com.mangala.wallet.features.conversationui.di

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.mangala.wallet.features.conversationui.ConversationUiDatabase
import com.mangala.wallet.features.conversationui.data.local.ConversationUiDatabaseWrapper
import com.mangala.wallet.features.conversationui.data.local.DesktopDatabaseDriverFactory
import com.mangala.wallet.features.conversationui.domain.usecase.FileExporter
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.Paths

internal actual fun conversationUiPlatformSpecificModule() = module {
    single {
        val databaseName = CONVERSATION_UI_DATABASE_NAME
        val databasePath = Paths.get(System.getProperty("user.home"), "mangalawallet").toAbsolutePath()
        if (!Files.exists(databasePath) || !Files.isDirectory(databasePath)) {
            Files.createDirectory(databasePath)
        }
        val jdbcUrl = "jdbc:sqlite:$databasePath/$databaseName"
        val driver = JdbcSqliteDriver(jdbcUrl).also { ConversationUiDatabase.Schema.create(it) }
        ConversationUiDatabase(driver)
    }
    
    single { 
        ConversationUiDatabaseWrapper(
            DesktopDatabaseDriverFactory().createDriver()
        )
    }
    
    single { FileExporter() }
}