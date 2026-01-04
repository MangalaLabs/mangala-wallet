package com.mangala.wallet.features.chains.antelope_base.data.local.actions.actiontrace

import com.mangala.wallet.features.chains.antelope_base.data.repository.actions.mapper.AntelopeActionTraceTransactionType
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionTraceCacheMetadataEntity

interface AntelopeActionTraceCacheMetadataLocalDataSource {
    suspend fun insertActionTraceCacheMetadata(metadata: AntelopeActionTraceCacheMetadataEntity)
    suspend fun getActionTraceCacheMetadata(
        accountName: String,
        blockchainUid: String,
        transactionType: AntelopeActionTraceTransactionType
    ): AntelopeActionTraceCacheMetadataEntity?
}