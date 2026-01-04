package com.mangala.wallet.features.chains.antelope.presentation.esr

import cafe.adriel.voyager.core.model.screenModelScope
import com.linh.antelope_qr.domain.usecase.DecodeEsrUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.ResolveEsrUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EsrScreenModel(
    private val esrUri: String,
    private val decodeEsrUseCase: DecodeEsrUseCase,
    private val resolveEsrUseCase: ResolveEsrUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<EsrScreenUiState> =
        MutableStateFlow(EsrScreenUiState.Loading)
    val uiState: StateFlow<EsrScreenUiState> = _uiState.asStateFlow()

    private lateinit var blockchainType: BlockchainType

    init {
        try {
            screenModelScope.launch {
                val result = decodeEsrUseCase(esrUri)

                blockchainType = BlockchainType.fromChainId(result.resolvedChainId.orEmpty())

                if (blockchainType is BlockchainType.Unsupported) {
                    _uiState.value = EsrScreenUiState.Error("Unsupported blockchain")
                } else {
                    if (result.isIdentityRequest) {
                        screenModelScope.launch {
                            val accounts = getAccountsUseCase(blockchainType).map { it.accountName }
                            val firstAccount = accounts.firstOrNull()
                                ?: run {
                                    _uiState.value = EsrScreenUiState.Error("No accounts found")
                                    return@launch
                                }
                            val permissions = getAccountPermissionsUseCase(
                                firstAccount,
                                blockchainType.uid
                            ).map { it.permissionType.permissionName }

                            _uiState.value = EsrScreenUiState.Data(
                                EsrDataUiModel.Identity(
                                    result,
                                    blockchainType,
                                    accounts,
                                    permissions,
                                    selectedAccount = firstAccount,
                                    selectedPermission = permissions.first()
                                )
                            )
                        }
                    } else {
                        screenModelScope.launch {
                            val availableAccounts = getAccountsUseCase(blockchainType).map { it.accountName }
                            val availableAuthorizations = mutableListOf<String>()
                            
                            result.authorizations.forEach { auth ->
                                if (availableAccounts.contains(auth.actor)) {
                                    val permissions = getAccountPermissionsUseCase(
                                        auth.actor,
                                        blockchainType.uid
                                    ).map { it.permissionType.permissionName }
                                    
                                    if (permissions.contains(auth.permission)) {
                                        availableAuthorizations.add("${auth.actor}@${auth.permission}")
                                    }
                                }
                            }
                            
                            val authorizationOptions = result.authorizations.map { "${it.actor}@${it.permission}" }
                            val validAuthorizations = availableAuthorizations.intersect(authorizationOptions.toSet()).toList()
                            val selectedAuthorization = validAuthorizations.firstOrNull()
                            
                            _uiState.value = EsrScreenUiState.Data(
                                EsrDataUiModel.SignTransaction(
                                    result, 
                                    blockchainType, 
                                    selectedAuthorization,
                                    validAuthorizations
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.value = EsrScreenUiState.Error("Failed to decode ESR: ${e.message}")
        }
    }

    fun onSelectAccount(accountName: String) {
        screenModelScope.launch {
            val permissions = getAccountPermissionsUseCase(
                accountName,
                blockchainType.uid
            ).map { it.permissionType.permissionName }

            _uiState.update {
                (it as? EsrScreenUiState.Data)?.copy(
                    uiModel = (it.uiModel as? EsrDataUiModel.Identity)?.copy(
                        accounts = it.uiModel.accounts,
                        permissions = permissions,
                        selectedAccount = accountName,
                        selectedPermission = permissions.first()
                    ) ?: it.uiModel
                ) ?: it
            }
        }
    }

    fun onSelectPermission(permission: String) {
        _uiState.update {
            (it as? EsrScreenUiState.Data)?.copy(
                uiModel = (it.uiModel as? EsrDataUiModel.Identity)?.copy(
                    accounts = it.uiModel.accounts,
                    permissions = it.uiModel.permissions,
                    selectedAccount = it.uiModel.selectedAccount,
                    selectedPermission = permission
                ) ?: it.uiModel
            ) ?: it
        }
    }

    fun onSelectAuthorization(authorization: String) {
        _uiState.update {
            (it as? EsrScreenUiState.Data)?.copy(
                uiModel = (it.uiModel as? EsrDataUiModel.SignTransaction)?.copy(
                    selectedAuthorization = authorization
                ) ?: it.uiModel
            ) ?: it
        }
    }

    fun onAcceptIdentity() {
        screenModelScope.launch {
            val uiState = (uiState.value as? EsrScreenUiState.Data) ?: return@launch
            val uiModel = uiState.uiModel as? EsrDataUiModel.Identity ?: return@launch

            _uiState.value = EsrScreenUiState.Signing

            resolveEsrUseCase(
                esrUri,
                uiModel.esrSigningRequest,
                uiModel.selectedAccount.orEmpty(),
                uiModel.selectedPermission.orEmpty(),
                null
            ).fold(
                onSuccess = {
                    _uiState.value = EsrScreenUiState.Success
                },
                onFailure = {
                    _uiState.value = EsrScreenUiState.Data(
                        uiState.uiModel,
                        it.message
                    )
                }
            )
        }
    }

    fun onAcceptSignTransaction() {
        screenModelScope.launch {
            val uiState = (uiState.value as? EsrScreenUiState.Data) ?: return@launch
            val uiModel = uiState.uiModel as? EsrDataUiModel.SignTransaction ?: return@launch
            val esrSigningRequest = uiModel.esrSigningRequest

            val selectedAuthorization = uiModel.selectedAuthorization
                ?: run {
                    _uiState.value = EsrScreenUiState.Data(
                        uiModel,
                        "Please select an authorization"
                    )
                    return@launch
                }

            val authParts = selectedAuthorization.split("@")
            if (authParts.size != 2) {
                _uiState.value = EsrScreenUiState.Data(
                    uiModel,
                    "Invalid authorization format"
                )
                return@launch
            }

            val actor = authParts[0]
            val permission = authParts[1]

            _uiState.value = EsrScreenUiState.Signing

            try {
                resolveEsrUseCase(
                    esrUri,
                    esrSigningRequest,
                    actor,
                    permission,
                    null
                ).fold(
                    onSuccess = {
                        _uiState.value = EsrScreenUiState.Success
                    },
                    onFailure = {
                        _uiState.value = EsrScreenUiState.Data(
                            uiModel,
                            it.message
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EsrScreenUiState.Data(
                    uiModel,
                    "Failed to resolve ESR: ${e.message}"
                )
                return@launch
            }
        }
    }
}