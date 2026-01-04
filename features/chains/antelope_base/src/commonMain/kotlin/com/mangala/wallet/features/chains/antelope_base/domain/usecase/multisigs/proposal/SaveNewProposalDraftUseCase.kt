package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.features.chains.antelope_base.domain.repository.proposal.ProposalRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class SaveNewProposalDraftUseCase(
    private val proposalRepository: ProposalRepository
) {
    suspend operator fun invoke(
        proposalId: Long?,
        blockchainType: BlockchainType,
        proposalName: String,
        expirationTimestamp: Long,
        proposerAccountName: String,
        proposerAccountPermission: String,
        actions: List<MultisigAction>,
        approvers: Map<MultisigActionAuthorization, List<MultisigActionAuthorization>>
    ) {
        proposalRepository.saveProposalDraft(
            proposalId,
            blockchainType,
            proposalName,
            expirationTimestamp,
            proposerAccountName,
            proposerAccountPermission,
            actions,
            approvers
        )
    }

    fun isDraftProposalNameExists(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String
    ): Boolean {
        return proposalRepository.isProposalDraftExists(
            blockchainType = blockchainType,
            proposalName = proposalName,
            proposerAccountName = proposerAccountName
        )
    }
}