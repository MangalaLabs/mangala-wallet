package com.mangala.wallet.features.chains.antelope_base.domain.repository.actions

import app.cash.paging.PagingData
import com.mangala.antelope.base.api.model.EosAction
import com.mangala.antelope.base.api.model.GetActionsResponse
import com.mangala.wallet.features.chains.antelope_base.domain.model.ActionPagingModel
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import kotlinx.coroutines.flow.Flow

interface ActionsRepository {
    suspend fun getActions(
        blockchainType: BlockchainType,
        accountName: String,
        filter: String?,
        skip: Int?,
        limit: Int?,
        sort: String,
        transferTo: String?,
        transferFrom: String?,
        after: String?,
        before: String?
    ): ApiResponse<GetActionsResponse, CustomError>

    fun getPaginatedActionsForAccount(
        blockchainType: BlockchainType,
        accountName: String,
        limit: Int,
        sort: String,
        filter: String?,
        transferTo: String?,
        transferFrom: String?,
        after: String?,
        before: String?
    ): Flow<PagingData<ActionPagingModel>>


    suspend fun getActionName(
        accountName: String,
        actionName: String,
        lastAccountCodeUpdatedTimestamp: Long,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Result<List<AntelopeActionAbi>>

    suspend fun getBuyRamTransferActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>>

    suspend fun getSellRamTransferActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>>

    suspend fun getRamFeeActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>>

    suspend fun getLogRamActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>>

    suspend fun getLast24hLogRamChangeAction(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>>
}