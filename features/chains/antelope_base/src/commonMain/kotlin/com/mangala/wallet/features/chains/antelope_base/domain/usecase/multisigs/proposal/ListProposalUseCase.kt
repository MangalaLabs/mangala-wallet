package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import app.cash.paging.PagingData
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ProposalData
import com.mangala.wallet.features.chains.antelope_base.domain.repository.proposal.ProposalRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow

class ListProposalUseCase(
    private val repository: ProposalRepository,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
) {
    operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType,
        type: ProposalData.Type = ProposalData.Type.PROPOSAL,
        forceRefresh: Boolean
    ) = repository.getPaginatedProposalForAccount(
        accountName = accountName,
        blockchainType = blockchainType,
        type = type,
        forceRefresh = forceRefresh
    )

    suspend operator fun invoke(
        accountName: String,
        type: ProposalData.Type = ProposalData.Type.PROPOSAL,
        forceRefresh: Boolean,
    ): Flow<PagingData<ProposalData>> {
        val blockchainType = getSelectedNetworkUseCase().blockchainType
        return invoke(
            accountName = accountName,
            blockchainType = blockchainType,
            type = type,
            forceRefresh = forceRefresh
        )
    }
}