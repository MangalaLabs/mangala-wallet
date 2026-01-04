package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigProposal
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ProposalData
import com.mangala.wallet.features.chains.antelope_base.domain.repository.proposal.ProposalRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class UpdateProposalOnSubmitSuccessUseCase(
    private val proposalRepository: ProposalRepository
) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        draftProposal: MultisigProposal?,
        proposalName: String,
        proposer: String,
        expiresAt: Long,
        actions: List<MultisigAction>
    ) {
        draftProposal?.let {
            proposalRepository.deleteProposal(
                blockchainType,
                it.proposalName,
                it.proposerName
            )
        }
        proposalRepository.updateProposal(
            ProposalData(
                proposalName = proposalName,
                proposer = proposer,
                blockchainType = blockchainType,
                createdAt = Clock.System.now(),
                expiresAt = Instant.fromEpochMilliseconds(expiresAt),
                actionsName = actions.map { it.actionName },
                isDraft = false,
                requestedApprovals = null
            )
        )
    }
}