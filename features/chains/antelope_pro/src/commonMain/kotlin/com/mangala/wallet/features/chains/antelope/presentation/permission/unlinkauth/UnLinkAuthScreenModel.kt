package com.mangala.wallet.features.chains.antelope.presentation.permission.unlinkauth

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.LinkedAction
import com.mangala.antelope.base.api.model.Permission
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.MultiSignAccountCheckingUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.UnLinkAuthUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UnLinkAuthScreenModel(
    private val accountName: String,
    private val accountPermission: String,
    private val unLinkAuthUseCase: UnLinkAuthUseCase,
    private val getAccountUseCase: GetAccountInfoUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val multiSignAccountCheckingUseCase: MultiSignAccountCheckingUseCase
) : BaseScreenModel() {
    private var _uiState: MutableStateFlow<UnLinkAuthUiState> =
        MutableStateFlow(UnLinkAuthUiState.Loading)
    val uiState: StateFlow<UnLinkAuthUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            _uiState.value = UnLinkAuthUiState.Loading
            multiSignAccountCheckingUseCase.invoke(accountName, accountPermission)?.let {
                if (it) {
                    _uiState.value = UnLinkAuthUiState.MultiSignAccount
                } else {
                    loadAccount()
                }
            } ?: run {
                _uiState.value =
                    UnLinkAuthUiState.Error("check multi sign account failed")
            }
        }
    }

    fun unlinkAuth(contract: String?, action: String?) {
        screenModelScope.launch {
            val contractNotNull = contract ?: run {
                _uiState.value = UnLinkAuthUiState.UnLinkAuthFailed("Contract is null")
                return@launch
            }
            val actionNotNull =
                action ?: run {
                    _uiState.value = UnLinkAuthUiState.UnLinkAuthFailed("Action is null")
                    return@launch
                }

            unLinkAuthUseCase.invoke(
                accountName,
                accountPermission,
                contractNotNull,
                actionNotNull
            )
                .onSuccess { _uiState.value = UnLinkAuthUiState.UnLinkAuthSuccess }
                .onFailure {
                    _uiState.value = UnLinkAuthUiState.UnLinkAuthFailed(
                        it.message ?: "un-link auth failed"
                    )
                }
        }
    }

    private suspend fun loadAccount() {
        _uiState.value = UnLinkAuthUiState.Loading

        val blockchainType = getSelectedNetworkUseCase.invoke().blockchainType

        getAccountUseCase(blockchainType, accountName).let { accountResponse ->
            accountResponse?.let {
                val linkedActions = getLinkAuths(it.permissions)
                _uiState.value = UnLinkAuthUiState.Success(linkedActions)
            } ?: run {
                _uiState.value = UnLinkAuthUiState.Error("Get account failed")
            }
        }
    }

    private fun getLinkAuths(permissions: List<Permission>?): List<LinkedAction> {
        return permissions?.let { permissionList ->
            permissionList.flatMap { permission ->
                permission.linkedActions ?: run { listOf() }
            }
        } ?: run { listOf() }
    }
}

sealed class UnLinkAuthUiState {
    data object Loading : UnLinkAuthUiState()
    data object MultiSignAccount : UnLinkAuthUiState()
    data class Success(val linkedActions: List<LinkedAction>) : UnLinkAuthUiState()
    data object UnLinkAuthSuccess : UnLinkAuthUiState()
    data class UnLinkAuthFailed(val message: String) : UnLinkAuthUiState()
    data class Error(val message: String) : UnLinkAuthUiState()
}

