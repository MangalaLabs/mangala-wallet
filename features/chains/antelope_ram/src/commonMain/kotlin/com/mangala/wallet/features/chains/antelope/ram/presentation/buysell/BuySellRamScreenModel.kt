package com.mangala.wallet.features.chains.antelope.ram.presentation.buysell

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.model.AntelopeRamOhlcData
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamChartUseCase
import com.mangala.antelope.base.model.RamMarketData
import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.ram.domain.usecase.BuySellRamUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.RAM_BUY_SELL_MINIMUM_VALUE_IN_NATIVE_COIN
import com.mangala.wallet.features.chains.antelope_base.domain.RAM_TRANSACTION_MINIMUM_AMOUNT_IN_KILOBYTES
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckAccountNotExistsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountWithBalanceInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamPriceUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.BaseAntelopeTransactScreenModel
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AmountInputValidationUtils
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.RecipientValidationStatus
import com.mangala.wallet.ui.component.RamSuggestionInputUiModel
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.calculatingDecimalMode
import com.mangala.wallet.utils.ext.divideSafe
import com.mangala.wallet.utils.ext.format
import com.mangala.wallet.utils.ext.kilobytesToBytes
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.max
import com.mangala.wallet.utils.toBigDecimalOrZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor

class BuySellRamScreenModel(
    private val getAccountInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val getRamChartUseCase: GetRamChartUseCase,
    private val getRamPriceUseCase: GetRamPriceUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val buySellRamUseCase: BuySellRamUseCase,
    private val accountName: String,
    private val isBuyRam: Boolean,
    private val checkAccountNotExistsUseCase: CheckAccountNotExistsUseCase,
    private val validateAccountUseCase: ValidateAccountUseCase,
) : BaseAntelopeTransactScreenModel(
    transactUseCase = buySellRamUseCase,
    blockchainUid = ""
) {
    private val _uiState = MutableStateFlow<BuySellRamUiState>(
        BuySellRamUiState.Success(
            BuySellRamUiModel(
                ramMarketData = RamMarketData(
                    price = BigDecimal.ZERO,
                    currency = "",
                    unallocatedRam = 0,
                    eosPool = BigDecimal.ZERO,
                    supplyRamCore = BigDecimal.ZERO
                )
            )
        )
    )
    val uiState: StateFlow<BuySellRamUiState> get() = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    private var loadAccountJob: Job? = null
    private var receiverAccountNameInputJob: Job? = null

    private val listSuggestionInput = listOf(
        RamSuggestionInputUiModel(10, RamSuggestionInputUiModel.Quantity.Percent),
        RamSuggestionInputUiModel(15, RamSuggestionInputUiModel.Quantity.Percent),
        RamSuggestionInputUiModel(25, RamSuggestionInputUiModel.Quantity.Percent),
        RamSuggestionInputUiModel(50, RamSuggestionInputUiModel.Quantity.Percent),
        RamSuggestionInputUiModel(75, RamSuggestionInputUiModel.Quantity.Percent),
        RamSuggestionInputUiModel(100, RamSuggestionInputUiModel.Quantity.Percent)
    )

    init {
        loadAccountJob = screenModelScope.launch {
            blockchainUid = getSelectedNetworkUseCase().blockChainUid
            loadData()
        }
    }

    fun pullToRefresh() {
        _isRefreshing.value = true
        loadData(true)
        _isRefreshing.value = false

    }

    fun continueTransaction() {
        loadData()
    }

    private fun loadData(forceReload: Boolean = false) {
        loadAccountJob?.cancel()
        loadAccountJob = screenModelScope.launch {
            setInitialState()

            _uiState.update { currentState ->
                when (currentState) {
                    is BuySellRamUiState.Success -> {
                        BuySellRamUiState.Success(
                            currentState.uiModel.copy(
                                isLoading = true,
                                suggestionInputUiModels = listSuggestionInput
                            )
                        )
                    }
                    else -> {
                        BuySellRamUiState.Success(
                            BuySellRamUiModel(
                                ramMarketData = RamMarketData(
                                    price = BigDecimal.ZERO,
                                    currency = "",
                                    unallocatedRam = 0,
                                    eosPool = BigDecimal.ZERO,
                                    supplyRamCore = BigDecimal.ZERO
                                ),
                                suggestionInputUiModels = listSuggestionInput,
                                isLoading = true,
                            )
                        )
                    }
                }
            }

            combine(
                getRamPriceUseCase.invokeFlow(blockchainType, forceReload),
                getRamChartUseCase.getOhlcFlow(blockchainType, SamplingInterval.ONE_HOUR),
                getAccountInfoUseCase.invokeFlow(accountName, blockchainType, forceRefresh = forceReload)
            ) { ramPriceResource, ramChartResource, accountInfoResource ->
                Triple(ramPriceResource, ramChartResource, accountInfoResource)
            }.collectLatest { (ramPriceResource, ramChartResource, accountInfoResource) ->
                updateUiWithAvailableData(ramPriceResource, ramChartResource, accountInfoResource)
            }
        }
    }

    private fun updateUiWithAvailableData(
        ramPriceResource: Resource<RamMarketData?>,
        ramChartResource: Resource<AntelopeRamOhlcData>,
        accountInfoResource: Resource<AntelopeAccount?>
    ) {
        val ramPrice = ramPriceResource.data
        val ramChart = ramChartResource.data
        val accountInfo = accountInfoResource.data

        val isLoading = ramPriceResource is Resource.Loading ||
                ramChartResource is Resource.Loading ||
                accountInfoResource is Resource.Loading

        _uiState.update { currentState ->
            (currentState as? BuySellRamUiState.Success)?.let { state ->
                BuySellRamUiState.Success(
                    uiModel = state.uiModel.copy(
                        ramMarketData = ramPrice ?: state.uiModel.ramMarketData,
                        ram24hPnl = ramChart?.getPriceChangePercentage24h() ?: state.uiModel.ram24hPnl,
                        accountInfo = accountInfo ?: state.uiModel.accountInfo,
                        eosBalance = accountInfo?.safeCoreBalance ?: state.uiModel.eosBalance,
                        nativeToken = accountInfo?.safeCoreBalance?.symbol ?: state.uiModel.nativeToken,
                        isLoading = isLoading,
                    )
                )
            } ?: currentState
        }

        val currentState = _uiState.value as? BuySellRamUiState.Success ?: return
        val uiModel = currentState.uiModel

        if (uiModel.isInputtingEos && uiModel.nativeCoinAmount.isNotBlank()) {
            onEosValueChange(uiModel.nativeCoinAmount)
        } else if (!uiModel.isInputtingEos && uiModel.ramAmount.isNotBlank()) {
            onRamValueChange(uiModel.ramAmount)
        }
    }

    private fun setInitialState() {
        _uiState.update { currentState ->
            BuySellRamUiState.Success(
                BuySellRamUiModel(
                    ramMarketData = RamMarketData(
                        price = BigDecimal.ZERO,
                        currency = "",
                        unallocatedRam = 0,
                        eosPool = BigDecimal.ZERO,
                        supplyRamCore = BigDecimal.ZERO
                    ),
                    suggestionInputUiModels = listSuggestionInput
                )
            )
        }
    }

    private suspend fun collectRamPnl() {
        val result = getRamChartUseCase.getOhlc(blockchainType, SamplingInterval.ONE_HOUR)

        _uiState.update { currentState ->
            (currentState as? BuySellRamUiState.Success)?.let {
                BuySellRamUiState.Success(
                    uiModel = it.uiModel.copy(
                        ram24hPnl = result.getOrNull()?.getPriceChangePercentage24h()
                    )
                )
            } ?: currentState
        }
    }

    private suspend fun collectAccountInfo(forceReload: Boolean) {
        getAccountInfoUseCase.invokeFlow(accountName, blockchainType, forceRefresh = forceReload)
            .collectLatest { accountInfo ->
                accountInfo.data?.let { accountInfor ->
                    val nativeTokenSymbol = accountInfor.safeCoreBalance.symbol

                    _uiState.update { currentState ->
                        (currentState as? BuySellRamUiState.Success)?.let { uiState ->
                            BuySellRamUiState.Success(
                                uiModel = uiState.uiModel.copy(
                                    accountInfo = accountInfor,
                                    eosBalance = accountInfor.safeCoreBalance,
                                    nativeToken = nativeTokenSymbol
                                )
                            )
                        } ?: currentState
                    }
                } ?: handleFailure("Failed to fetch account info")
            }

    }

    private fun handleFailure(message: String) {
        _uiState.value = BuySellRamUiState.Error(message)
    }

    private suspend fun collectRamPrice(forceReload: Boolean) {
        val result = getRamPriceUseCase(blockchainType, forceRefresh = forceReload)

        _uiState.update { currentState ->
            (currentState as? BuySellRamUiState.Success)?.let {
                BuySellRamUiState.Success(uiModel = it.uiModel.copy(ramMarketData = result))
            } ?: currentState
        }
    }

    fun onRamValueChange(value: String) {
        if (AmountInputValidationUtils.isValidInput(
                value,
                precision = BUY_RAM_BYTES_DISPLAY_SCALE.toInt()
            ).not()
        ) {
            return
        }
        val currentState = _uiState.value as? BuySellRamUiState.Success ?: return
        val uiModel = currentState.uiModel

        if (value.isEmpty()) {
            _uiState.update {
                BuySellRamUiState.Success(
                    uiModel = uiModel.copy(
                        suggestionInputUiModels = listSuggestionInput,
                        ramAmount = "",
                        nativeCoinAmount = "",
                        inputAmountError = null,
                    )
                )
            }
        } else {
            try {
                val amountInKbRam = value.toBigDecimalOrZero()
                val ramPriceBigDecimal = uiModel.ramPrice
                val ramFeeCoefficient = if (isBuyRam) 1 + RAM_FEE_PERCENTAGE else 1.0
                val amountInNativeCoin =
                    amountInKbRam * ramPriceBigDecimal * ramFeeCoefficient.toBigDecimal()
                _uiState.update {
                    val inputAmountError = if (isBuyRam) {
                        getBuyRamErrorString(
                            amountInNativeCoin = amountInNativeCoin,
                            ramPrice = ramPriceBigDecimal,
                            amountInKbRam = amountInKbRam,
                            uiModel = uiModel
                        )
                    } else {
                        getSellRamErrorString(
                            ramPrice = ramPriceBigDecimal,
                            amountInKbRam = amountInKbRam,
                            uiModel = uiModel
                        )
                    }
                    BuySellRamUiState.Success(
                        uiModel = uiModel.copy(
                            inputAmountError = inputAmountError,
                            ramAmount = value,
                            nativeCoinAmount = amountInNativeCoin.toPlainString(),
                        )
                    )
                }
            } catch (e: NumberFormatException) {
                // Handle the error
                e.printStackTrace()
                handleFailure(e.message ?: "Invalid input")
            }
        }
    }

    fun onEosValueChange(value: String) {
        val currentState = _uiState.value as? BuySellRamUiState.Success ?: return
        val uiModel = currentState.uiModel

        if (AmountInputValidationUtils.isValidInput(value, uiModel.eosBalance.precision).not()) {
            return
        }

        if (value.isEmpty()) {
            _uiState.update {
                BuySellRamUiState.Success(
                    uiModel = uiModel.copy(
                        suggestionInputUiModels = listSuggestionInput,
                        ramAmount = "",
                        nativeCoinAmount = "",
                        inputAmountError = null,
                    )
                )
            }
        } else {
            try {
                val valueBigDecimal = value.toBigDecimalOrZero()
                val ramPriceBigDecimal = uiModel.ramPrice
                val amountInKbRam =
                    getKbRamCanBeBoughtWithNativeCoinAmount(valueBigDecimal, ramPriceBigDecimal)
                _uiState.update {
                    val minValueRounded = getRoundedMinKilobytesValueByPrice(ramPriceBigDecimal)

                    val error = when {
                        valueBigDecimal > uiModel.eosBalance.amount.toBigDecimal() -> {
                            WrappedStringResource.StringRes(
                                MR.strings.message_buy_sell_ram_insufficient,
                                uiModel.nativeToken
                            )
                        }

                        amountInKbRam > 0 && amountInKbRam < minValueRounded -> {
                            val minimumAmountInNativeCoin = (minValueRounded.multiply(
                                ramPriceBigDecimal,
                                decimalMode = DecimalMode(
                                    decimalPrecision = 10,
                                    RoundingMode.CEILING,
                                    scale = uiModel.eosBalance.precision.toLong()
                                )
                            ))

                            val decimalPattern = "#".repeat(uiModel.eosBalance.precision)
                            val decimalFormat = DecimalFormat("#.$decimalPattern")
                            val minimumAmountString =
                                decimalFormat.format(
                                    minimumAmountInNativeCoin.doubleValue(
                                        exactRequired = false
                                    )
                                )

                            val resource = if (isBuyRam) {
                                MR.strings.message_buy_sell_ram_minimum_buy_error
                            } else {
                                MR.strings.message_buy_sell_ram_minimum_sell_error
                            }

                            WrappedStringResource.StringRes(
                                resource,
                                "$minimumAmountString ${uiModel.nativeToken}"
                            )
                        }

                        else -> null
                    }

                    BuySellRamUiState.Success(
                        uiModel = uiModel.copy(
                            inputAmountError = error,
                            ramAmount = amountInKbRam.toPlainString(),
                            nativeCoinAmount = value,
                        )
                    )
                }
            } catch (e: NumberFormatException) {
                // Handle the error
                e.printStackTrace()
                handleFailure(e.message ?: "Invalid input")
            }
        }
    }

    fun onIsBuyForOtherChange(isBuyForOther: Boolean) {
        val currentState = _uiState.value as? BuySellRamUiState.Success ?: return
        _uiState.update {
            BuySellRamUiState.Success(
                uiModel = currentState.uiModel.copy(
                    isBuyForOther = isBuyForOther
                )
            )
        }
    }

    fun onReceiveAccountNameChange(receiveAccountName: String) {
        val currentState = (_uiState.value as? BuySellRamUiState.Success)?.uiModel ?: return
        val trimmedValue = receiveAccountName.trim()

        _uiState.update {
            BuySellRamUiState.Success(
                uiModel = currentState.copy(
                    receiveAccountName = trimmedValue,
                    receiverAccountNameValidationStatus = RecipientValidationStatus.NotValidated,
                )
            )
        }

        if (trimmedValue.isEmpty()) return

        receiverAccountNameInputJob?.cancel()
        receiverAccountNameInputJob = screenModelScope.launch {
            delay(RECEIVER_ACCOUNT_NAME_INPUT_DEBOUNCE_PERIOD)

            _uiState.update {
                val lastState =
                    (_uiState.value as? BuySellRamUiState.Success)?.uiModel ?: return@launch

                BuySellRamUiState.Success(
                    uiModel = lastState.copy(
                        receiverAccountNameValidationStatus = RecipientValidationStatus.Validating
                    )
                )
            }

            val validateState = withContext(Dispatchers.IO) {
                checkValidAddress(receiveAccountName)
            }

            _uiState.update {
                val lastState =
                    (_uiState.value as? BuySellRamUiState.Success)?.uiModel ?: return@launch

                BuySellRamUiState.Success(
                    uiModel = lastState.copy(
                        receiverAccountNameValidationStatus = validateState
                    )
                )
            }
        }
    }

    private suspend fun checkValidAddress(
        address: String,
    ): RecipientValidationStatus {
        val isValidAccount =
            validateAccountUseCase.validateAccountName(address)

        val isValidRecipient = if (isValidAccount) checkAccountNotExistsUseCase(
            blockchainType,
            address
        ).not() else false
        return isValidRecipient.toRecipientValidationStatus()

    }

    private fun Boolean.toRecipientValidationStatus() =
        if (this) RecipientValidationStatus.Valid else RecipientValidationStatus.Invalid

    fun reverseRamEosInput() {
        val currentState = _uiState.value as? BuySellRamUiState.Success ?: return
        val uiModel = currentState.uiModel
        val newState = _uiState.updateAndGet {
            BuySellRamUiState.Success(
                uiModel = uiModel.copy(
                    isInputtingEos = uiModel.isInputtingEos.not()
                )
            )
        }

        // Trigger validation
        if ((newState as BuySellRamUiState.Success).uiModel.isInputtingEos) {
            onEosValueChange(newState.uiModel.nativeCoinAmount) // TODO: Account for RAM fee
        } else {
            // TODO: Check when we switch from EOS mode to RAM mode, we need to account for max RAM can be bought
            onRamValueChange(newState.uiModel.ramAmount)
        }
    }

    fun onSelectSuggestionInput(suggestionInputUiModel: RamSuggestionInputUiModel) {
        val currentState = _uiState.value as? BuySellRamUiState.Success ?: return
        _uiState.update {
            currentState.copy(
                uiModel = currentState.uiModel.copy(
                    suggestionInputUiModels = currentState.uiModel.suggestionInputUiModels.map {
                        if (it == suggestionInputUiModel) {
                            it.copy(isSelected = true)
                        } else {
                            it.copy(isSelected = false)
                        }
                    }
                )
            )
        }

        val uiModel = currentState.uiModel
        when (suggestionInputUiModel.quantity) {
            RamSuggestionInputUiModel.Quantity.Percent -> {
                if (uiModel.isInputtingEos || isBuyRam) {
                    handleSelectSuggestionInputRamBuy(uiModel, suggestionInputUiModel)
                } else {
                    handleSelectSuggestionInputRamSell(uiModel, suggestionInputUiModel)
                }
            }

            RamSuggestionInputUiModel.Quantity.Eos -> onEosValueChange(suggestionInputUiModel.amount.toString())
            RamSuggestionInputUiModel.Quantity.Kb -> onRamValueChange(suggestionInputUiModel.amount.toString())
        }
    }

    override fun onDismissTransactionFeeBreakdown() {
        val currentState = _uiState.value as? BuySellRamUiState.Success ?: return
        _uiState.update {
            BuySellRamUiState.Success(
                uiModel = currentState.uiModel.copy(
                    resourceRequiredBreakdown = null,
                    resourceRequiredTotal = null
                )
            )
        }
    }

    override fun onConfirmResourceProviderFee() {
        val currentState = _uiState.value as? BuySellRamUiState.Success ?: return
        val uiModel = currentState.uiModel

        _uiState.value =
            currentState.copy(uiModel = uiModel.copy(showPinPrompt = true, isLoading = false))
    }

    private fun handleSelectSuggestionInputRamBuy(
        uiModel: BuySellRamUiModel,
        suggestionInputUiModel: RamSuggestionInputUiModel
    ) {
        val eosBalance = uiModel.eosBalance.amount.toBigDecimal()
        val percent = suggestionInputUiModel.amount.toBigDecimal()
            .divideSafe(100.toBigDecimal(), calculatingDecimalMode)
        if (uiModel.isInputtingEos) {
            // When using buyRam, the 0.5% fee is deducted from the EOS specified, and the remainder buys as much RAM as possible.
            val eosAmount = (eosBalance * percent).roundToDigitPositionAfterDecimalPoint(
                uiModel.eosBalance.precision.toLong(),
                RoundingMode.FLOOR
            )

            onEosValueChange(eosAmount.toPlainString())
        } else {
            val amountRamInKb = getRamAmountCanBeBoughtFromBalancePercentage(percent, eosBalance, uiModel)

            val ramAmountString = amountRamInKb.format(BUY_RAM_BYTES_DISPLAY_SCALE, RoundingMode.FLOOR, ignoreLocale = true)
            onRamValueChange(ramAmountString)
        }
    }

    private fun handleSelectSuggestionInputRamSell(
        uiModel: BuySellRamUiModel,
        suggestionInputUiModel: RamSuggestionInputUiModel
    ) {
        val totalRam = uiModel.ramAvailable
        val percent = suggestionInputUiModel.amount.toBigDecimal()
            .divideSafe(100.toBigDecimal(), calculatingDecimalMode)
        val ramAmount = ((totalRam ?: BigDecimal.ZERO).times(percent))
            .roundToDigitPositionAfterDecimalPoint(
                BUY_RAM_BYTES_DISPLAY_SCALE,
                RoundingMode.FLOOR
            )
        onRamValueChange(ramAmount.toPlainString())
    }

    private suspend fun pushBuyTransactionWithoutResourceProvider(uiModel: BuySellRamUiModel): Result<String> {
        return if (uiModel.isInputtingEos) {
            buySellRamUseCase.buyRam(
                blockchainType = blockchainType,
                senderAccountName = accountName,
                receiver = getReceiverAccountName(uiModel),
                coinQuantity = Balance(
                    uiModel.nativeCoinAmount.toDouble(),
                    uiModel.nativeToken,
                    uiModel.eosBalance.precision
                )
            )
        } else {
            buySellRamUseCase.buyRamBytes(
                blockchainType = blockchainType,
                senderAccountName = accountName,
                receiver = getReceiverAccountName(uiModel),
                quantityInBytes = uiModel.ramAmount.toDoubleOrNull()
                    ?.let { floor(it.kilobytesToBytes()).toLong() } ?: 0L
            )
        }
    }

    private suspend fun pushSellTransactionWithoutResourceProvider(uiModel: BuySellRamUiModel): Result<String> {
        return buySellRamUseCase.sellRam(
            blockchainType = blockchainType,
            accountName = accountName,
            quantityInBytes = uiModel.ramAmount.toDoubleOrNull()
                ?.let { floor(it.kilobytesToBytes()).toLong() } ?: 0L
        )
    }

    override fun onPinPromptShown() {
        val currentState = _uiState.value as? BuySellRamUiState.Success ?: return

        _uiState.update {
            BuySellRamUiState.Success(
                uiModel = currentState.uiModel.copy(
                    showPinPrompt = false
                )
            )
        }
    }

    override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
        val uiState = (_uiState.value as? BuySellRamUiState.Success) ?: return Result.failure(
            Exception("Invalid state")
        )
        val uiModel = uiState.uiModel

        return if (isBuyRam) {
            pushBuyTransactionWithoutResourceProvider(uiModel)
        } else {
            pushSellTransactionWithoutResourceProvider(uiModel)
        }
    }

    override fun showLoadingState() {
        val uiState = (_uiState.value as? BuySellRamUiState.Success) ?: return
        val uiModel = uiState.uiModel

        _uiState.value = uiState.copy(uiModel = uiModel.copy(isLoading = true))
    }

    override fun onPushTransactionSuccess(txHash: String) {
        _uiState.value = BuySellRamUiState.ExecuteBuySellSuccess(txHash)
    }

    override fun onPushTransactionFail(throwable: Throwable) {
        val currentState = _uiState.value as? BuySellRamUiState.Success ?: return
        val uiModel = currentState.uiModel
        _uiState.update {
            BuySellRamUiState.Success(
                uiModel = uiModel.copy(
                    isLoading = false,
                    inputAmountError = WrappedStringResource.StringRes(
                        MR.strings.message_error_from_node,
                        "${throwable.message}"
                    )
                )
            )
        }
    }

    override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
        val uiState = (_uiState.value as? BuySellRamUiState.Success) ?: return
        val uiModel = uiState.uiModel

        _uiState.value =
            uiState.copy(
                uiModel = uiModel.copy(
                    resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
                    resourceRequiredTotal = resourceProviderResponse.fee,
                    isLoading = false
                )
            )
    }

    override fun onRequestTransactionInvalidRequest() {
        _uiState.value = BuySellRamUiState.Error("Failed to buy sell RAM")
    }

    override fun onRequestTransactionResourceCovered() {
        val uiState = (_uiState.value as? BuySellRamUiState.Success) ?: return
        val uiModel = uiState.uiModel

        _uiState.value =
            uiState.copy(uiModel = uiModel.copy(showPinPrompt = true, isLoading = false))
    }

    override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
        val uiState = (_uiState.value as? BuySellRamUiState.Success) ?: return Result.failure(
            Exception("Invalid state")
        )
        val uiModel = uiState.uiModel

        return if (isBuyRam) {
            if (uiModel.isInputtingEos) {
                buySellRamUseCase.requestBuyRamTransaction(
                    blockchainType = blockchainType,
                    senderAccountName = accountName,
                    receiver = getReceiverAccountName(uiModel),
                    coinQuantity = Balance(
                        uiModel.nativeCoinAmount.toDouble(),
                        uiModel.nativeToken,
                        uiModel.eosBalance.precision
                    )
                )
            } else {
                buySellRamUseCase.requestBuyRamBytesTransaction(
                    blockchainType = blockchainType,
                    senderAccountName = accountName,
                    receiver = getReceiverAccountName(uiModel),
                    quantityInBytes = uiModel.ramAmount.toDoubleOrNull()
                        ?.let { floor(it.kilobytesToBytes()).toLong() } ?: 0L
                )
            }
        } else {
            buySellRamUseCase.requestSellRamTransaction(
                blockchainType = blockchainType,
                accountName = accountName,
                quantityInBytes = uiModel.ramAmount.toDoubleOrNull()
                    ?.let { floor(it.kilobytesToBytes()).toLong() } ?: 0L
            )
        }
    }

    private fun getReceiverAccountName(uiModel: BuySellRamUiModel): String {
        return if (uiModel.isBuyForOther) uiModel.receiveAccountName else accountName
    }

    private fun getBuyRamErrorString(
        amountInNativeCoin: BigDecimal,
        ramPrice: BigDecimal,
        amountInKbRam: BigDecimal,
        uiModel: BuySellRamUiModel
    ): WrappedStringResource? {
        val minValueRounded = getRoundedMinKilobytesValueByPrice(ramPrice)


        return when {
            amountInNativeCoin > uiModel.eosBalance.amount.toBigDecimal() -> {
                val resource =
                    if (uiModel.isInputtingEos) uiModel.nativeToken else "RAM"

                WrappedStringResource.StringRes(
                    MR.strings.message_buy_sell_ram_insufficient,
                    resource
                )
            }

            uiModel.isInputtingEos.not() && amountInKbRam > getRamAmountCanBeBoughtFromBalancePercentage(
                BigDecimal.ONE,
                uiModel.eosBalance.amount.toBigDecimal(),
                uiModel
            ) -> {
                val resource =
                    if (uiModel.isInputtingEos) uiModel.nativeToken else "RAM"

                WrappedStringResource.StringRes(
                    MR.strings.message_buy_sell_ram_insufficient,
                    resource
                )
            }

            amountInKbRam > 0 && amountInKbRam < minValueRounded -> {
                val decimalFormat = DecimalFormat(RAM_AMOUNT_DECIMAL_FORMAT_PATTERN)
                val minimumAmountString =
                    decimalFormat.format(minValueRounded.doubleValue(exactRequired = false))

                WrappedStringResource.StringRes(
                    MR.strings.message_buy_sell_ram_minimum_buy_error,
                    "$minimumAmountString KB"
                )
            }

            else -> null
        }
    }

    private fun getSellRamErrorString(
        ramPrice: BigDecimal,
        amountInKbRam: BigDecimal,
        uiModel: BuySellRamUiModel
    ): WrappedStringResource? {
        val minValueRounded = getRoundedMinKilobytesValueByPrice(ramPrice)

        return when {
            amountInKbRam > uiModel.ramAvailable.orZero() -> WrappedStringResource.StringRes(
                MR.strings.message_buy_sell_ram_insufficient,
                "RAM"
            )

            (amountInKbRam > 0 && amountInKbRam < minValueRounded) -> {
                val decimalFormat = DecimalFormat(RAM_AMOUNT_DECIMAL_FORMAT_PATTERN)
                val minimumAmountString = decimalFormat.format(
                    minValueRounded.doubleValue(exactRequired = false)
                )

                WrappedStringResource.StringRes(
                    MR.strings.message_buy_sell_ram_minimum_sell_error,
                    "$minimumAmountString KB"
                )
            }

            else -> null
        }
    }

    private fun getRoundedMinKilobytesValueByPrice(ramPrice: BigDecimal): BigDecimal {
        val minValueByPrice =
            getKbRamCanBeBoughtWithNativeCoinAmount(
                RAM_BUY_SELL_MINIMUM_VALUE_IN_NATIVE_COIN.toBigDecimal(),
                ramPrice
            )

        val minValue =
            max(minValueByPrice, RAM_TRANSACTION_MINIMUM_AMOUNT_IN_KILOBYTES.toBigDecimal())
        return minValue.roundToDigitPositionAfterDecimalPoint(
            BUY_RAM_BYTES_DISPLAY_SCALE,
            RoundingMode.CEILING
        )
    }

    private fun getRamAmountCanBeBoughtFromBalancePercentage(
        percent: BigDecimal,
        eosBalance: BigDecimal,
        uiModel: BuySellRamUiModel
    ): BigDecimal {
        // With buyRamBytes, if the exact EOS or other native token needed to cover the specified bytes + 0.5% fee exceeds the balance, it fails.
        // So we need to subtract the RAM fee from max amount to account for RAM fees
        val percentAccountedForRamFee =
            percent.minus((RAM_FEE_PERCENTAGE + BUY_RAM_BYTES_BUFFER).toBigDecimal())
        val eosAmount =
            (eosBalance * percentAccountedForRamFee).roundToDigitPositionAfterDecimalPoint(
                uiModel.eosBalance.precision.toLong(),
                RoundingMode.FLOOR
            )
        val amountRamInKb = getKbRamCanBeBoughtWithNativeCoinAmount(eosAmount, uiModel.ramPrice)
        return amountRamInKb
    }

    private fun getKbRamCanBeBoughtWithNativeCoinAmount(
        nativeCoinAmount: BigDecimal,
        ramPriceBigDecimal: BigDecimal
    ) = nativeCoinAmount.divideSafe(
        ramPriceBigDecimal, decimalMode = DecimalMode(
            decimalPrecision = 10, RoundingMode.FLOOR, scale = BUY_RAM_BYTES_SCALE
        )
    ).times((1 - (RAM_FEE_PERCENTAGE + BUY_RAM_BYTES_BUFFER)).toBigDecimal())

    companion object {
        private const val RAM_AMOUNT_DECIMAL_FORMAT_PATTERN = "#.####"
        private const val RECEIVER_ACCOUNT_NAME_INPUT_DEBOUNCE_PERIOD = 300L
        private const val RAM_FEE_PERCENTAGE = 0.005 // 0.5%
        private const val BUY_RAM_BYTES_BUFFER = 0.001 // 0.1% buffer
        const val BUY_RAM_BYTES_DISPLAY_SCALE = 3L
        const val BUY_RAM_BYTES_SCALE = 4L
    }
}
