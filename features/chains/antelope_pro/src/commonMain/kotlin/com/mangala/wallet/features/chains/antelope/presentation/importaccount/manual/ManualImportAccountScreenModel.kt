package com.mangala.wallet.features.chains.antelope.presentation.importaccount.manual

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckPublicKeyLinkedToAccountNameException
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckPublicKeyLinkedToAccountNameUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.memtrip.eos.core.crypto.EosPrivateKey.Companion.toEosPrivateKeyOrNull
import com.memtrip.eos.core.crypto.EosPublicKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManualImportAccountScreenModel(
    private val validateAccountUseCase: ValidateAccountUseCase,
    private val checkPublicKeyLinkedToAccountNameUseCase: CheckPublicKeyLinkedToAccountNameUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) : BaseScreenModel() {

    private lateinit var blockchainType: BlockchainType

    init {
        screenModelScope.launch {
            blockchainType = getSelectedNetworkUseCase().blockchainType
        }
    }

    private val _uiState: MutableStateFlow<ManualImportAccountScreenUiState> = MutableStateFlow(
        ManualImportAccountScreenUiState.NotImported(
            activePrivateKey = "",
            ownerPrivateKey = "",
            accountName = "",
            accountLabel = ""
        )
    )
    val uiState: StateFlow<ManualImportAccountScreenUiState> = _uiState.asStateFlow()

    fun onAccountLabelChange(accountLabel: String) {
        _uiState.update {
            val state = (it as? ManualImportAccountScreenUiState.NotImported) ?: return@update it
            state.copy(accountLabel = accountLabel)
        }
    }

    fun onActivePrivateKeyChange(privateKey: String) {
        _uiState.update {
            val state = (it as? ManualImportAccountScreenUiState.NotImported) ?: return@update it
            val error =
                if (privateKey.isNotBlank() && privateKey.toEosPrivateKeyOrNull() == null) "Invalid active private key" else null
            state.copy(activePrivateKey = privateKey, activePrivateKeyError = error)
        }
    }

    fun onOwnerPrivateKeyChange(privateKey: String) {
        _uiState.update {
            val state = (it as? ManualImportAccountScreenUiState.NotImported) ?: return@update it
            val error =
                if (privateKey.isNotBlank() && privateKey.toEosPrivateKeyOrNull() == null) "Invalid owner private key" else null
            state.copy(ownerPrivateKey = privateKey, ownerPrivateKeyError = error)
        }
    }

    fun onAccountNameChange(accountName: String) {
        _uiState.update {
            val state = (it as? ManualImportAccountScreenUiState.NotImported) ?: return@update it
            val validationResult = if (accountName.isBlank()) {
                null
            } else {
                validateAccountUseCase.validateAccountName(accountName, AccountNameType.Standard) // TODO: Let users choose type: premium or standard
            }
            state.copy(
                accountName = accountName,
                accountCharacterValidationResult = validationResult
            )
        }
    }

    fun onImportAccount() {
        screenModelScope.launch {
            val state = (_uiState.value as? ManualImportAccountScreenUiState.NotImported) ?: return@launch
            if (state.activePublicKey == null) {
                return@launch
            }

            val account = checkPublicKeyLinkedToAccountNameUseCase(
                EosPublicKey(state.activePublicKey!!),
                state.ownerPublicKey?.let { EosPublicKey(it) },
                state.accountName,
                blockchainType
            )

            account.fold(
                onSuccess = {
                    saveAccountUseCase(
                        accountName = state.accountName,
                        activePrivateKey = state.activePrivateKey.toEosPrivateKeyOrNull(),
                        ownerPrivateKey = state.ownerPrivateKey.toEosPrivateKeyOrNull(),
                        createAccountState = AntelopeAccount.CreateAccountState.DONE
                    )
                    _uiState.value = ManualImportAccountScreenUiState.Imported(
                        accountName = state.accountName,
                        activePrivateKey = state.activePrivateKey,
                        ownerPrivateKey = state.ownerPrivateKey,
                        accountLabel = state.accountLabel
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        if (error is CheckPublicKeyLinkedToAccountNameException) {
                            when (error) {
                                is CheckPublicKeyLinkedToAccountNameException.InvalidAccountException, is CheckPublicKeyLinkedToAccountNameException.InvalidAccountNameException -> state.copy(
                                    error = "Account name invalid or does not exist"
                                )
                                is CheckPublicKeyLinkedToAccountNameException.InvalidActivePublicKeyException -> state.copy(
                                    activePrivateKeyError = "Active private key does not correspond to account name entered"
                                )
                                is CheckPublicKeyLinkedToAccountNameException.InvalidOwnerPublicKeyException -> state.copy(
                                    ownerPrivateKeyError = "Owner private key does not correspond to account name entered"
                                )
                                is CheckPublicKeyLinkedToAccountNameException.InvalidPublicKeyException -> state.copy(
                                    error = "Invalid public key"
                                )
                            }
                        } else {
                            state.copy(
                                error = error.message
                            )
                        }
                    }
                }
            )
        }
    }
}