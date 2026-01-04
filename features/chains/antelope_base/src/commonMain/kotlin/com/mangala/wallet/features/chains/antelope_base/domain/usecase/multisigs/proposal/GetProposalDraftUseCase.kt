package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigProposal
import com.mangala.wallet.features.chains.antelope_base.domain.repository.proposal.ProposalRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class GetProposalDraftUseCase(
    private val proposalRepository: ProposalRepository
) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String,
    ): MultisigProposal? {
        return proposalRepository.getProposalDraft(
            blockchainType,
            proposalName,
            proposerAccountName
        )
    }
}