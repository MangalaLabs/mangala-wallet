package com.mangala.wallet.features.chains.antelope_base.data.local.actions

import app.cash.paging.PagingSource
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionTraceEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionsEntity
import kotlinx.coroutines.flow.Flow

interface AntelopeActionsLocalDataSource {
    suspend fun insertActions(actions: List<AntelopeActionsEntity>)
    suspend fun insertAction(action: AntelopeActionsEntity)
    fun getActionPagingSource(
        accountName: String,
        blockchainUid: String
    ): PagingSource<Int, AntelopeActionsEntity>

    suspend fun getActionsName(accountName: String) : List<AntelopeActionEntity>

    suspend fun deleteActionsByAccountName(accountName: String);

    suspend fun insertActionsByContract(
        antelopeActionEntities: List<AntelopeActionEntity>
    )
    
    suspend fun clearAllActions()
}