package com.mangala.wallet.features.receive.presentation

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReceiveTokenScreenModel(
    private val accountId: String?,
    private val address: String?,
    private val networkType: NetworkType,
    initialBlockchainUid: String?,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    buildEnvironmentProvider: BuildEnvironmentProvider
) : BaseScreenModel() {

    private val _uiState =
        MutableStateFlow<AccountUiModelReceiveTokenUiState>(AccountUiModelReceiveTokenUiState.Initial)
    val uiState: StateFlow<AccountUiModelReceiveTokenUiState> = _uiState.asStateFlow()

    private val allNetworks =
        BlockchainNetworkData.getAllBlockchainNetworkSupported(buildEnvironmentProvider.isDevelopmentEnvironment())

    init {
        if (accountId == null && address == null) {
            throw IllegalArgumentException("Either accountId or address must be provided")
        }

        screenModelScope.launch(Dispatchers.IO) {
            val selectedNetwork = initialBlockchainUid?.let { initialBlockchainUid ->
                allNetworks
                    .asSequence()
                    .filter { it.blockchainType.networkType == networkType }
                    .find { it.blockChainUid == initialBlockchainUid }
            } ?: run {
                getSelectedNetworkUseCase()
            }
            val nativeCoin = getNativeCoinUseCase(selectedNetwork.blockChainUid)

            _uiState.value = when (networkType) {
                NetworkType.EVM ->
                    AccountUiModelReceiveTokenUiState.Evm(
                        address = null,
                        nativeCoin = nativeCoin,
                        selectedNetwork = selectedNetwork,
                    )

                NetworkType.ANTELOPE ->
                    AccountUiModelReceiveTokenUiState.Antelope(
                        address = null,
                        nativeCoin = nativeCoin.copy(reference = nativeCoin.reference?.replace("EOS", "A")),
                        selectedNetwork = selectedNetwork,
                    )

                else ->
                    AccountUiModelReceiveTokenUiState.Initial
            }

            // If address is provided, set it directly; otherwise use accountId
            if (address != null) {
                setAddressDirect(address)
            } else if (accountId != null) {
                onSelectAccount(accountId)
            }
        }
    }

    private fun setAddressDirect(address: String) {
        _uiState.update {
            when (it) {
                is AccountUiModelReceiveTokenUiState.Evm -> {
                    it.copy(address = address)
                }
                is AccountUiModelReceiveTokenUiState.Antelope -> {
                    it.copy(address = address)
                }
                else -> it
            }
        }
    }

    fun onSelectAccount(accountId: String) {
        screenModelScope.launch {
            if (networkType == NetworkType.EVM) {
                val account = getAccountByIdUseCase(accountId)
                _uiState.update {
                    if (it is AccountUiModelReceiveTokenUiState.Evm) {
                        it.copy(address = account?.name)
                    } else it
                }
            } else {
                val accountAntelope =
                    getAccountsUseCase.invoke().find { it.accountName == accountId }
                _uiState.update {
                    if (it is AccountUiModelReceiveTokenUiState.Antelope) {
                        it.copy(address = accountAntelope?.accountName)
                    } else it
                }
            }
        }
    }

    fun onAmountChange(amount: String) {
        _uiState.update {
            when (it) {
                is AccountUiModelReceiveTokenUiState.Evm -> {
                    it.copy(amount = amount.ifBlank { null })
                }

                is AccountUiModelReceiveTokenUiState.Antelope -> {
                    it.copy(amount = amount.ifBlank { null })
                }

                else -> it
            }
        }
    }
}