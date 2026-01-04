package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.account.usecases.GetAccountBalanceUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveSelectedNetworkUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.wallet.usecases.GetAllWalletsUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletAccountsUseCase
import com.mangala.wallet.domain.wallet.usecases.SelectWalletUseCase
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.channels.Channel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChangeNetworkForPaymentScreenModel(
    private val saveSelectedNetworkUseCase: SaveSelectedNetworkUseCase,
    private val getAllWalletsUseCase: GetAllWalletsUseCase,
    private val selectWalletUseCase: SelectWalletUseCase,
    private val getSelectedWalletAccountsUseCase: GetSelectedWalletAccountsUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountBalanceUseCase: GetAccountBalanceUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider,
    private val getNativeCoinUseCase: GetNativeCoinUseCase
): BaseScreenModel() {

    private var _uiState: MutableStateFlow<ChangeNetworkForPaymentUiState> = MutableStateFlow(ChangeNetworkForPaymentUiState.Loading)
    val uiState: StateFlow<ChangeNetworkForPaymentUiState> = _uiState.asStateFlow()

    val checkAccountBalanceChannel = Channel<Boolean>(Channel.BUFFERED)
    val getNativeTokenChannel = Channel<String>(Channel.BUFFERED)

    init {
        screenModelScope.launch {
            val selectedNetwork = getSelectedNetworkUseCase()
            val supportedTokens = PayWithCryptoMethod.listSupportedNetwork()
            val wallets = getAllWalletsUseCase()
            val accounts = getSelectedWalletAccountsUseCase() ?: run {
                _uiState.value = ChangeNetworkForPaymentUiState.Error(
                    WrappedStringResource.StringRes(
                        MR.strings.error_change_network_can_not_get_selected_account
                    )
                )
                return@launch
            }
            println("accounts: $accounts")
            _uiState.value = ChangeNetworkForPaymentUiState.Success(
                supportedNetworks = supportedTokens,
                accountBlockchainType = selectedNetwork.blockchainType,
                wallets = wallets,
                accounts = accounts
            )
        }
    }

    fun chooseNetworkAndWalletToPay(
       network: BlockchainType,
       walletModel: WalletModel
    ) {
        screenModelScope.launch {
            val isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()
            println("on continue clicked: $network, $walletModel")
            val chainId = Chain.fromBlockchainType(network).id

            val blockchainNetworkData = BlockchainNetworkData.getBlockchainByChainId(chainId.toLong(), isDevelopmentEnvironment)
                ?: run {
                    _uiState.value = ChangeNetworkForPaymentUiState.Error(
                        WrappedStringResource.StringRes(
                            MR.strings.error_change_network_not_supported_for_payment
                        )
                    )
                    return@launch
                }
            saveSelectedNetworkUseCase(blockchainNetworkData)
            selectWalletUseCase(walletModel.id)
        }
    }

    fun checkAccountBalance(
        network: BlockchainType,
        account: AccountBlockchainModel
    ) {
        screenModelScope.launch {
            val nativeToken = getAccountBalanceUseCase(
                forceReload = false,
                address = account.bip44Address,
                blockchainType = network,
                accountId = account.account.id,
                sparkline = false
            ).filter { it.isCoin }.getOrNull(0) ?: run {
                _uiState.value = ChangeNetworkForPaymentUiState.Error(
                    WrappedStringResource.StringRes(
                        MR.strings.error_pay_with_crypto_can_not_get_native_token_of_account,
                        getNativeCoinUseCase.invoke(network.uid).reference ?: network.getNativeTokenSymbol()
                    )
                )
                return@launch
            }
            println("nativeToken: $nativeToken")
            if (nativeToken.totalAmount > 0.0) {
                checkAccountBalanceChannel.send(true)
            } else {
                checkAccountBalanceChannel.send(false)
            }
            getNativeTokenChannel.send(nativeToken.contractSymbol)
        }
    }
}

sealed class ChangeNetworkForPaymentUiState {
    data object Loading : ChangeNetworkForPaymentUiState()
    data class Success(
        val supportedNetworks: List<BlockchainType>,
        val accountBlockchainType: BlockchainType,
        val wallets: List<WalletModel>,
        val accounts: List<AccountBlockchainModel>
    ) : ChangeNetworkForPaymentUiState()
    data class Error(val message: WrappedStringResource) : ChangeNetworkForPaymentUiState()
}