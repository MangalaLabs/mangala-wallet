package com.mangala.wallet.features.addressbook.di

import androidx.sqlite.db.SupportSQLiteDatabase
import org.koin.dsl.module
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.local.DatabaseInitializer
import com.mangala.wallet.features.addressbook.data.local.avatar.AndroidAvatarLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.avatar.AvatarLocalDataSource
import com.mangala.wallet.features.addressbook.database.AddressBookDatabase
import com.mangala.wallet.features.addressbook.utils.avatar.AndroidAvatarImageProcessor
import com.mangala.wallet.features.addressbook.utils.avatar.AvatarImageProcessor
import com.mangala.wallet.features.addressbook.utils.clipboard.ClipboardManager
import org.koin.android.ext.koin.androidContext

internal actual fun addressBookPlatformSpecificModule() = module {
    single {
        val driver = AndroidSqliteDriver(
            schema = AddressBookDatabase.Schema,
            context = get(),
            name = ADDRESS_BOOK_DATABASE_NAME,
            callback = object : AndroidSqliteDriver.Callback(AddressBookDatabase.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
        val database = AddressBookDatabase(driver = driver)
        
        // Initialize default data if needed
        val initializer = DatabaseInitializer(database, get())
        initializer.initializeDefaultData()
        
        AddressBookDatabaseWrapper(database)
    }

    single<AvatarImageProcessor> {
        AndroidAvatarImageProcessor(androidContext())
    }

    single<AvatarLocalDataSource> { AndroidAvatarLocalDataSource(androidContext()) }

    single<ClipboardManager> { ClipboardManager(get()) }
}