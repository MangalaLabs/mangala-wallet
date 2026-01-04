package com.mangala.eticket.di

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.mangala.eticket.data.local.ETicketDatabaseWrapper
import com.mangala.eticket.database.ETicketDatabase
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties

actual fun eTicketDatabaseModule() = module {
    single {
        val databaseName = "eticket"
        val databasePath = Paths.get(System.getProperty("user.home"), "eticket").toAbsolutePath()
        if (!Files.exists(databasePath) || !Files.isDirectory(databasePath)) {
            Files.createDirectory(databasePath)
        }
        val jdbcUrl = "jdbc:sqlite:${databasePath.toString()}/$databaseName.db"
        val driver = JdbcSqliteDriver(jdbcUrl, Properties(1).apply { put("foreign_keys", "true") }).also { ETicketDatabase.Schema.create(it) }
        ETicketDatabaseWrapper(ETicketDatabase(driver))
    }
}