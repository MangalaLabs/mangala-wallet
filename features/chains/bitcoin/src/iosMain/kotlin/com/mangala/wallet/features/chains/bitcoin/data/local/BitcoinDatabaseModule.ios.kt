package com.mangala.wallet.features.chains.bitcoin.data.local

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import com.mangala.wallet.features.chains.bitcoin.database.BitcoinDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun bitcoinDatabaseModule() = module {
    single {
        val driver = NativeSqliteDriver(
            schema = BitcoinDatabase.Schema,
            name = BITCOIN_DATABASE_NAME,
            onConfiguration = { configuration ->
                configuration.copy(
                    extendedConfig = DatabaseConfiguration.Extended(
                        foreignKeyConstraints = true
                    )
                )
            }
        )
        BitcoinDatabase(driver = driver)
    }
}