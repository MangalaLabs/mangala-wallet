package com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions

import app.cash.paging.PagingData
import com.mangala.antelope.base.api.model.EosAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.ActionPagingModel
import com.mangala.wallet.features.chains.antelope_base.domain.repository.actions.ActionsRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

class GetActionsUseCase(private val repository: ActionsRepository) {

    fun getActionsPaginated(
        blockchainType: BlockchainType,
        accountName: String,
        limit: Int,
        sort: String,
        filter: String? = null,
        transferTo: String? = null,
        transferFrom: String? = null,
        after: String? = null,
        before: String? = null
    ): Flow<PagingData<ActionPagingModel>> {
        return repository.getPaginatedActionsForAccount(
            blockchainType = blockchainType,
            accountName = accountName,
            limit = limit,
            sort = sort,
            filter = filter,
            transferTo = transferTo,
            transferFrom = transferFrom,
            after = after,
            before = before
        )
    }

    suspend fun getBuyRamTransferActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>>  {
        return repository.getBuyRamTransferActions(
            accountName,
            blockchainType,
            forceRefresh
        )
    }

    suspend fun getSellRamTransferActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>> {
        return repository.getSellRamTransferActions(
            accountName,
            blockchainType,
            forceRefresh
        )
    }

    suspend fun getRamFeeActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>> {
        return repository.getRamFeeActions(
            accountName,
            blockchainType,
            forceRefresh
        )
    }

    suspend fun getLogRamActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>> {
        return repository.getLogRamActions(
            accountName,
            blockchainType,
            forceRefresh
        )
    }

    suspend fun getLast24hLogRamChangeAction(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>> {
        return repository.getLast24hLogRamChangeAction(
            accountName,
            blockchainType,
            forceRefresh
        )
    }

    companion object {
        const val SKIP = 0
        const val LIMIT = 100
        const val SORT = "desc"
    }
}