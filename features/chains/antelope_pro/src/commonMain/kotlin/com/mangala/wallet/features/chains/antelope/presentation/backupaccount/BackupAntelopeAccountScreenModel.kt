package com.mangala.wallet.features.chains.antelope.presentation.backupaccount

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPrivateKeyUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BackupAntelopeAccountScreenModel(
    private val accountName: String,
    private val blockchainUid: String?,
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    private val getAccountPrivateKeyUseCase: GetAccountPrivateKeyUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
): BaseScreenModel() {

    private val _uiState: MutableStateFlow<BackupAntelopeAccountScreenUiState> = MutableStateFlow(BackupAntelopeAccountScreenUiState.Loading)
    val uiState: StateFlow<BackupAntelopeAccountScreenUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            val blockchainUid = this@BackupAntelopeAccountScreenModel.blockchainUid
                ?: getSelectedNetworkUseCase().blockChainUid
            val blockchainType = BlockchainType.fromUid(blockchainUid)

            val accountPermissions = getAccountPermissionsUseCase(accountName, blockchainUid)
            val activePermission = accountPermissions.find { it.permissionType == AntelopePermissionType.Active }
            val ownerPermission = accountPermissions.find { it.permissionType == AntelopePermissionType.Owner }
            val accountActivePrivateKey = activePermission?.let {
                getAccountPrivateKeyUseCase(
                    accountName,
                    AntelopePermissionType.Active.permissionName,
                    blockchainType
                )
            }
            val accountOwnerPrivateKey = ownerPermission?.let {
                getAccountPrivateKeyUseCase(
                    accountName,
                    AntelopePermissionType.Owner.permissionName,
                    blockchainType
                )
            }

            _uiState.value = BackupAntelopeAccountScreenUiState.Loaded(
                accountName = accountName,
                activeKey = accountActivePrivateKey,
                ownerKey = accountOwnerPrivateKey
            )
        }
    }
}