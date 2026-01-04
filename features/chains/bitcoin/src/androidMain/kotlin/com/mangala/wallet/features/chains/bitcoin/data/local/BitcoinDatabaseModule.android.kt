package com.mangala.wallet.features.chains.bitcoin.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.dsl.module
import com.mangala.wallet.features.chains.bitcoin.database.BitcoinDatabase

internal actual fun bitcoinDatabaseModule() = module {
    single {
        val driver = AndroidSqliteDriver(
            schema = BitcoinDatabase.Schema,
            context = get(),
            name = BITCOIN_DATABASE_NAME,
            callback = object : AndroidSqliteDriver.Callback(BitcoinDatabase.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
        BitcoinDatabase(
            driver = driver
        )
    }
}