package com.mangala.wallet.features.chains.antelope.presentation.permission.linkauth

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.LinkAuthUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.MultiSignAccountCheckingUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LinkAuthScreenModel(
    private val accountName: String,
    private val accountPermission: String,
    private val linkAuthUseCase: LinkAuthUseCase,
    private val multiSignAccountCheckingUseCase: MultiSignAccountCheckingUseCase
) : BaseScreenModel() {
    private var _uiState: MutableStateFlow<AuthLinkUIState> =
        MutableStateFlow(AuthLinkUIState.Loading)
    val uiState: StateFlow<AuthLinkUIState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            _uiState.value = AuthLinkUIState.Loading
            multiSignAccountCheckingUseCase.invoke(accountName, accountPermission)?.let {
                if (it) {
                    _uiState.value = AuthLinkUIState.MultiSignAccount
                } else {
                    _uiState.value = AuthLinkUIState.SingleSignAccount
                }
            } ?: run {
                _uiState.value =
                    AuthLinkUIState.Error("check multi sign account failed")
            }
        }
    }

    fun linkAuth(permission: String, contract: String, contractAction: String) {
        screenModelScope.launch {
            linkAuthUseCase.invoke(
                accountName,
                accountPermission,
                contract,
                contractAction,
                permission
            )
                .onSuccess {
                    _uiState.value = AuthLinkUIState.AuthLinkSuccess
                }
                .onFailure {
                    _uiState.value =
                        AuthLinkUIState.AuthLinkFailed(it.message ?: "Link auth failed")
                }
        }
    }

}

sealed class AuthLinkUIState {
    data object Loading : AuthLinkUIState()
    data object MultiSignAccount : AuthLinkUIState()
    data object SingleSignAccount : AuthLinkUIState()
    data object AuthLinkSuccess : AuthLinkUIState()
    data class AuthLinkFailed(val message: String) : AuthLinkUIState()
    data class Error(val message: String) : AuthLinkUIState()
}