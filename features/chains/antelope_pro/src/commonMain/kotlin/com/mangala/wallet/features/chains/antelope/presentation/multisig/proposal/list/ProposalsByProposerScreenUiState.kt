package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.list

import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse

sealed interface ProposalsByProposerScreenUiState {
    data object Loading : ProposalsByProposerScreenUiState


    data class Success(val data: GetMultisigProposalTableRowResponse, val scope: String) :
        ProposalsByProposerScreenUiState

    data class Error(val message: String) : ProposalsByProposerScreenUiState
}