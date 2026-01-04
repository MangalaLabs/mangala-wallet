package com.mangala.wallet.domain.di

import com.mangala.wallet.domain.datastore.MangalaWalletDataStoreWrapper
import com.mangala.wallet.domain.datastore.dataStoreFileName
import com.mangala.wallet.domain.datastore.getDataStore
import org.koin.dsl.module
import java.nio.file.Paths

actual fun dataStoreModule() = module {
    single<MangalaWalletDataStoreWrapper> {
        makeDataStore()
    }
}

fun makeDataStore(): MangalaWalletDataStoreWrapper {
    val dataStore = getDataStore(
        producePath = {
            val dataStorePath =
                Paths.get(System.getProperty("user.home"), dataStoreFileName).toString()
            dataStorePath
        }
    )
    return MangalaWalletDataStoreWrapper(dataStore)
}