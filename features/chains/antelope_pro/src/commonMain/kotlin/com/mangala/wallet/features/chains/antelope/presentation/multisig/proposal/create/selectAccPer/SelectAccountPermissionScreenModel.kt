package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.selectAccPer

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SelectAccountPermissionScreenModel(
    private val initialProposerName: String,
    private val initialProposerPermissionName: String,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
) : BaseScreenModel() {
    private val _uiState = MutableStateFlow<SelectAccountPermissionUiState>(
        SelectAccountPermissionUiState.Success(SelectAccountPermissionUiModel())
    )
    val uiState: StateFlow<SelectAccountPermissionUiState> = _uiState.asStateFlow()
    private val _proposer = MutableStateFlow(initialProposerName)
    val proposer: StateFlow<String> = _proposer.asStateFlow()

    private val _permissionExecute = MutableStateFlow(initialProposerPermissionName)
    val permissionExecute: StateFlow<String> = _permissionExecute.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        screenModelScope.launch {
            try {
                val accounts = getAccountsUseCase()
                val accountNames = accounts.map { it.accountName }
                val defaultProposer =
                    initialProposerName.ifBlank { accountNames.firstOrNull().orEmpty() }
                _proposer.value = defaultProposer
                fetchAndUpdateUiState(defaultProposer)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private suspend fun fetchAndUpdateUiState(proposer: String) {
        try {
            _uiState.value = SelectAccountPermissionUiState.Success(
                SelectAccountPermissionUiModel(
                    accountsImported = getAccountsUseCase().map { it.accountName },
                    proposer = proposer,
                    permissionExecute = _permissionExecute.value,
                    permissions = getAccountPermissionsUseCase(_proposer.value).map { it.permissionType.permissionName }
                )
            )
        } catch (e: Exception) {
            handleError(e)
        }
    }

    fun updateProposer(newProposer: String) {
        _proposer.value = newProposer
        screenModelScope.launch { fetchAndUpdateUiState(newProposer) }
    }

    fun updatePermission(newPermission: String) {
        _permissionExecute.value = newPermission
        screenModelScope.launch { fetchAndUpdateUiState(_proposer.value) }

    }

    private fun handleError(e: Exception) {
        _uiState.value = SelectAccountPermissionUiState.Error(e.message ?: "Unknown error")
    }
}
