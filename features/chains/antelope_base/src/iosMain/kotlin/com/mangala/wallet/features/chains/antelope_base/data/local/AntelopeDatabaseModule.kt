package com.mangala.wallet.features.chains.antelope_base.data.local

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import com.mangala.wallet.features.chains.antelope_base.database.AntelopeDatabase
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionsEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeProposalEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeRemoteKey
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperImpl
import org.koin.dsl.module

actual fun antelopeDatabaseModule() = module {
    single {
        val driver = NativeSqliteDriver(
            AntelopeDatabase.Schema,
            ANTELOPE_DATABASE_NAME,
            onConfiguration = { configuration ->
                configuration.copy(
                    extendedConfig = DatabaseConfiguration.Extended(
                        foreignKeyConstraints = true
                    )
                )
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
        SecureStorageWrapperImpl()
    }
}