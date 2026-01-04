package com.mangala.wallet.features.chains.antelope.presentation.proposal

import androidx.paging.cachedIn
import app.cash.paging.PagingData
import app.cash.paging.map
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ProposalData
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.ListProposalUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProposalScreenModal(
    private val listProposalUseCase: ListProposalUseCase,
    private val getAntelopeAccountsUseCase: GetAccountsUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
) : BaseScreenModel() {
    private val _filterState = MutableStateFlow(0)
    val filterState = _filterState.asStateFlow()

    private val _availableAccountNames = MutableStateFlow(emptyList<String>())
    val availableAccountNames = _availableAccountNames.asStateFlow()

    private val _selectedAccountName = MutableStateFlow("")
    val selectedAccountName = _selectedAccountName.asStateFlow()

    private val _blockchainType = MutableStateFlow<BlockchainType?>(null)

    init {
        screenModelScope.launch {
            val blockchainType = getSelectedNetworkUseCase().blockchainType
            _blockchainType.update { blockchainType }
            getAntelopeAccountsUseCase.invokeFlow(blockchainType = blockchainType)
                .collectLatest { accounts ->
                    _availableAccountNames.update { accounts.map { it.accountName } }
                    if (accounts.isNotEmpty() && selectedAccountName.value.isEmpty()) {
                        _selectedAccountName.update { accounts.first().accountName }
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val myProposalPaginated: Flow<PagingData<ProposalUiModel>> = selectedAccountName
        .combine(_blockchainType) { accountName, blockchainType ->
            blockchainType to accountName
        }
        .flatMapLatest { (blockchainType, accountName) ->
            if (blockchainType == null || accountName.isEmpty())
                flow { emit(PagingData.empty()) }
            else
                listProposalUseCase(
                    accountName = accountName,
                    blockchainType = blockchainType,
                    type = ProposalData.Type.PROPOSAL,
                    forceRefresh = false
                ).map { pagingData ->
                    pagingData.map { responseData ->
                        ProposalUiModel(
                            proposalName = responseData.proposalName,
                            state = if (responseData.isDraft) ProposalUiModel.State.Draft else ProposalUiModel.State.Pending,
                            action = responseData.actionsName.firstOrNull() ?: "",
                            submitter = accountName,
                            expiredDate = responseData.expiresAt,
                        )
                    }
                }
        }
        .cachedIn(screenModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val approvalPaginated: Flow<PagingData<ProposalUiModel>> = selectedAccountName
        .combine(_blockchainType) { accountName, blockchainType ->
            blockchainType to accountName
        }
        .flatMapLatest { (blockchainType, accountName) ->
            if (blockchainType == null || accountName.isEmpty())
                flow { emit(PagingData.empty()) }
            else
                listProposalUseCase(
                    accountName = accountName,
                    blockchainType = blockchainType,
                    type = ProposalData.Type.APPROVAL,
                    forceRefresh = false
                ).map { pagingData ->
                    pagingData.map { responseData ->
                        ProposalUiModel(
                            proposalName = responseData.proposalName,
                            state = if (responseData.isDraft) ProposalUiModel.State.Draft else ProposalUiModel.State.Pending,
                            action = responseData.actionsName.firstOrNull() ?: "",
                            submitter = responseData.proposer,
                            expiredDate = responseData.expiresAt,
                        )
                    }
                }
        }
        .cachedIn(screenModelScope)

    fun onSelectFilterState(filterState: Int) {
        _filterState.value = filterState
    }

    fun onSelectAccountName(accountName: String) {
        _selectedAccountName.value = accountName
    }
}