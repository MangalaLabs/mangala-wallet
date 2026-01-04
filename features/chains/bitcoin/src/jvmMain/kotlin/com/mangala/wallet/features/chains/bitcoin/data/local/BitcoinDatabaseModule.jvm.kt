package com.mangala.wallet.features.chains.bitcoin.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.mangala.wallet.features.chains.bitcoin.database.BitcoinDatabase
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties

internal actual fun bitcoinDatabaseModule() = module {
    single {
        val databaseName = BITCOIN_DATABASE_NAME
        val databasePath =
            Paths.get(System.getProperty("user.home"), "mangalawallet").toAbsolutePath()
        if (!Files.exists(databasePath) || !Files.isDirectory(databasePath)) {
            Files.createDirectory(databasePath)
        }
        val jdbcUrl = "jdbc:sqlite:$databasePath/$databaseName"
        val driver =
            JdbcSqliteDriver(jdbcUrl, Properties(1).apply { put("foreign_keys", "true") }).also {
                BitcoinDatabase.Schema.create(it)
            }

        BitcoinDatabase(driver)
    }
}