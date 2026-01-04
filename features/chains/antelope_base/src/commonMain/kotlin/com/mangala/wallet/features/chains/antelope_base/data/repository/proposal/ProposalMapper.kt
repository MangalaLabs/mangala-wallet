package com.mangala.wallet.features.chains.antelope_base.data.repository.proposal

import com.mangala.antelope.base.api.model.msig.ListProposalResponse
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ProposalData
import com.mangala.wallet.features.chains.antelopebase.AntelopeProposalEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.currentTimeInMillis
import com.mangala.wallet.utils.ext.toBoolean
import com.mangala.wallet.utils.ext.toLong
import kotlinx.datetime.Instant

fun ListProposalResponse.Proposal.toProposalEntity(
    blockchainType: BlockchainType,
    accountName: String
): AntelopeProposalEntity = AntelopeProposalEntity(
    proposer = this.proposer ?: "",
    proposal_name = this.proposalName ?: "",
    blockchain_uid = blockchainType.uid,
//    TODO: At the time I code this, the actionsName, created_at and expires_at are not available in the response
    created_at = currentTimeInMillis(),
    expires_at = currentTimeInMillis(),
    actionsName = emptyList(),
    requested_approvals = requestedApprovals?.map { it.actor ?: "" },
    account_name = accountName,
    is_draft = false.toLong()
)

fun AntelopeProposalEntity.toProposalData(): ProposalData = ProposalData(
    proposer = this.proposer,
    proposalName = this.proposal_name,
    blockchainType = BlockchainType.fromUid(this.blockchain_uid),
    createdAt = Instant.fromEpochMilliseconds(this.created_at),
    expiresAt = Instant.fromEpochMilliseconds(this.expires_at),
    actionsName = this.actionsName,
    isDraft = this.is_draft.toBoolean(),
    requestedApprovals = this.requested_approvals
)

fun ProposalData.toAntelopeProposalEntity(accountName: String): AntelopeProposalEntity =
    AntelopeProposalEntity(
        proposer = this.proposer,
        proposal_name = this.proposalName,
        blockchain_uid = this.blockchainType.uid,
        created_at = this.createdAt.toEpochMilliseconds(),
        expires_at = this.expiresAt.toEpochMilliseconds(),
        actionsName = this.actionsName,
        requested_approvals = this.requestedApprovals,
        account_name = accountName,
        is_draft = this.isDraft.toLong()
    )