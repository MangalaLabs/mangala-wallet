package com.mangala.wallet.domain.di

import android.content.Context
import com.mangala.wallet.domain.datastore.MangalaWalletDataStoreWrapper
import com.mangala.wallet.domain.datastore.dataStoreFileName
import com.mangala.wallet.domain.datastore.getDataStore
import org.koin.dsl.module

actual fun dataStoreModule() = module {
    single<MangalaWalletDataStoreWrapper> {
        makeDataStore(get())
    }
}

fun makeDataStore(context: Context): MangalaWalletDataStoreWrapper {
    val dataStore = getDataStore(
        producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
    )
    return MangalaWalletDataStoreWrapper(dataStore)
}