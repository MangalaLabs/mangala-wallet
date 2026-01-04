package com.mangala.wallet.features.chains.antelope.presentation.permission.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.Permission
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.DeleteAccountPermissionUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PermissionListScreenModel(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountUseCase: GetAccountInfoUseCase,
    private val accountName: String,
    private val accountPermission: String,
    private val deleteAccountPermissionUseCase: DeleteAccountPermissionUseCase
) : BaseScreenModel() {
    private val _uiState: MutableStateFlow<PermissionListUiState> =
        MutableStateFlow(PermissionListUiState.Loading)
    val uiState: StateFlow<PermissionListUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            loadAccount()
        }
    }

    fun onClickLoadAccount() {
        screenModelScope.launch {
            loadAccount()
        }
    }

    fun onDelete(permission: String?) {
        screenModelScope.launch {
            permission?.let {
                deleteAccountPermissionUseCase.invoke(
                    accountName,
                    accountPermission,
                    permission
                )
                    .onSuccess {
                        _uiState.value = PermissionListUiState.DeletePermissionSuccess(permission)
                    }
                    .onFailure {
                        _uiState.value = PermissionListUiState.DeletePermissionFailed(
                            permission,
                            it.message ?: "Delete permission failed"
                        )
                    }
            }
        }
    }

    private suspend fun loadAccount() {
        _uiState.value = PermissionListUiState.Loading
        val blockchainType = getSelectedNetworkUseCase.invoke().blockchainType
        getAccountUseCase(blockchainType, accountName).let { accountResponse ->
            accountResponse?.let {

                _uiState.value = PermissionListUiState.Success(
                    UiModel(
                        it.permissions,
                        getValidPermissionsForDelete(it.permissions ?: listOf(), accountPermission)
                    )
                )

            } ?: run {
                _uiState.value = PermissionListUiState.Error("Get account failed")
            }
        }
    }

    private fun getValidPermissionsForDelete(
        permissions: List<Permission>,
        accountPermission: String
    ): Map<String?, Boolean> {
        val activePermission = permissions.find { it.permName == accountPermission }
        val permissionDescendants =
            activePermission?.let { getAllDescendants(permissions, it.permName) } ?: emptyList()
        val permissionValidForDeleteMap = mutableMapOf<String?, Boolean>()
        val permissionValidForDeleteList = permissionDescendants.filter {
            it.permName != AntelopePermissionType.Owner.toString() && it.permName != AntelopePermissionType.Active.toString()
        }
        for (it in permissionValidForDeleteList) {
            permissionValidForDeleteMap[it.permName] = true
        }
        return permissionValidForDeleteMap
    }

    private fun getAllDescendants(
        permissions: List<Permission>,
        parentName: String?
    ): List<Permission> {
        val directChildren = permissions.filter { it.parent == parentName }
        val allDescendants = mutableListOf<Permission>()

        for (child in directChildren) {
            allDescendants.add(child)
            allDescendants.addAll(getAllDescendants(permissions, child.permName))
        }

        return allDescendants
    }
}

sealed class PermissionListUiState {
    data object Loading : PermissionListUiState()
    data class Success(val uiModel: UiModel) : PermissionListUiState()
    data class Error(val message: String) : PermissionListUiState()
    data class DeletePermissionSuccess(val permission: String) : PermissionListUiState()
    data class DeletePermissionFailed(val permission: String, val message: String) :
        PermissionListUiState()
}

data class UiModel(
    val permissions: List<Permission>?,
    val permissionValidForDeleteMap: Map<String?, Boolean>
)