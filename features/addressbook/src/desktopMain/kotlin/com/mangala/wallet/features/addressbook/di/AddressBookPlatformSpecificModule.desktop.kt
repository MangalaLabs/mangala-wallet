package com.mangala.wallet.features.addressbook.di

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.local.DatabaseInitializer
import com.mangala.wallet.features.addressbook.data.local.avatar.AvatarLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.avatar.DesktopAvatarLocalDataSource
import com.mangala.wallet.features.addressbook.database.AddressBookDatabase
import com.mangala.wallet.features.addressbook.utils.clipboard.ClipboardManager
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.Paths

internal actual fun addressBookPlatformSpecificModule() = module {
    single {
        val databaseName = ADDRESS_BOOK_DATABASE_NAME
        val databasePath = Paths.get(System.getProperty("user.home"), "mangalawallet").toAbsolutePath()
        if (!Files.exists(databasePath) || !Files.isDirectory(databasePath)) {
            Files.createDirectory(databasePath)
        }
        val jdbcUrl = "jdbc:sqlite:$databasePath/$databaseName"
        val driver = JdbcSqliteDriver(jdbcUrl).also { AddressBookDatabase.Schema.create(it) }
        val database = AddressBookDatabase(driver)
        
        // Initialize default data if needed
        val initializer = DatabaseInitializer(database, get())
        initializer.initializeDefaultData()
        
        AddressBookDatabaseWrapper(database)
    }

    single<AvatarLocalDataSource> { DesktopAvatarLocalDataSource() }

    single<ClipboardManager> { ClipboardManager() }
}