package com.mangala.wallet.features.chains.antelope.create_account.presentation.step6

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPrivateKeyUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ListAccountPublicKeysUseCase
import com.mangala.wallet.domain.datastore.usecases.CompleteOnboardingUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.ClipboardFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class VaultaBackupPrivateKeyScreenModel(
    private val clipboardFactory: ClipboardFactory,
    private val accountName: String,
    // TODO: Add use cases for fetching Vaulta-specific keys
    // private val getVaultaAccountKeysUseCase: GetVaultaAccountKeysUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val getAccountPrivateKeyUseCase: GetAccountPrivateKeyUseCase,
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    private val listAccountPublicKeysUseCase: ListAccountPublicKeysUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(VaultaBackupUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAccountKeys()
    }

    private fun loadAccountKeys() {
        screenModelScope.launch {
            try {
                val blockchainUid = getSelectedNetworkUseCase().blockChainUid
                val blockchainType = BlockchainType.fromUid(blockchainUid)

                // Get account permissions to ensure account exists
                val accountPermissions = getAccountPermissionsUseCase(accountName, blockchainUid)
                val activePermission = accountPermissions.find { it.permissionType == AntelopePermissionType.Active }
                val ownerPermission = accountPermissions.find { it.permissionType == AntelopePermissionType.Owner }

                // Get private keys for both permissions
                val activePrivateKey = activePermission?.let {
                    getAccountPrivateKeyUseCase(
                        accountName,
                        AntelopePermissionType.Active.permissionName,
                        blockchainType
                    )
                }

                val ownerPrivateKey = ownerPermission?.let {
                    getAccountPrivateKeyUseCase(
                        accountName,
                        AntelopePermissionType.Owner.permissionName,
                        blockchainType
                    )
                }

                // Get public keys from permissions
                val activePublicKeys = activePermission?.let {
                    listAccountPublicKeysUseCase(accountName, AntelopePermissionType.Active.permissionName)
                } ?: emptyList()

                val ownerPublicKeys = ownerPermission?.let {
                    listAccountPublicKeysUseCase(accountName, AntelopePermissionType.Owner.permissionName)
                } ?: emptyList()

                _uiState.update { currentState ->
                    currentState.copy(
                        ownerPublicKey = ownerPublicKeys.firstOrNull()?.key ?: "",
                        ownerPrivateKey = ownerPrivateKey?.toString() ?: "",
                        activePublicKey = activePublicKeys.firstOrNull()?.key ?: "",
                        activePrivateKey = activePrivateKey?.toString() ?: "",
                        isLoading = false,
                        error = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Failed to load account keys: ${e.message}"
                    )
                }
            }
        }
    }


    fun toggleOwnerKeyVisibility() {
        val newVisibility = !_uiState.value.isOwnerKeyVisible
        _uiState.update { currentState ->
            currentState.copy(
                isOwnerKeyVisible = newVisibility,
                hasViewedOwnerKey = if (newVisibility) true else currentState.hasViewedOwnerKey
            )
        }
    }

    fun toggleActiveKeyVisibility() {
        val newVisibility = !_uiState.value.isActiveKeyVisible
        _uiState.update { currentState ->
            currentState.copy(
                isActiveKeyVisible = newVisibility,
                hasViewedActiveKey = if (newVisibility) true else currentState.hasViewedActiveKey
            )
        }
    }

    fun copyOwnerPrivateKey() {
        val privateKey = _uiState.value.ownerPrivateKey
        if (privateKey.isNotEmpty()) {
            clipboardFactory.copyText("Owner Private Key", privateKey)
            // Show success feedback (implementation depends on UI framework)
        }
    }

    fun copyActivePrivateKey() {
        val privateKey = _uiState.value.activePrivateKey
        if (privateKey.isNotEmpty()) {
            clipboardFactory.copyText("Active Private Key", privateKey)
            // Show success feedback (implementation depends on UI framework)
        }
    }

    fun copyOwnerPublicKey() {
        val publicKey = _uiState.value.ownerPublicKey
        if (publicKey.isNotEmpty()) {
            clipboardFactory.copyText("Owner Public Key", publicKey)
            // Show success feedback (implementation depends on UI framework)
        }
    }

    fun copyActivePublicKey() {
        val publicKey = _uiState.value.activePublicKey
        if (publicKey.isNotEmpty()) {
            clipboardFactory.copyText("Active Public Key", publicKey)
            // Show success feedback (implementation depends on UI framework)
        }
    }

    fun onBackupCompleted() {
        screenModelScope.launch {
            val blockchainUid = getSelectedNetworkUseCase().blockChainUid
            _navigationState.value = NavigationState.NavigateToSetupPin(
                blockchainUid = blockchainUid,
                accountName = accountName
            )
        }
    }
    
    fun clearNavigationState() {
        _navigationState.value = null
    }

    fun retryLoadKeys() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        loadAccountKeys()
    }
    
    private val _navigationState = MutableStateFlow<NavigationState?>(null)
    val navigationState = _navigationState.asStateFlow()
}

sealed class NavigationState {
    data class NavigateToSetupPin(
        val blockchainUid: String,
        val accountName: String
    ) : NavigationState()
}

data class VaultaBackupUiState(
    val isLoading: Boolean = true,
    val ownerPublicKey: String = "",
    val ownerPrivateKey: String = "",
    val activePublicKey: String = "",
    val activePrivateKey: String = "",
    val isOwnerKeyVisible: Boolean = false,
    val isActiveKeyVisible: Boolean = false,
    val hasViewedOwnerKey: Boolean = false,
    val hasViewedActiveKey: Boolean = false,
    val error: String? = null
) {
    val hasViewedAllKeys: Boolean
        get() = hasViewedOwnerKey && hasViewedActiveKey
}