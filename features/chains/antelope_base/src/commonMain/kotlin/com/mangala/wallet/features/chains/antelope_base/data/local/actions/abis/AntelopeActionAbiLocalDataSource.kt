package com.mangala.wallet.features.chains.antelope_base.data.local.actions.abis

import com.mangala.wallet.features.chains.antelopebase.AntelopeActionAbiEntity
import kotlinx.coroutines.flow.Flow

interface AntelopeActionAbiLocalDataSource {
    suspend fun insertActionsAbi(actions: List<AntelopeActionAbiEntity>)

    suspend fun insertActionAbi(action: AntelopeActionAbiEntity)

    suspend fun getActionAbiByAccountName(accountName: String): List<AntelopeActionAbiEntity>

    fun getActionAbiByAccountNameFlow(accountName: String):
            Flow<List<AntelopeActionAbiEntity>>

    suspend fun deleteActionAbiByAccountName(accountName: String)

    suspend fun getActionAbiByAccountNameAndActionName(
        accountName: String,
        actionName: String
    ): List<AntelopeActionAbiEntity>
    
    suspend fun clearAllActionAbi()
}