package com.mangala.wallet.features.send_base.step4.evm

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.transaction.fee.TransactionFeeType
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.erc20.contract.TransferMethod
import com.mangala.wallet.features.chains.evmcompatible.core.amountToBigInt
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EstimateGasUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetNonceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendTokenUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.features.chains.ui.FeeOptionUiModel
import com.mangala.wallet.features.chains.ui.SendTransactionScreenModel
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.utils.Constants.TRANSACTION_FEE_REFRESH_INTERVAL
import com.mangala.wallet.utils.ext.formatFiat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive

open class BaseEvmStep4VerifyAndSendScreenModel(
    contactId: Long?,
    blockchainUid: String,
    tokenId: Long,
    protected val recipientAddress: String,
    protected val amount: String,
    accountId: String,
    getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getAccountByIdUseCase: GetAccountByIdUseCase,
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    private val getSelectedCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getTransactionFeeOptionsUseCase: GetTransactionFeeOptionsUseCase,
    private val getContactUseCase: GetContactByIdUseCase,
    private val getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase: GetRecommendedGasPriceUseCase,
    private val sendTokenUseCase: SendTokenUseCase,
    private val estimateGasUseCase: EstimateGasUseCase,
    getNativeCoinUseCase: GetNativeCoinUseCase,
    getLatestBlockUseCase: GetLatestBlockUseCase,
    protected val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val getNonceUseCase: GetNonceUseCase
) : SendTransactionScreenModel(
    fetchTokenPriceUseCase,
    getNativeCoinUseCase,
    getTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase,
    getTransactionFeeOptionsUseCase,
    getCurrentCurrencyCodeUseCase,
    getLatestBlockUseCase
) {

    protected val _uiState =
        MutableStateFlow<BaseEvmStep4VerifyAndSendScreenUiState>(
            BaseEvmStep4VerifyAndSendScreenUiState.Loading
        )
    val uiState: StateFlow<BaseEvmStep4VerifyAndSendScreenUiState> get() = _uiState

    private var refreshJob: Job? = null
    private lateinit var currencySymbol: String
    protected lateinit var token: TokenBalanceEntity
        private set
    protected val currentAccount = getAccountByIdUseCase(accountId)
    protected var nonce: Long? = null
        private set

    init {
        this.accountId = accountId
        setBlockchainType(blockchainUid)

        screenModelScope.launch {
            val tokens = getTokenBalanceByTokenId(tokenId, accountId)
            tokens.firstOrNull()?.let { token ->
                this@BaseEvmStep4VerifyAndSendScreenModel.token = token
                _uiState.update {
                    BaseEvmStep4VerifyAndSendScreenUiState.Data(
                        contact = contactId?.let { it1 -> getContactById(it1) },
                        recipientAddress = recipientAddress,
                        account = currentAccount,
                        selectedToken = token,
                        estimatedGasLimit = null,
                        gasPrice = null,
                        txHash = null,
                        transactionFeeOptions = emptyList(),
                        selectedTransactionFee = null,
                        tokenFiatValue = "",
                        totalTransactionFiatValue = ""
                    )
                }
                calculateTransactionFee(
                    currentAccount.bip44Address,
                    recipientAddress,
                    amount,
                    token
                ) // TODO: Support different address types
            } ?: kotlin.run {
                _uiState.update { BaseEvmStep4VerifyAndSendScreenUiState.Error }
            }
        }
    }

    private suspend fun getContactById(id: Long): ContactEntity? {
        return getContactUseCase.invoke(id)
    }

    fun onConsumeTxHash() {
        _uiState.update { (it as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.copy(txHash = null) ?: it }
        stopRefreshJob()
    }

    fun getTxHash(): String? {
        return (_uiState.value as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.txHash
    }

    fun onTransactionFeeSelected(transactionFeeOption: EvmFeeOptionUiModel) {
        _uiState.update {
            (it as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.copy(
                selectedTransactionFee = transactionFeeOption,
                transactionFeeOptions = it.transactionFeeOptions.map { feeOption ->
                    val isSelected = feeOption.transactionFee.transactionFeeType == transactionFeeOption.transactionFee.transactionFeeType
                    feeOption.copy(isSelected = isSelected)
                }
            ) ?: it
        }
    }

    private fun stopRefreshJob() {
        refreshJob?.cancel()
    }

    private fun getTokenBalanceByTokenId(
        tokenId: Long,
        accountId: String
    ): List<TokenBalanceEntity> {
        return getTokenBalanceByTokenIdUseCase.invoke(tokenId, accountId)
    }

    private fun setBlockchainType(blockchainUid: String) {
        blockchainType = BlockchainType.fromUid(blockchainUid)
    }

    private fun calculateTransactionFee(
        fromAddress: String,
        toAddress: String,
        amount: String,
        crypto: TokenBalanceEntity
    ) {
        refreshJob?.cancel()
        refreshJob = screenModelScope.launch {
            while (isActive) {
                _uiState.update {
                    val oldUiState = (it as? BaseEvmStep4VerifyAndSendScreenUiState.Data)
                    val oldSelectedTransactionFee = oldUiState?.selectedTransactionFee

                    val unbufferedGasLimit =
                        calculateGasLimit(crypto, toAddress, amount, fromAddress) ?: kotlin.run {
                            _uiState.update {
                                (it as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.copy(
                                    estimateGasErrorVisible = true
                                ) ?: it
                            }
                            return@launch
                        }
                    val gasLimit =
                        if (crypto.isCoin) unbufferedGasLimit else unbufferedGasLimit.getBufferedGasLimit()

                    val gasLimitRawValue = BigDecimal.parseString(gasLimit.toString())
                    gasLimitInWei = gasLimitRawValue

                    val feeOptions = getTransactionFeeOptions() ?: return@launch
                    val transactionFeeType =
                        oldSelectedTransactionFee?.transactionFee?.transactionFeeType
                            ?: TransactionFeeType.REGULAR
                    val transactionFeeOptionUiModelsAsync = async {
                        getTransactionFeeOptionUiModels(
                            feeOptions,
                            transactionFeeType,
                            gasLimitInWei
                        )
                    }
                    val tokenFiatPriceAsync = async {
                        getTokenFiatPrice(crypto)
                    }

                    if (::currencySymbol.isInitialized.not()) {
                        val currencyCode = getSelectedCurrencyCodeUseCase()
                        currencySymbol = Currency.valueOf(currencyCode).symbol
                    }
                    val transactionFeeOptionUiModels = transactionFeeOptionUiModelsAsync.await()
                    val tokenFiatPrice = tokenFiatPriceAsync.await()

                    // TODO: Move this nonce logic elsewhere
                    nonce = getNonceUseCase.getNonceLong(
                        rpcUrl,
                        currentId.getAndIncrement(),
                        Address(currentAccount?.bip44Address.orEmpty()),
                        DefaultBlockParameter.Pending
                    )

                    val gasFeeOption = transactionFeeOptionUiModels?.let { it1 ->
                        getCurrentFeeOptionUiModel(
                            it1,
                            transactionFeeType,
                        )
                    }


                    if (gasLimit == null || gasFeeOption == null) {
                        _uiState.update {
                            oldUiState?.copy(estimateGasErrorVisible = true) ?: it
                        }
                        return@launch
                    }

                    val rawTokenFiatValue = tokenFiatPrice?.multiply(BigDecimal.parseString(amount))
                    val tokenFiatValue = rawTokenFiatValue?.formatFiat(currencySymbol).orEmpty()
                    val rawTotalTransactionFiatValue = rawTokenFiatValue?.plus(gasFeeOption.transactionFeeFiatValue)
                    val formattedTotalTransactionFiatValue = rawTotalTransactionFiatValue?.formatFiat(currencySymbol).orEmpty()

                    oldUiState?.copy(
                        estimatedGasLimit = gasLimit,
                        gasPrice = this@BaseEvmStep4VerifyAndSendScreenModel.gasPrice,
                        transactionFeeOptions = transactionFeeOptionUiModels,
                        selectedTransactionFee = gasFeeOption,
                        estimateGasErrorVisible = false,
                        tokenFiatValue = tokenFiatValue,
                        totalTransactionFiatValue = formattedTotalTransactionFiatValue
                    ) ?: it
                }
                delay(TRANSACTION_FEE_REFRESH_INTERVAL)
            }
        }
    }

    private suspend fun getTokenFiatPrice(crypto: TokenBalanceEntity): BigDecimal? {
        nativeCoin.let {
            val tokenPrice = fetchTokenPriceUseCase(
                false,
                mapOf(it.coinUid to crypto),
                false
            ).firstOrNull() ?: return null

            val currentPrice = tokenPrice.currentPrice ?: "0"
            return BigDecimal.parseString(currentPrice)
        }
    }

    private suspend fun calculateGasLimit(
        crypto: TokenBalanceEntity,
        toAddress: String,
        amount: String,
        fromAddress: String
    ): Long? {
        val selectedTransactionFee = (_uiState.value as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.selectedTransactionFee?.transactionFee

        return if (crypto.isCoin) {
            SEND_NATIVE_COIN_GAS_LIMIT
        } else {
            val transactionData = buildTransferTransactionData(
                Address(crypto.contractAddress),
                Address(toAddress),
                amount.amountToBigInt()
            )

            estimateGasUseCase.invoke(
                rpcUrl,
                currentId.getAndIncrement(),
                Address(fromAddress),
                Address(toAddress),
                amount.amountToBigInt(),
                getPreferredGasPrice(selectedTransactionFee),
                transactionData
            )
        }
    }

    private fun sendCoin() {
        (_uiState.value as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.let {
            screenModelScope.launch {
                val selectedTransactionFee = it.selectedTransactionFee?.transactionFee

                val preferredGasPrice = getPreferredGasPrice(selectedTransactionFee)


                val txHash = sendTokenUseCase.sendCoin(
                    accountId = accountId,
                    blockchainType = blockchainType,
                    addressType = AddressType.Bip44, // TODO: Network support - support multiple address types
                    to = recipientAddress,
                    amount = amount,
                    gasPrice = preferredGasPrice,
                    rpcUrl = rpcUrl,
                    gasLimit = it.estimatedGasLimit,
                    coinSymbol = it.selectedToken.contractSymbol
                )
                _uiState.update {
                    (it as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.copy(txHash = txHash) ?: it
                }
            }
        }
    }

    private fun sendToken() {
        (_uiState.value as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.let {
            screenModelScope.launch {
                val selectedTransactionFee =
                    (_uiState.value as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.selectedTransactionFee?.transactionFee

                val preferredGasPrice = getPreferredGasPrice(selectedTransactionFee)


                val txHash = sendTokenUseCase.sendToken(
                    accountId = accountId,
                    blockchainType = blockchainType,
                    addressType = AddressType.Bip44, // TODO: Network support - Support multiple address types
                    to = recipientAddress,
                    amount = amount,
                    gasPrice = preferredGasPrice,
                    rpcUrl = rpcUrl,
                    gasLimit = it.estimatedGasLimit,
                    contractAddress = it.selectedToken.contractAddress,
                    tokenSymbol = it.selectedToken.contractSymbol
                )
                _uiState.update {
                    (it as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.copy(txHash = txHash) ?: it
                }
            }
        }
    }

    protected fun buildSendTransactionData(
        to: Address,
        value: BigInteger
    ): TransactionData {
        return TransactionData(
            to = to,
            value = value,
            ByteArray(0)
        )
    }

    protected fun buildTransferTransactionData(
        contractAddress: Address,
        to: Address,
        value: BigInteger
    ): TransactionData {
        return TransactionData(
            to = contractAddress,
            value = BigInteger.ZERO,
            TransferMethod(to, value).encodedABI()
        )
    }

    // TODO: Should only be for pro variant
    fun onAuthenticationSuccess() {
        val currentUiState = _uiState.value
        if (currentUiState !is BaseEvmStep4VerifyAndSendScreenUiState.Data) return

        if (currentUiState.selectedToken.isCoin) {
            sendCoin()
        } else {
            sendToken()
        }
    }

    // TODO: Should only be for UI variant
    fun stopGasRefreshJob() {
        refreshJob?.cancel()
    }

    fun restartGasRefreshJob() {
        calculateTransactionFee(
            currentAccount?.bip44Address.orEmpty(),
            recipientAddress,
            amount,
            token
        )
    }

    companion object {
        private const val SEND_NATIVE_COIN_GAS_LIMIT = 21000L
    }
}
