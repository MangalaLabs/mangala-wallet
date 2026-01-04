package com.mangala.wallet.features.addressbook.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.local.DatabaseInitializer
import com.mangala.wallet.features.addressbook.data.local.avatar.AvatarLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.avatar.IosAvatarLocalDataSource
import com.mangala.wallet.features.addressbook.database.AddressBookDatabase
import com.mangala.wallet.features.addressbook.utils.clipboard.ClipboardManager
import org.koin.dsl.module

internal actual fun addressBookPlatformSpecificModule() = module {
    single {
        val driver = NativeSqliteDriver(
            AddressBookDatabase.Schema,
            ADDRESS_BOOK_DATABASE_NAME,
            onConfiguration = { configuration ->
                configuration.copy(
                    extendedConfig = DatabaseConfiguration.Extended(
                        foreignKeyConstraints = true
                    )
                )
            }
        )

        val database = AddressBookDatabase(driver = driver)
        
        // Initialize default data if needed
        val initializer = DatabaseInitializer(database, get())
        initializer.initializeDefaultData()
        
        AddressBookDatabaseWrapper(database)
    }

    single<AvatarLocalDataSource> { IosAvatarLocalDataSource() }

    single<ClipboardManager> { ClipboardManager() }
}