package com.mangala.wallet.features.chains.antelope_base.data.local.actions.actiontrace

import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelope_base.data.repository.actions.mapper.AntelopeActionTraceTransactionType
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionTraceCacheMetadataEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class AntelopeActionTraceCacheMetadataLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AntelopeActionTraceCacheMetadataLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun insertActionTraceCacheMetadata(metadata: AntelopeActionTraceCacheMetadataEntity) = withContext(ioDispatcher) {
        dbQuery.insertAntelopeActionTraceCacheMetadata(
            account_name = metadata.account_name,
            blockchain_uid = metadata.blockchain_uid,
            transaction_type = metadata.transaction_type,
            last_updated = metadata.last_updated,
            lookup_before_timestamp = metadata.lookup_before_timestamp,
            past_data_load_finished = metadata.past_data_load_finished
        )
    }

    override suspend fun getActionTraceCacheMetadata(
        accountName: String,
        blockchainUid: String,
        transactionType: AntelopeActionTraceTransactionType
    ): AntelopeActionTraceCacheMetadataEntity? = withContext(ioDispatcher) {
        return@withContext dbQuery.selectAntelopeActionTraceCacheMetadata(
            accountName,
            blockchainUid,
            transactionType.name
        ).executeAsOneOrNull()
    }
}