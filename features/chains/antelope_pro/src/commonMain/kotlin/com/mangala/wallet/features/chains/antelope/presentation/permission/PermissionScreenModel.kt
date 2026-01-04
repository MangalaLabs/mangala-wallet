package com.mangala.wallet.features.chains.antelope.presentation.permission

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.Permission
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PermissionScreenModel(
    private val account: String,
    private val currentPermission: String,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountUseCase: GetAccountInfoUseCase
) : BaseScreenModel() {

    private var _uiState: MutableStateFlow<PermissionScreenUiState> =
        MutableStateFlow(PermissionScreenUiState.Loading)
    val uiState: StateFlow<PermissionScreenUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            loadAccount()
        }
    }

    private suspend fun loadAccount() {
        val blockchainType = getSelectedNetworkUseCase().blockchainType

        _uiState.value = PermissionScreenUiState.Loading
        getAccountUseCase(blockchainType, account).let { accountResponse ->
            accountResponse?.let { account ->
                val permissionsName = getPermissionsName(account.permissions)
                if (permissionsName != null) {
                    _uiState.value = PermissionScreenUiState.Success(UiModel(permissionsName))
                } else {
                    _uiState.value =
                        PermissionScreenUiState.Error("check multiSign account or get permNames failed")
                }
            } ?: run {
                _uiState.value = PermissionScreenUiState.Error("Get account failed")
            }
        }
    }

    private fun getPermissionsName(permissions: List<Permission>?): List<String>? {
        return permissions?.let { perms ->
            perms.mapNotNull { it.permName }
        } ?: run {
            null
        }
    }
}

sealed class PermissionScreenUiState {
    data object Loading : PermissionScreenUiState()
    data class Success(val uiModel: UiModel) : PermissionScreenUiState()
    data class Error(val errorMessage: String) : PermissionScreenUiState()
}

data class UiModel(
    val permissionParentsName: List<String>
)