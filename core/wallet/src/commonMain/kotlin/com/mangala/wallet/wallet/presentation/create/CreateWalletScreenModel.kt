package com.mangala.wallet.wallet.presentation.create

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.CreateWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.RestoreWalletUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CreateWalletState {
    data object Idle : CreateWalletState
    data object Creating : CreateWalletState
    data object Success : CreateWalletState
    data class Error(val message: String) : CreateWalletState
}

class CreateWalletScreenModel(
    private val createWalletUseCase: CreateWalletUseCase,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase,
    private val restoreWalletUseCase: RestoreWalletUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
): BaseScreenModel() {

    val onCreateDone: Channel<Unit> = Channel()

    private val _state = MutableStateFlow<CreateWalletState>(CreateWalletState.Idle)
    val state = _state.asStateFlow()

    private var hasStartedCreation = false

    fun createWallet(blockchainUid: String, antelopeAccountName: String?) {
        if (hasStartedCreation) return
        hasStartedCreation = true

        _state.value = CreateWalletState.Creating
        screenModelScope.launch {
            try {
                val blockchainType = BlockchainType.fromUid(blockchainUid)

                when(blockchainType.networkType) {
                    NetworkType.ANTELOPE -> {
                        antelopeAccountName?.let {
                            updateAccountStatusUseCase(
                                it,
                                isTemp = false,
                                blockchainType,
                                createAccountState = AntelopeAccount.CreateAccountState.DONE
                            )
                        }
                    }
                    NetworkType.EVM -> {
                        createWalletUseCase(wordsCount = 12, passphrase = "", blockchainType)
                    }
                    NetworkType.BITCOIN -> {
                        createWalletUseCase(wordsCount = 12, passphrase = "", blockchainType)
                    }
                    NetworkType.OTHER,
                    NetworkType.UNSUPPORTED -> {
                        _state.value = CreateWalletState.Error("Unsupported network type")
                        hasStartedCreation = false
                        return@launch
                    }
                }
                _state.value = CreateWalletState.Success
            } catch (e: Exception) {
                _state.value = CreateWalletState.Error(
                    e.message ?: "Failed to create wallet"
                )
                hasStartedCreation = false
            }
        }
    }

    fun restoreWallet(listString: List<String>, name: String) {
        _state.value = CreateWalletState.Creating
        screenModelScope.launch {
            try {
                restoreWalletUseCase(listString, name, getSelectedNetworkUseCase().blockchainType)
                _state.value = CreateWalletState.Success
                onCreateDone.trySend(Unit)
            } catch (e: Exception) {
                _state.value = CreateWalletState.Error(
                    e.message ?: "Failed to restore wallet"
                )
            }
        }
    }

    fun dismissError() {
        _state.value = CreateWalletState.Idle
        hasStartedCreation = false
    }
}
