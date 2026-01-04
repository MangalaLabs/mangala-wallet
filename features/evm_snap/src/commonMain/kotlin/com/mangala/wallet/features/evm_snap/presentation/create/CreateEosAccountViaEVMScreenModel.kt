package com.mangala.wallet.features.evm_snap.presentation.create

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.GetAllWalletsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.createimport.GetAccountsByAuthorizersUseCase
import com.mangala.wallet.features.evm_snap.domain.usecase.GetEosPrivateKeyFromEvmUseCase
import com.mangala.wallet.features.evm_snap.presentation.EosAccountLinkedEvmWalletChannelData
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

class CreateEosAccountViaEVMScreenModel(
    private val getEosPrivateKeyFromEvmUseCase: GetEosPrivateKeyFromEvmUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountsByAuthorizersUseCase: GetAccountsByAuthorizersUseCase,
    private val getAllWalletUseCase: GetAllWalletsUseCase,
): BaseScreenModel() {
    private var _uiState: MutableStateFlow<CreateEosAccountViaEvmUIState> = MutableStateFlow(CreateEosAccountViaEvmUIState.Loading)
    val uiState: StateFlow<CreateEosAccountViaEvmUIState> = _uiState.asStateFlow()

    val eosAccountLinkedEvmWalletChannel = Channel<EosAccountLinkedEvmWalletChannelData>(Channel.BUFFERED)

    init {
        fetchEvmWallets()
    }

    fun getEosPrivateKeyFromEvmWallet(walletModel: WalletModel) {
        screenModelScope.launch {
            _uiState.value = CreateEosAccountViaEvmUIState.Loading
            val eosAccountKeyPairs = getEosPrivateKeyFromEvmUseCase.getEosKeyPairs(walletModel)
            _uiState.value = CreateEosAccountViaEvmUIState.Success(
                eosOwnerPrivateKey = eosAccountKeyPairs.ownerKeyPair.toLegacyString(),
                eosActivePrivateKey = eosAccountKeyPairs.activeKeyPair.toLegacyString()
            )
        }
    }

    fun isEvmLinkedWithEosAccount(walletModel: WalletModel) {
        screenModelScope.launch {
            val privateKey = getEosPrivateKeyFromEvmUseCase.getOwnerPrivateKey(walletModel)
            val currentNetwork = getSelectedNetworkUseCase.invoke().blockchainType
            val accounts: Result<List<AntelopeAccountByAuthorizer>>
            val timeTaken = measureTime {
                accounts = getAccountsByAuthorizersUseCase.invoke(
                    privateKey = privateKey,
                    blockchainType = currentNetwork
                )
            }
            val minDuration = LOADING_SCREEN_MIN_DURATION_MILLIS.milliseconds
            val remainingTime = minDuration.minus(timeTaken)

            if (remainingTime > Duration.ZERO) {
                delay(remainingTime)
            }

            accounts.fold(
                onSuccess = { data ->
                    if (data.isEmpty()) {
                        eosAccountLinkedEvmWalletChannel.send(EosAccountLinkedEvmWalletChannelData(walletModel.id, false))
                    } else {
                        eosAccountLinkedEvmWalletChannel.send(EosAccountLinkedEvmWalletChannelData(walletModel.id, true))
                    }
                },
                onFailure = { error ->
                    println("=== got error when importing account: $error")
                    eosAccountLinkedEvmWalletChannel.send(EosAccountLinkedEvmWalletChannelData(walletModel.id, false))
                }
            )
        }
    }

    private fun fetchEvmWallets() {
        screenModelScope.launch {
            val wallets = getAllWalletUseCase.invoke()
            _uiState.value = CreateEosAccountViaEvmUIState.Initial(wallets)
        }
    }

    companion object {
        private const val LOADING_SCREEN_MIN_DURATION_MILLIS = 1000
    }
}

sealed class CreateEosAccountViaEvmUIState {
    data class Initial(val wallets: List<WalletModel>): CreateEosAccountViaEvmUIState()
    data object Loading: CreateEosAccountViaEvmUIState()
    data class Success(val eosOwnerPrivateKey: String, val eosActivePrivateKey: String): CreateEosAccountViaEvmUIState()
}