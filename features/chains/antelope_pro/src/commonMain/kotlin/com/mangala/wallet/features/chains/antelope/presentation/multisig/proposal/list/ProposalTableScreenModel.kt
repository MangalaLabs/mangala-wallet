package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse
import com.mangala.antelope.base.api.model.GetTableByScopeRequest
import com.mangala.antelope.base.api.model.GetTableRowsMultisigsRequest
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.GetTableByScopeUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.GetTableRowsMultisigsProposalsUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProposalTableScreenModel(
    private val getTableByScopeUseCase: GetTableByScopeUseCase,
    private val getTableRowsMultisigsProposalsUseCase: GetTableRowsMultisigsProposalsUseCase,
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<ProposalTableScreenUiState> =
        MutableStateFlow(ProposalTableScreenUiState.Loading)
    val uiState: StateFlow<ProposalTableScreenUiState> = _uiState.asStateFlow()

    private var nextPageKey: String? = ""

    init {
        loadData()
    }

    fun getTableRows() {
        screenModelScope.launch {
            val response = getTableByScopeUseCase.invoke(
                GetTableByScopeRequest(
                    code = "eosio.msig",
                    table = "proposal",
                    lowerBound = nextPageKey.orEmpty(),
                    upperBound = "",
                    limit = 10
                )
            )

            nextPageKey = response?.more
            if (response != null) {
                val currentData = (_uiState.value as ProposalTableScreenUiState.Success).data
                val updatedRows = currentData.rows.orEmpty() + (response.rows.orEmpty())
                val updatedData = currentData.copy(rows = updatedRows)

                _uiState.value = ProposalTableScreenUiState.Success(
                    updatedData,
                    GetMultisigProposalTableRowResponse(rows = listOf())
                )
            } else {
                _uiState.value = ProposalTableScreenUiState.Error("Error fetching table rows")
            }
        }
    }

    private fun loadData() {
        screenModelScope.launch {
            _uiState.value = ProposalTableScreenUiState.Loading
            val response = getTableByScopeUseCase.invoke(
                GetTableByScopeRequest(
                    code = "eosio.msig",
                    table = "proposal",
                    lowerBound = "",
                    upperBound = "",
                    limit = 10
                )
            )
            println("response: $response")
            if (response != null) {
                _uiState.value = ProposalTableScreenUiState.Success(
                    response,
                    GetMultisigProposalTableRowResponse(rows = listOf())
                )
            } else {
                _uiState.value = ProposalTableScreenUiState.Error("Error fetching table rows")
            }
        }
    }


    fun showProposals(scope: String): List<GetMultisigProposalTableRowResponse.ProposalRow> {
        val proposals = mutableListOf<GetMultisigProposalTableRowResponse.ProposalRow>()

        screenModelScope.launch {
            val proposalTable = getTableRowsMultisigsProposalsUseCase.invoke(
                GetTableRowsMultisigsRequest(
                    code = "eosio.msig",
                    table = "approvals2",
                    lowerBound = "",
                    upperBound = "",
                    encodeType = "",
                    keyType = "",
                    indexPosition = "1",
                    limit = 10,
                    scope = scope,
                    json = true,
                    reverse = false,
                    showPayer = false
                )
            )

            println("proposalTable: $proposalTable")

            if (proposalTable != null) {
                _uiState.value = ProposalTableScreenUiState.LoadProposalRows(
                    proposalTable,
                    scope
                )
            } else {
                _uiState.value = ProposalTableScreenUiState.Error("Error fetching table rows")
            }
        }


        return proposals
    }


}