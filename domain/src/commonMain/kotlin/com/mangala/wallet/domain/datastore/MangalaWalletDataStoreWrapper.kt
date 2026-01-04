package com.mangala.wallet.domain.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.mangala.wallet.database.MangalaWalletDatabase
import okio.Path.Companion.toPath


class MangalaWalletDataStoreWrapper(val instance: DataStore<Preferences>)

internal const val dataStoreFileName = "mangala.preferences_pb"

fun getDataStore(producePath: () -> String): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })

