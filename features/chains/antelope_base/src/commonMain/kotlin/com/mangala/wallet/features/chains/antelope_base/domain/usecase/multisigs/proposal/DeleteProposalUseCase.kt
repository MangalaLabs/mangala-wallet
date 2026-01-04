package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.wallet.features.chains.antelope_base.domain.repository.proposal.ProposalRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class DeleteProposalUseCase(private val proposalRepository: ProposalRepository) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String
    ) {
        proposalRepository.deleteProposal(
            blockchainType,
            proposalName,
            proposerAccountName
        )
    }
}