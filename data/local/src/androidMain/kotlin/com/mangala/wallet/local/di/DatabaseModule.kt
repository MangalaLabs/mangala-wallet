package com.mangala.wallet.local.di

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.mangala.wallet.database.MangalaWalletDatabase
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.local.cache.metadataTargetCacheEntityAdapter
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperImpl
import com.mangala.wallet.mokoresources.MR
import commangalawalletdatabase.TransactionMetadataEntity
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

actual fun databaseModule() = module(createdAtStart = true) {
    single {
        val fileName = "mangalawallet.db"
        val context = androidContext()

        val database: File = context.getDatabasePath(fileName)

        if (!database.exists()) {
            val inputStream = context.resources.openRawResource(MR.files.mangalawallet_db.rawResId)
            val outputStream = FileOutputStream(database.absolutePath)

            inputStream.use { input: InputStream ->
                outputStream.use { output: FileOutputStream ->
                    input.copyTo(output)
                }
            }
        }

        val driver = AndroidSqliteDriver(
            MangalaWalletDatabase.Schema,
            context,
            fileName,
            callback = object : AndroidSqliteDriver.Callback(MangalaWalletDatabase.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
        MangalaWalletDatabaseWrapper(MangalaWalletDatabase(driver))
    }

    single<SecureStorageWrapper> {
        SecureStorageWrapperImpl(androidContext())
    }
}