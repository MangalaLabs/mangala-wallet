package com.mangala.wallet.features.chains.antelope_base.domain.repository.proposal

import app.cash.paging.PagingData
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigProposal
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ProposalData
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow

interface ProposalRepository {
    fun getPaginatedProposalForAccount(
        accountName: String,
        blockchainType: BlockchainType,
        type: ProposalData.Type,
        forceRefresh: Boolean
    ): Flow<PagingData<ProposalData>>

    suspend fun saveProposalDraft(
        proposalId: Long?,
        blockchainType: BlockchainType,
        proposalName: String,
        expirationTimestamp: Long,
        proposerAccountName: String,
        proposerAccountPermission: String,
        actions: List<MultisigAction>,
        approvers: Map<MultisigActionAuthorization, List<MultisigActionAuthorization>>
    )
    suspend fun getProposalDraft(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String,
    ): MultisigProposal?
    fun isProposalDraftExists(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String,
    ): Boolean
    suspend fun updateProposal(proposalData: ProposalData)
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
}