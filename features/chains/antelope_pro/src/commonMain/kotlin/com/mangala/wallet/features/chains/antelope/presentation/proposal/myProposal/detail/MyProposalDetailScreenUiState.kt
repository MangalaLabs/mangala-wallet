package com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail

import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ProposalDetail

sealed class MyProposalDetailScreenUiState {
    data class Loaded(
        val data: ProposalDetail,
        val error: String? = null,
        val isLoading: Boolean = false,
        val buttonEnabled: Boolean = false,
        val promptConfirmTransaction: Boolean = false,
        val resourceRequiredBreakdown: FeeBreakdown? = null,
        val resourceRequiredTotal: String? = null,
        val inputSectionEnabled: Boolean = false
    ) : MyProposalDetailScreenUiState()
    data class Success(val txHash: String) : MyProposalDetailScreenUiState()
    data class Error(val message: String) : MyProposalDetailScreenUiState()
    data class ExecutedError(val message: String) : MyProposalDetailScreenUiState()
}