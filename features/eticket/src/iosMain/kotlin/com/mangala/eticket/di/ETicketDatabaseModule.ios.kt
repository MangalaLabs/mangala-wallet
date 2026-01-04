package com.mangala.eticket.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.mangala.eticket.data.local.ETicketDatabaseWrapper
import com.mangala.eticket.database.ETicketDatabase
import com.mangala.wallet.database.MangalaWalletDatabase
import org.koin.dsl.module

actual fun eTicketDatabaseModule() = module {
    single {
        val driver = NativeSqliteDriver(MangalaWalletDatabase.Schema, "eticket.db")
        ETicketDatabaseWrapper(ETicketDatabase(driver))
    }
}