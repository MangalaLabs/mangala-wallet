package com.mangala.eticket.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.mangala.eticket.data.local.ETicketDatabaseWrapper
import com.mangala.eticket.database.ETicketDatabase
import org.koin.dsl.module

actual fun eTicketDatabaseModule() = module {
    single {
        val driver = AndroidSqliteDriver(ETicketDatabase.Schema, get(), "eticket.db")
        ETicketDatabase(driver)
    }

    single {
        ETicketDatabaseWrapper(get<ETicketDatabase>()) // Use the provided ETicketDatabase instance
    }
}