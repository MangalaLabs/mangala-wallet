package com.mangala.wallet.features.transactionhistory.presentation.evm.info

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.features.transactionhistory.presentation.utils.getFormattedAddress
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.blockchain.usecases.GetBlockchainExplorerLinkUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.historicalprice.usecases.FetchHistoricalTokenPriceUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.transaction.history.usecases.GetTransactionByTxHashUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.ClipboardFactory
import com.mangala.wallet.utils.ShareFactory
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.ext.weiToEth
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TransactionInfoScreenModel(
    private val accountId: String,
    private val txHash: String,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getBlockchainExplorerLinkUseCase: GetBlockchainExplorerLinkUseCase,
    private val getTransactionByTxHashUseCase: GetTransactionByTxHashUseCase,
    private val fetchHistoricalTokenPriceUseCase: FetchHistoricalTokenPriceUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    private val clipboardFactory: ClipboardFactory,
    private val shareFactory: ShareFactory
) : BaseScreenModel() {

    private val _uiState =
        MutableStateFlow<TransactionInfoUiState>(TransactionInfoUiState.Loading)
    val uiState get() = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        loadTransaction(forceRefresh = false)
    }

    fun onPullToRefresh() {
        screenModelScope.launch {
            _isRefreshing.value = true
            loadTransaction(forceRefresh = true)
            _isRefreshing.value = false
        }
    }

    fun onClickShare() {
        val it = _uiState.value
        if (it !is TransactionInfoUiState.Loaded) return

        shareFactory.shareText("Mangala share via", it.uiModel.blockExplorerUrl) // TODO: Extract string resource
    }

    fun onClickCopyTransactionId() {
        clipboardFactory.copyText("Mangala copy", txHash) // TODO: Extract string resource
    }

    fun onClickCopyAddress() {
        val it = _uiState.value
        if (it !is TransactionInfoUiState.Loaded) return

        val address = Address(it.uiModel.address).eip55

        clipboardFactory.copyText("Mangala copy", address)  // TODO: Extract string resource
    }

    private fun loadTransaction(forceRefresh: Boolean) {
        screenModelScope.launch {
            _uiState.update { TransactionInfoUiState.Loading }

            val currencyCodeAsync = async { getCurrentCurrencyCodeUseCase() }
            val selectedNetworkAsync = async { getSelectedNetworkUseCase() }
            val accountAsync = async { getAccountByIdUseCase.invokeSuspend(accountId) }

            val selectedNetwork = selectedNetworkAsync.await()
            val nativeTokenAsync = async {
                getNativeCoinUseCase(selectedNetwork.blockChainUid)
            }
            val transactionAsync = async {
                getTransactionByTxHashUseCase(
                    accountId = accountId,
                    blockchainUid = selectedNetwork.blockChainUid,
                    txHash = txHash
                )
            }
            val blockExplorerAsync = async {
                getBlockchainExplorerLinkUseCase.getTxLink(
                    blockchainUid = selectedNetwork.blockChainUid,
                    txHash = txHash
                )
            }

            val transaction = transactionAsync.await()
            val currencyCode = currencyCodeAsync.await()
            val currency = Currency.valueOf(currencyCode)
            val currencySymbol = Currency.valueOf(currencyCode).symbol

            val blockExplorerUrl = blockExplorerAsync.await()
            val nativeToken = nativeTokenAsync.await()
            val account = accountAsync.await()

            val nativeTokenRef = transaction.gasMetadata.contractTickerSymbol ?: nativeToken.reference.orEmpty()
            val (_, transactedSymbol) = transaction.getValueTransacted(accountId)

            val cryptoTokenRef = if (transactedSymbol == nativeTokenRef) {
                nativeTokenRef
            } else {
                transaction.logEvents?.firstOrNull()?.senderAddress ?: transaction.getTransactedTokenAddress().orEmpty()
            }

            val gasCoinPriceFlow = fetchHistoricalTokenPriceUseCase.invokeFlow(
                tokenRef = nativeTokenRef,
                date = transaction.blockSignedAt,
                forceRefresh = forceRefresh
            )
            val cryptoPriceFlow = fetchHistoricalTokenPriceUseCase.invokeFlow(
                tokenRef = cryptoTokenRef,
                date = transaction.blockSignedAt,
                forceRefresh = forceRefresh
            )

            combine(gasCoinPriceFlow, cryptoPriceFlow) { gasCoinPriceResource, cryptoPriceResource ->
                val historicalPriceGasCoin = gasCoinPriceResource.data?.getPriceInCurrency(currency)?.toBigDecimal()
                val historicalPriceCrypto = cryptoPriceResource.data?.getPriceInCurrency(currency)?.toBigDecimal()

                with(transaction) {
                    val valueTransacted = getValueTransacted(account.bip44Address)
                    val gasFee = if (feesPaid.isNotBlank()) feesPaid.toBigDecimal()
                        .weiToEth(gasMetadata.contractDecimals ?: nativeToken.decimals?.toInt().orZero()) else BigDecimal.ZERO

                    _uiState.update {
                        TransactionInfoUiState.Loaded(
                            TransactionInfoUi(
                                status = status,
                                type = transactionType,
                                amount = valueTransacted.first ?: BigDecimal.ZERO,
                                symbol = valueTransacted.second.orEmpty(),
                                fiatValue = valueTransacted.first?.times(
                                    historicalPriceCrypto ?: BigDecimal.ZERO
                                ) ?: BigDecimal.ZERO,
                                fiatCurrencySymbol = currencySymbol,
                                network = selectedNetwork.name,
                                transactionId = txHash,
                                address = getFormattedAddress(),
                                gasFee = gasFee,
                                gasFeeFiatValue = gasFee.times(
                                    historicalPriceGasCoin ?: BigDecimal.ZERO
                                ),
                                gasFeeSymbol = gasMetadata.contractTickerSymbol.orEmpty(),
                                date = blockSignedAt,
                                blockExplorerUrl = blockExplorerUrl
                            )
                        )
                    }
                }
            }.collect {

            }
        }
    }
}