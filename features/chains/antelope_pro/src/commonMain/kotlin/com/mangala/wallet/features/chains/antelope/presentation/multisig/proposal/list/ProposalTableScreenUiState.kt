package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.list

import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse
import com.mangala.antelope.base.api.model.GetTableByScopeResponse
import com.mangala.antelope.base.api.model.GetTableRowsResponse

sealed interface ProposalTableScreenUiState {
    data object Loading : ProposalTableScreenUiState

    data class Success(
        val data: GetTableByScopeResponse,
        val tableProposal: GetMultisigProposalTableRowResponse,
    ) : ProposalTableScreenUiState

    data class LoadProposalRows(val data: GetMultisigProposalTableRowResponse, val scope: String) :
        ProposalTableScreenUiState

    data class Error(val message: String) : ProposalTableScreenUiState
}