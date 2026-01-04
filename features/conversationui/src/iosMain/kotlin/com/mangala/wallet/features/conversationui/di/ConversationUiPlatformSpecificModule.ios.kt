package com.mangala.wallet.features.conversationui.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import com.mangala.wallet.features.conversationui.data.local.ConversationUiDatabaseWrapper
import com.mangala.wallet.features.conversationui.data.local.IosDatabaseDriverFactory
import com.mangala.wallet.features.conversationui.database.ConversationUiDatabase
import com.mangala.wallet.features.conversationui.domain.usecase.FileExporter
import org.koin.dsl.module

internal actual fun conversationUiPlatformSpecificModule() = module {
    single {
        val driver = NativeSqliteDriver(
            ConversationUiDatabase.Schema,
            CONVERSATION_UI_DATABASE_NAME,
            onConfiguration = { configuration ->
                configuration.copy(
                    extendedConfig = DatabaseConfiguration.Extended(
                        foreignKeyConstraints = true
                    )
                )
            }
        )

        ConversationUiDatabase(driver = driver)
    }
    
    single { 
        ConversationUiDatabaseWrapper(
            IosDatabaseDriverFactory().createDriver()
        )
    }
    
    single { FileExporter() }
}