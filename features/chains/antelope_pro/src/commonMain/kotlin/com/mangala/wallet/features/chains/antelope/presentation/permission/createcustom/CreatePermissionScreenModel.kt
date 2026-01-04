package com.mangala.wallet.features.chains.antelope.presentation.permission.createcustom

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.AccountAuthAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.AccountAuthWait
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.AccountKey
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.MultiSignAccountCheckingUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.UpdateAccountPermissionUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreatePermissionScreenModel(
    private val accountName: String,
    private val accountPermission: String,
    private val updateAccountPermissionUseCase: UpdateAccountPermissionUseCase,
    private val multiSignAccountCheckingUseCase: MultiSignAccountCheckingUseCase
) : BaseScreenModel() {

    private var _uiState: MutableStateFlow<CreatePermissionScreenUiState> =
        MutableStateFlow(CreatePermissionScreenUiState.Loading)
    val uiState: StateFlow<CreatePermissionScreenUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            _uiState.value = CreatePermissionScreenUiState.Loading
            multiSignAccountCheckingUseCase.invoke(accountName, accountPermission)?.let {
                if (it) {
                    _uiState.value = CreatePermissionScreenUiState.MultiSignAccount
                } else {
                    _uiState.value = CreatePermissionScreenUiState.SingleAccount
                }
            } ?: run {
                _uiState.value =
                    CreatePermissionScreenUiState.Error("check multi sign account failed")
            }
        }
    }

    fun onSubmitCreatePermission(
        permission: String,
        permissionParent: String,
        threshold: Int,
        keys: List<AccountKey>,
        accounts: List<AccountAuthAccount>,
        waits: List<AccountAuthWait>
    ) {
        screenModelScope.launch {
            createPermission(permission, permissionParent, threshold, keys, accounts, waits)
        }
    }

    private suspend fun createPermission(
        permission: String,
        permissionParent: String,
        threshold: Int,
        keys: List<AccountKey>,
        accounts: List<AccountAuthAccount>,
        waits: List<AccountAuthWait>
    ) {
        updateAccountPermissionUseCase.invoke(
            accountName,
            accountPermission,
            permission,
            permissionParent,
            threshold,
            keys,
            accounts,
            waits
        )
            .onSuccess {
                _uiState.value = CreatePermissionScreenUiState.CreatePermissionSuccess
            }
            .onFailure {
                _uiState.value = CreatePermissionScreenUiState.CreatePermissionFailed(
                    it.message ?: "create permission failed"
                )
            }
    }
}

sealed class CreatePermissionScreenUiState {
    data object Loading : CreatePermissionScreenUiState()
    data object MultiSignAccount : CreatePermissionScreenUiState()
    data object SingleAccount : CreatePermissionScreenUiState()
    data object CreatePermissionSuccess : CreatePermissionScreenUiState()
    data class CreatePermissionFailed(val message: String) : CreatePermissionScreenUiState()
    data class Error(val message: String) : CreatePermissionScreenUiState()
}