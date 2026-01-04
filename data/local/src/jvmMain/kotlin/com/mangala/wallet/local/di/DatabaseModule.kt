package com.mangala.wallet.local.di

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.mangala.wallet.database.MangalaWalletDatabase
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperImpl
import com.mangala.wallet.mokoresources.MR
import java.nio.file.Files
import java.nio.file.Paths
import org.koin.dsl.module
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Properties

actual fun databaseModule() = module(createdAtStart = true) {
    single {
        val databaseName = "mangala_wallet"
        val databasePath = Paths.get(System.getProperty("user.home"), "mangalawallet").toAbsolutePath()
        if (!Files.exists(databasePath) || !Files.isDirectory(databasePath)) {
            Files.createDirectory(databasePath)
            Files.copy(Paths.get(MR.files.mangalawallet_db.filePath), databasePath)
        }
        val jdbcUrl = "jdbc:sqlite:${databasePath.toString()}/$databaseName.db"
        val driver = JdbcSqliteDriver(jdbcUrl, Properties(1).apply { put("foreign_keys", "true") }).also { MangalaWalletDatabase.Schema.create(it) }
        MangalaWalletDatabaseWrapper(MangalaWalletDatabase(driver))
    }

    single<SecureStorageWrapper> {
        SecureStorageWrapperImpl()
    }
}