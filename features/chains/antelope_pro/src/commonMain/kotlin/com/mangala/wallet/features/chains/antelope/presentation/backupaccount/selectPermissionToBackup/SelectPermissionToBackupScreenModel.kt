package com.mangala.wallet.features.chains.antelope.presentation.backupaccount.selectPermissionToBackup

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SelectPermissionToBackupScreenModel(
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    private val accountName: String
) : BaseScreenModel() {
    private val _accountPermissions = MutableStateFlow(emptyList<String>())
    val accountPermissions = _accountPermissions.asStateFlow()

    init {
        loadAccountPermissions()
    }

    private fun loadAccountPermissions() {
        screenModelScope.launch {
            _accountPermissions.value = getAccountPermissionsUseCase(accountName).map { it.permissionType.permissionName }
        }
    }
}