package com.mangala.wallet.features.chains.antelope.presentation.permission.updatepermission

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.Account
import com.mangala.antelope.base.api.model.Key
import com.mangala.antelope.base.api.model.Wait
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.AccountAuthAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.AccountAuthWait
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.AccountKey
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.UpdateAccountPermissionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UpdatePermissionScreenModel(
    private val updateAccountPermissionUseCase: UpdateAccountPermissionUseCase
) : ScreenModel {
    private val _uiState: MutableStateFlow<UpdatePermissionScreenUiState> = MutableStateFlow(
        UpdatePermissionScreenUiState.Loading
    )
    val uiState: StateFlow<UpdatePermissionScreenUiState> = _uiState.asStateFlow()

    fun onSubmitUpdatePermission(
        authorizationAccountName: String,
        authorizationPermission: String,
        permissionUpdated: String,
        permissionParent: String,
        threshold: Int,
        keys: List<Key>,
        accounts: List<Account>,
        waits: List<Wait>,
    ) {
        screenModelScope.launch {
            updateAccountPermissionUseCase.invoke(
                authorizationAccountName,
                authorizationPermission,
                permissionUpdated,
                permissionParent,
                threshold,
                convertKeysToAccountKeys(keys),
                convertAccountsToAccountAuthAccounts(accounts),
                convertWaitsToAccountAuthWaits(waits)
            )
                .onSuccess {
                    _uiState.value = UpdatePermissionScreenUiState.Success("Permission updated")
                }
                .onFailure {
                    _uiState.value =
                        UpdatePermissionScreenUiState.Error(
                            it.message ?: "Update permission failed"
                        )
                }
        }
    }

    private fun convertWaitsToAccountAuthWaits(waits: List<Wait>): List<AccountAuthWait> {
        return waits.map {
            AccountAuthWait(
                it.waitSec?.toInt() ?: 0,
                it.weight ?: 0
            )
        }
    }

    private fun convertAccountsToAccountAuthAccounts(accounts: List<Account>): List<AccountAuthAccount> {
        return accounts.map { account ->
            account.permission?.let {
                AccountAuthAccount(
                    it.actor ?: "",
                    it.permission ?: "",
                    account.weight ?: 0L
                )
            } ?: run {
                AccountAuthAccount(
                    "",
                    "",
                    account.weight ?: 0L
                )
            }
        }
    }

    private fun convertKeysToAccountKeys(keys: List<Key>): List<AccountKey> {
        return keys.map {
            AccountKey(it.key ?: "", it.weight?.toLong() ?: 0)
        }
    }
}
