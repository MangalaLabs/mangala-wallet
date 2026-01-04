package com.mangala.wallet.domain.di

import com.mangala.wallet.domain.datastore.MangalaWalletDataStoreWrapper
import com.mangala.wallet.domain.datastore.dataStoreFileName
import com.mangala.wallet.domain.datastore.getDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun dataStoreModule() = module {
    single<MangalaWalletDataStoreWrapper> {
        val dataStore = getDataStore(
            producePath = {
                val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )!!.path
                requireNotNull(documentDirectory) + "/$dataStoreFileName"
            }
        )

        MangalaWalletDataStoreWrapper(dataStore)
    }
}
