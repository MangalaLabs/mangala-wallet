package com.mangala.wallet.features.swap_base.presentation.preview

import cafe.adriel.voyager.core.model.screenModelScope
import com.benasher44.uuid.uuid4
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.balance.usecases.GetTokenBalanceByTokenIdUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.domain.transaction.fee.TransactionFeeType
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.AllowanceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.ApproveUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EstimateGasUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetLatestBlockUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetNonceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetRecommendedGasPriceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetTransactionFeeOptionsUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SwapTokenUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionType
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.features.chains.evmcompatible.utils.Numeric
import com.mangala.wallet.features.chains.ui.SendTransactionScreenModel
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.TextFieldState
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.Token
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.uniswap.domain.models.TradeOptions
import com.mangala.wallet.uniswap.domain.models.TradeType
import com.mangala.wallet.uniswap.domain.usecase.GetSwapTradeDataUseCase
import com.mangala.wallet.uniswap.domain.usecase.GetSwapTransactionDataUseCase
import com.mangala.wallet.utils.ext.ethToWei
import com.mangala.wallet.utils.ext.removeTrailingZeroes
import com.mangala.wallet.utils.ext.weiToEth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication.Companion.init
import org.koin.core.component.KoinComponent

class PreviewSwapTokenScreenModel(
    tradeDataFromSwapScreen: TradeData,
    private val accountAddress: String,
    accountId: String,
    private val dex: Dex,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val getSwapTradeDataUseCase: GetSwapTradeDataUseCase,
    private val getSwapTransactionDataUseCase: GetSwapTransactionDataUseCase,
    getNativeCoinUseCase: GetNativeCoinUseCase,
    fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    getTransactionFeeOptionsUseCase: GetTransactionFeeOptionsUseCase,
    getTokenBalanceByTokenIdUseCase: GetTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase: GetRecommendedGasPriceUseCase,
    private val swapTokenUseCase: SwapTokenUseCase,
    private val estimateGasUseCase: EstimateGasUseCase,
    private val allowanceUseCase: AllowanceUseCase,
    private val approveUseCase: ApproveUseCase,
    getLatestBlockUseCase: GetLatestBlockUseCase,
    private val getNonceUseCase: GetNonceUseCase,
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase
) : SendTransactionScreenModel(
    fetchTokenPriceUseCase,
    getNativeCoinUseCase,
    getTokenBalanceByTokenIdUseCase,
    getRecommendedGasPriceUseCase,
    getTransactionFeeOptionsUseCase,
    getCurrentCurrencyCodeUseCase,
    getLatestBlockUseCase
), KoinComponent {

    private var jobSyncApprove: Job? = null
    private var latestNonce: Long? = null

    init {
        this.accountId = accountId
        screenModelScope.launch {
            val selectedNetwork = getSelectedNetworkUseCase()
            val selectNetworkBlockchainUid = selectedNetwork.blockChainUid
            this@PreviewSwapTokenScreenModel.blockchainType = BlockchainType.fromUid(selectNetworkBlockchainUid)
            val routerAddress = Address(
                dex.addresses.firstOrNull { it.first == chain }?.second?.routerAddress ?: ""
            )
            checkApprove(routerAddress)
        }
    }

    private var tradeData: TradeData = tradeDataFromSwapScreen

    private val _amountIn = MutableStateFlow(tradeData.amountIn)
    val amountIn = _amountIn.asStateFlow()

    private val _amountOut = MutableStateFlow(tradeData.amountOut)
    val amountOut = _amountOut.asStateFlow()

    private val _spendingCap = MutableStateFlow("")
    val spendingCap = _spendingCap.asStateFlow()

    private val _uiState = MutableStateFlow<PreviewSwapUiState>(PreviewSwapUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private lateinit var transactionData: TransactionData

    fun executeSwap() {
        val uiState = uiState.value as? PreviewSwapUiState.Success ?: return
        val uiModel = uiState.previewSwapUiModel
        screenModelScope.launch {
            val selectedNetwork = getSelectedNetworkUseCase()
            val selectNetworkBlockchainUid = selectedNetwork.blockChainUid
            val blockchainType = BlockchainType.fromUid(selectNetworkBlockchainUid)

            _uiState.value = PreviewSwapUiState.Loading

            val selectedTransactionFee = uiModel.selectedTransactionFeeOption?.transactionFee

            val txHash = swapTokenUseCase(
                blockchainType = blockchainType,
                from = Address(accountAddress),
                transactionData = transactionData,
                gasPrice = getPreferredGasPrice(selectedTransactionFee),
                gas = uiModel.estimatedGasLimit,
            )

            _uiState.value = PreviewSwapUiState.Success(
                previewSwapUiModel = uiModel.copy(
                    txHash = txHash
                )
            )
        }
    }

    private fun syncData() {
        screenModelScope.launch {
            val tradeType = tradeData.type
            val routerAddress = Address(
                dex.addresses.firstOrNull { it.first == chain }?.second?.routerAddress ?: ""
            )
            ensureActive()

            while (isActive) {
                val data = getSwapTradeDataUseCase(
                    blockchainType = blockchainType,
                    dex = dex,
                    id = currentId.getAndIncrement(),
                    address = Address(accountAddress), // TODO: Handle network with different address format
                    tradeType = tradeType,
                    tokenFrom = tradeData.trade.tokenAmountIn.token,
                    tokenTo = tradeData.trade.tokenAmountOut.token,
                    amount = if (tradeType == TradeType.ExactIn) tradeData.amountIn else tradeData.amountOut,
                    tradeOptions = TradeOptions(),
                    forceRefresh = true
                )

                if (data == null) {
                    delay(TRANSACTION_FEE_REFRESH_INTERVAL_MILLIS)
                    continue
                }

                tradeData = data
                if (tradeType == TradeType.ExactIn) {
                    _amountOut.value = tradeData.amountOut
                } else {
                    _amountIn.value = tradeData.amountIn
                }

                val transactionData = getSwapTransactionDataUseCase(
                    blockchainType = blockchainType,
                    dex = dex,
                    tradeData = tradeData,
                    address = Address(accountAddress), // TODO: Handle network with different address format
                    id = currentId.getAndIncrement(),
                )

                this@PreviewSwapTokenScreenModel.transactionData = transactionData
                val transactionFee =
                    (uiState.value as? PreviewSwapUiState.Success)?.previewSwapUiModel?.selectedTransactionFeeOption?.transactionFee

                latestNonce = getNonceUseCase.getNonceLong(
                    blockchainType.getRpcUrl().first(),
                    currentId.getAndIncrement(),
                    Address(accountAddress)
                )

                val gasLimit = estimateGasUseCase.invoke(
                    url = rpcUrl,
                    id = currentId.getAndIncrement(),
                    from = Address(accountAddress),
                    to = routerAddress,
                    amount = if (data.trade.tokenAmountIn.token is Token.Ether) data.amountIn?.toBigInteger()
                        ?: BigInteger.ZERO else BigInteger.ZERO,
                    gasPrice = getPreferredGasPrice(transactionFee),
                    transactionData = transactionData
                )?.getBufferedGasLimit()

                if (gasLimit == null) {
                    delay(TRANSACTION_FEE_REFRESH_INTERVAL_MILLIS)
                    continue
                }

                val gasLimitRawValue = BigDecimal.parseString(gasLimit.toString())
                gasLimitInWei = gasLimitRawValue

                (uiState.value as? PreviewSwapUiState.Success)?.let {
                    _uiState.value =
                        PreviewSwapUiState.Success(it.previewSwapUiModel.copy(estimatedGasLimit = gasLimit))
                }

                val feeOptions = getTransactionFeeOptions() ?: return@launch
                val transactionFeeType = TransactionFeeType.REGULAR
                val transactionFeeOptionUiModels = getTransactionFeeOptionUiModels(
                    feeOptions,
                    transactionFeeType,
                    gasLimitInWei
                )
                val gasFeeOption = transactionFeeOptionUiModels?.let { it1 ->
                    getCurrentFeeOptionUiModel(
                        it1,
                        transactionFeeType,
                    )
                }

                if (gasFeeOption == null) {
                    delay(TRANSACTION_FEE_REFRESH_INTERVAL_MILLIS)
                    continue
                }

                (uiState.value as? PreviewSwapUiState.Success)?.let {
                    _uiState.value = PreviewSwapUiState.Success(
                        it.previewSwapUiModel.copy(selectedTransactionFeeOption = gasFeeOption)
                    )
                }

                delay(TRANSACTION_FEE_REFRESH_INTERVAL_MILLIS)
            }
        }
    }

    private fun checkApprove(routerAddress: Address) {
        screenModelScope.launch{
            val result = allowanceUseCase(
                url = rpcUrl,
                id = currentId.getAndIncrement(),
                contractAddress = tradeData.trade.tokenAmountIn.token.address,
                receiveAddress = Address(accountAddress),
                spenderAddress = routerAddress,
                defaultBlockParameter = DefaultBlockParameter.Latest
            )

            result?.let {
                val allowanceInBigDecimal = BigDecimal.fromBigInteger(it)
                if (allowanceInBigDecimal < (tradeData.amountIn ?: BigDecimal.ONE)) {
                    _uiState.value = PreviewSwapUiState.NeedApprove(PreviewSwapApproveUiModel())
                } else {
                    _uiState.value = PreviewSwapUiState.Success(PreviewSwapUiModel())
                    syncData()
                }
            }
        }
    }

    private fun syncApproveData(){
        if (spendingCap.value.isEmpty()) return
        val routerAddress = Address(
            dex.addresses.firstOrNull { it.first == chain }?.second?.routerAddress ?: ""
        )

        this@PreviewSwapTokenScreenModel.transactionData = approveUseCase.approveTransactionData(
            contractAddress = tradeData.trade.tokenAmountIn.token.address,
            spenderAddress = routerAddress,
            amount = BigDecimal.parseString(spendingCap.value)
                .ethToWei(tradeData.trade.tokenAmountIn.token.decimals).toBigInteger()
        )

        val transactionFee =
            (uiState.value as? PreviewSwapUiState.NeedApprove)?.previewSwapApproveUiModel?.selectedTransactionFeeOption?.transactionFee
        jobSyncApprove?.cancel()
        jobSyncApprove = screenModelScope.launch {
            ensureActive()

            while (isActive) {

                val gasLimit = estimateGasUseCase.estimateGas(
                    rpcUrl,
                    currentId.getAndIncrement(),
                    Address(accountAddress),
                    transactionData,
                    getPreferredGasPrice(transactionFee)
                )?.getBufferedGasLimit()

                val gasLimitRawValue = BigDecimal.parseString(gasLimit.toString())
                gasLimitInWei = gasLimitRawValue

                (uiState.value as? PreviewSwapUiState.NeedApprove)?.let {
                    _uiState.value =
                        PreviewSwapUiState.NeedApprove(
                            it.previewSwapApproveUiModel.copy(
                                estimatedGasLimit = gasLimit
                            )
                        )
                }

                latestNonce = getNonceUseCase.getNonceLong(
                    blockchainType.getRpcUrl().first(),
                    currentId.getAndIncrement(),
                    Address(accountAddress)
                )

                val feeOptions = getTransactionFeeOptions() ?: return@launch
                val transactionFeeType = TransactionFeeType.REGULAR
                val transactionFeeOptionUiModels = getTransactionFeeOptionUiModels(
                    feeOptions,
                    transactionFeeType,
                    gasLimitInWei
                )
                val gasFeeOption = transactionFeeOptionUiModels?.let { it1 ->
                    getCurrentFeeOptionUiModel(
                        it1,
                        transactionFeeType,
                    )
                }

                if (gasLimit == null || gasFeeOption == null) {
                    delay(TRANSACTION_FEE_REFRESH_INTERVAL_MILLIS)
                    continue
                }

                (uiState.value as? PreviewSwapUiState.NeedApprove)?.let {
                    _uiState.value = PreviewSwapUiState.NeedApprove(
                        it.previewSwapApproveUiModel.copy(selectedTransactionFeeOption = gasFeeOption)
                    )
                }

                delay(TRANSACTION_FEE_REFRESH_INTERVAL_MILLIS)
            }
        }
    }

    fun getSignTransactionRequest(): SignTransactionRequest? {
        (_uiState.value as? PreviewSwapUiState.Success)?.let {
            val wallet = getSelectedWalletUseCase()

            return SignTransactionRequest(
                requestId = uuid4().toString(),
                walletId = wallet?.id ?: return null,
                accountId = accountId,
                fromAddress = accountAddress,
                nonce = latestNonce ?: 0,
                blockchainType = chain.toBlockchainType(),
                transactionData = transactionData,
                gasPrice = getPreferredGasPrice(it.previewSwapUiModel.selectedTransactionFeeOption?.transactionFee),
                gasLimit = it.previewSwapUiModel.estimatedGasLimit ?: return null,
                transactionType = SignTransactionType.Swap(
                    fromToken = "",  // TODO: #323 pass in data
                    toToken = "",
                    fromAmount = "",
                    toAmount = ""
                ),
                gasFiatValue = it.previewSwapUiModel.selectedTransactionFeeOption?.transactionFeeFiatValueString.orEmpty(),
                contactName = null,
                contactAddress = null
            )
        } ?: return null
    }

    fun getApproveSignTransactionRequest(): SignTransactionRequest? {
        (_uiState.value as? PreviewSwapUiState.NeedApprove)?.let {
            val wallet = getSelectedWalletUseCase()

            return SignTransactionRequest(
                requestId = uuid4().toString(),
                walletId = wallet?.id ?: return null,
                accountId = accountId,
                fromAddress = accountAddress,
                nonce = latestNonce ?: 0,
                blockchainType = chain.toBlockchainType(),
                transactionData = transactionData,
                gasPrice = getPreferredGasPrice(it.previewSwapApproveUiModel.selectedTransactionFeeOption?.transactionFee),
                gasLimit = it.previewSwapApproveUiModel.estimatedGasLimit ?: return null,
                transactionType = SignTransactionType.Erc20Approve(
                    token = "", // TODO: #323 pass in data
                    amount = ""
                ),
                gasFiatValue = it.previewSwapApproveUiModel.selectedTransactionFeeOption?.transactionFeeFiatValueString.orEmpty(),
                contactName = null,
                contactAddress = null
            )
        } ?: return null
    }

    fun setMaxSpendingCap(){
        val max = BigInteger.parseString(
            "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
            base = 16
        ).toString()
        onSpendingCapChanged(
            BigDecimal.parseString(max).weiToEth(tradeData.trade.tokenAmountIn.token.decimals)
                .removeTrailingZeroes().toPlainString()
        )
    }

    fun onSpendingCapChanged(spendingCap: String){
        _spendingCap.value = spendingCap
        val uiState = uiState.value as? PreviewSwapUiState.NeedApprove ?: return
        if (spendingCap.isBlank()) {
            _uiState.value = uiState.copy(
                previewSwapApproveUiModel = uiState.previewSwapApproveUiModel.copy(spendingCapTextFieldState = TextFieldState.Empty)
            )
            return
        }
        if (spendingCap.toBigDecimal() < (tradeData.amountIn ?: BigDecimal.ONE)) {
            _uiState.value = uiState.copy(
                previewSwapApproveUiModel = uiState.previewSwapApproveUiModel.copy(spendingCapTextFieldState = TextFieldState.Wrong)
            )
        } else {
            _uiState.value = uiState.copy(
                previewSwapApproveUiModel = uiState.previewSwapApproveUiModel.copy(spendingCapTextFieldState = TextFieldState.Correct)
            )
        }
        if (uiState.previewSwapApproveUiModel.isFocus.not()) syncApproveData()
    }

    fun onFocusSpendingCapChanged(isFocus: Boolean){
        val uiState = uiState.value as? PreviewSwapUiState.NeedApprove ?: return
        _uiState.value = uiState.copy(
            previewSwapApproveUiModel = uiState.previewSwapApproveUiModel.copy(isFocus = isFocus)
        )
        if (isFocus) {
            jobSyncApprove?.cancel()
        } else syncApproveData()
    }

    fun approve(){
        val uiState = uiState.value as? PreviewSwapUiState.NeedApprove ?: return
        val uiModel = uiState.previewSwapApproveUiModel
        val selectedTransactionFee = uiModel.selectedTransactionFeeOption?.transactionFee
        jobSyncApprove?.cancel()
        screenModelScope.launch{
            _uiState.value = PreviewSwapUiState.Loading
            val txHash = approveUseCase(
                blockchainType = blockchainType,
                from = Address(accountAddress),
                gasPrice = getPreferredGasPrice(selectedTransactionFee),
                transactionData = transactionData,
                gas = uiModel.estimatedGasLimit
            )

            if (txHash?.isEmpty() == false) {
                _uiState.value =
                    PreviewSwapUiState.Success(previewSwapUiModel = PreviewSwapUiModel())
                syncData()
            } else {
                _uiState.value = uiState
                syncApproveData()
            }
        }
    }

    companion object {
        private const val TRANSACTION_FEE_REFRESH_INTERVAL_MILLIS = 30_000L
    }
}