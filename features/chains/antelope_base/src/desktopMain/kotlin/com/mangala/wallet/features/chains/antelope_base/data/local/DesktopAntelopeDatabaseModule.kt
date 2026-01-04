package com.mangala.wallet.features.chains.antelope_base.data.local

import org.koin.dsl.module

actual fun antelopeDatabaseModule() = module {
//    single {
//        val databaseName = ANTELOPE_DATABASE_NAME
//        val databasePath = Paths.get(System.getProperty("user.home"), "mangalawallet").toAbsolutePath()
//        if (!Files.exists(databasePath) || !Files.isDirectory(databasePath)) {
//            Files.createDirectory(databasePath)
//        }
//        val jdbcUrl = "jdbc:sqlite:${databasePath.toString()}/$databaseName"
//        val driver = JdbcSqliteDriver(jdbcUrl, Properties(1).apply { put("foreign_keys", "true") }).also { AntelopeDatabase.Schema.create(it) }
//        AntelopeDatabaseWrapper(AntelopeDatabase(driver))
//    }
//
//    single<SecureStorageWrapper> {
//        SecureStorageWrapperImpl()
//    }
}