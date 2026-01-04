package com.mangala.wallet.features.chains.antelope_base.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.mangala.wallet.features.chains.antelope_base.database.AntelopeDatabase
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionsEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeProposalEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeRemoteKey
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun antelopeDatabaseModule() = module {
    single {
        val driver = AndroidSqliteDriver(
            schema = AntelopeDatabase.Schema,
            context = get(),
            name = ANTELOPE_DATABASE_NAME,
            callback = object : AndroidSqliteDriver.Callback(AntelopeDatabase.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
        AntelopeDatabaseWrapper(
            AntelopeDatabase(
                driver = driver,
                AntelopeActionsEntityAdapter = AntelopeActionsEntity.Adapter(
                    actionTraceAdapter = listOfStringsAdapter,
                    accountActionSeqAdapter = listOfLongsAdapter,
                    blockNumAdapter = listOfLongsAdapter,
                ),
                AntelopeProposalEntityAdapter = AntelopeProposalEntity.Adapter(
                    actionsNameAdapter = listOfStringsAdapter,
                    requested_approvalsAdapter = listOfStringsAdapter,
                ),
                AntelopeRemoteKeyAdapter = AntelopeRemoteKey.Adapter(
                    target_cache_entityAdapter = enumAntelopeRemoteKeyTargetEntity,
                )
            )
        )
    }

    single<SecureStorageWrapper> {
        SecureStorageWrapperImpl(androidContext())
    }
}