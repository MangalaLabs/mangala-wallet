package com.mangala.wallet.features.chains.antelope_base.data.local.actions.actiontrace

import com.mangala.wallet.features.chains.antelope_base.data.repository.actions.mapper.AntelopeActionTraceTransactionType
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionTraceEntity
import kotlinx.coroutines.flow.Flow

interface AntelopeActionTraceLocalDataSource {
    fun getAntelopeActionTrace(
        accountName: String,
        blockchainUid: String,
        actionTraceTransactionType: AntelopeActionTraceTransactionType
    ): Flow<List<AntelopeActionTraceEntity>>

    suspend fun insertActionTraces(actions: List<AntelopeActionTraceEntity>)
}