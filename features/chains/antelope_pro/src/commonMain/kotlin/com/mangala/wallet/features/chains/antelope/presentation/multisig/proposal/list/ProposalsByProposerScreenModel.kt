package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.GetTableRowsMultisigsRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AccountName.MULTISIG
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.GetTableRowsMultisigsProposalsUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProposalsByProposerScreenModel(
    val proposer: String,
    private val getTableRowsMultisigsProposalsUseCase: GetTableRowsMultisigsProposalsUseCase,
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<ProposalsByProposerScreenUiState> =
        MutableStateFlow(ProposalsByProposerScreenUiState.Loading)
    val uiState: StateFlow<ProposalsByProposerScreenUiState> = _uiState.asStateFlow()

    init {
        showProposals(proposer)
    }

    private fun showProposals(scope: String){

        screenModelScope.launch {
            val proposalTable = getTableRowsMultisigsProposalsUseCase.invoke(
                GetTableRowsMultisigsRequest(
                    code = MULTISIG,
                    table = "approvals2",
                    lowerBound = "",
                    upperBound = "",
                    encodeType = "",
                    keyType = "",
                    indexPosition = "1",
                    limit = 100,
                    scope = scope,
                    json = true,
                    reverse = false,
                    showPayer = false
                )
            )

            if (proposalTable != null) {
                _uiState.value = ProposalsByProposerScreenUiState.Success(
                    proposalTable,
                    scope
                )
            } else {
                _uiState.value = ProposalsByProposerScreenUiState.Error("Error fetching table rows")
            }
        }

    }

}