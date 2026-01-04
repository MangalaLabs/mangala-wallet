package com.mangala.wallet.features.chains.antelope_base.data.local.proposal

import app.cash.paging.PagingSource
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ProposalData
import com.mangala.wallet.features.chains.antelopebase.AntelopeMsigProposalDraftEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeProposalEntity
import com.mangala.wallet.features.chains.antelopebase.SelectMsigProposalDraftByAccountName
import com.mangala.wallet.model.blockchain.BlockchainType

interface ProposalLocalDataSource {
    fun getProposalPagingSource(
        accountName: String,
        blockchainType: BlockchainType,
        type: ProposalData.Type,
    ): PagingSource<Int, AntelopeProposalEntity>

    suspend fun upsertProposal(proposalEntity: AntelopeProposalEntity)

    suspend fun upsertProposals(proposalEntities: List<AntelopeProposalEntity>)

    suspend fun insertProposal(
        proposalEntity: AntelopeProposalEntity
    )

    suspend fun insertProposals(
        proposalEntities: List<AntelopeProposalEntity>
    )

    suspend fun insertDraftProposal(
        proposalId: Long?,
        proposalEntity: AntelopeProposalEntity,
        draftEntity: AntelopeMsigProposalDraftEntity
    )

    suspend fun getDraftProposal(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String,
    ): SelectMsigProposalDraftByAccountName?

    fun getDraftProposalExists(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String,
    ): Boolean

    suspend fun deleteProposal(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String,
    )

    suspend fun deleteDraftProposal(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String,
    )

    suspend fun invalidateProposalCache(
        accountName: String,
        blockchainType: BlockchainType,
        type: ProposalData.Type,
    )
}