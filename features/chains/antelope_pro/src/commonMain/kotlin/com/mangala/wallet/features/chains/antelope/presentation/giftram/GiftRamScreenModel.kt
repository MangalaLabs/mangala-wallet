package com.mangala.wallet.features.chains.antelope.presentation.giftram

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.RAM_TRANSACTION_MINIMUM_AMOUNT_IN_KILOBYTES
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckAccountNotExistsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountWithBalanceInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.giftram.GiftRamUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.BaseAntelopeTransactScreenModel
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AmountInputValidationUtils
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.RecipientValidationStatus
import com.mangala.wallet.ui.component.RamSuggestionInputUiModel
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.ext.kilobytesToBytes
import com.mangala.wallet.utils.toBigDecimalOrZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor

class GiftRamScreenModel(
    private val accountName: String,
    private val giftRamUseCase: GiftRamUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val validateAccountUseCase: ValidateAccountUseCase,
    private val checkAccountNotExistsUseCase: CheckAccountNotExistsUseCase,
) : BaseAntelopeTransactScreenModel(transactUseCase = giftRamUseCase, blockchainUid = "") {

    private val _uiState: MutableStateFlow<GiftRamUiState> =
        MutableStateFlow(
            GiftRamUiState.Success(
                uiModel = GiftRamUiModel(
                    accountInfo = null
                )
            )
        )
    val uiState: StateFlow<GiftRamUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    private var loadAccountJob: Job? = null
    private var accountNameInputValidationJob: Job? = null

    init {
        loadAccountJob = screenModelScope.launch {
            blockchainUid = getSelectedNetworkUseCase().blockChainUid

            loadAccount(forceReload = false)
        }
    }

    fun pullToRefresh() {
        loadAccountJob?.cancel()
        loadAccountJob = screenModelScope.launch {
            _isRefreshing.value = true
            loadAccount(true)
            _isRefreshing.value = false
        }
    }

    fun continueTransaction() {
        loadAccountJob?.cancel()
        loadAccountJob = screenModelScope.launch {
            loadAccount(false)
        }
    }

    private suspend fun loadAccount(forceReload: Boolean = false) {
        collectAccount(
            accountName = accountName,
            forceReload = forceReload
        )
    }

    private suspend fun collectAccount(
        accountName: String,
        forceReload: Boolean
    ) {
        getAccountInfoUseCase.invokeFlow(accountName, forceReload).collectLatest { response ->
            _uiState.update { currentState ->
                (currentState as? GiftRamUiState.Success)?.let {
                    GiftRamUiState.Success(
                        uiModel = it.uiModel.copy(
                            accountInfo = response.data
                        )
                    )
                } ?: currentState
            }
        }
    }

    fun onSelectSuggestionInput(suggestionInputUiModel: RamSuggestionInputUiModel) {
        val currentState = _uiState.value as? GiftRamUiState.Success ?: return
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
                val totalRam = uiModel.ramAvailable
                val percent = suggestionInputUiModel.amount / 100.0
                val ramAmount = (totalRam * percent).toBigDecimal()
                    .roundToDigitPositionAfterDecimalPoint(
                        BUY_RAM_BYTES_DISPLAY_SCALE,
                        RoundingMode.FLOOR
                    )
                onRamAmountChange(ramAmount.toPlainString())
            }

            RamSuggestionInputUiModel.Quantity.Eos -> TODO()
            RamSuggestionInputUiModel.Quantity.Kb -> onRamAmountChange(suggestionInputUiModel.amount.toString())
        }
    }

    fun onRamAmountChange(value: String) {
        if (AmountInputValidationUtils.isValidInput(
                value,
                precision = BUY_RAM_BYTES_DISPLAY_SCALE.toInt()
            ).not()
        ) return
        val currentState = _uiState.value as? GiftRamUiState.Success ?: return
        val valueBigDecimal = value.toBigDecimalOrZero()

        _uiState.update {
            val insufficientInputAmountError =
                when {
                    valueBigDecimal > currentState.uiModel.ramAvailable.toBigDecimal() -> {
                        WrappedStringResource.StringRes(
                            MR.strings.message_buy_sell_ram_insufficient,
                            "RAM"
                        )
                    }

                    valueBigDecimal > 0 && valueBigDecimal < RAM_TRANSACTION_MINIMUM_AMOUNT_IN_KILOBYTES.toBigDecimal() -> {
                        val decimalFormat = DecimalFormat("#.###")
                        val minimumAmountString = decimalFormat.format(0.001)

                        WrappedStringResource.StringRes(
                            MR.strings.message_transfer_ram_min_amount_error,
                            "$minimumAmountString KB"
                        )
                    }

                    else -> null
                }
            GiftRamUiState.Success(
                uiModel = (it as GiftRamUiState.Success).uiModel.copy(
                    ramAmountText = value,
                    inputAmountError = insufficientInputAmountError
                )
            )
        }
    }

    fun onRecipientAccountChange(value: String) {
        val currentState = (_uiState.value as? GiftRamUiState.Success)?.uiModel ?: return
        val trimmedValue = value.trim()

        _uiState.update {
            GiftRamUiState.Success(
                uiModel = currentState.copy(
                    recipientAccountText = trimmedValue,
                    recipientAccountNameValidationStatus = RecipientValidationStatus.NotValidated
                )
            )
        }

        if (trimmedValue.isEmpty()) return

        accountNameInputValidationJob?.cancel()
        accountNameInputValidationJob = screenModelScope.launch {
            delay(ACCOUNT_NAME_INPUT_DEBOUNCE_PERIOD)

            _uiState.update {
                val lastState = (it as? GiftRamUiState.Success)

                lastState?.copy(
                    uiModel = lastState.uiModel.copy(
                        recipientAccountNameValidationStatus = RecipientValidationStatus.Validating
                    )
                ) ?: it
            }

            val validationStatus = withContext(Dispatchers.IO) {
                checkIsInvalidAccountName(trimmedValue)
            }

            _uiState.update {
                val lastState = (it as? GiftRamUiState.Success)

                lastState?.copy(
                    uiModel = lastState.uiModel.copy(
                        recipientAccountNameValidationStatus = validationStatus
                    )
                ) ?: it
            }
        }
    }

    private suspend fun checkIsInvalidAccountName(accountName: String): RecipientValidationStatus {
        return if (accountName.length <= 12 && AccountNameType.getAccountNameType(accountName) != AccountNameType.None) {
            if (validateAccountUseCase.validateAccountName(accountName)) {
                if (checkAccountNotExistsUseCase(blockchainType, accountName)) {
                    RecipientValidationStatus.Invalid
                } else {
                    RecipientValidationStatus.Valid
                }
            } else RecipientValidationStatus.Invalid
        } else {
            RecipientValidationStatus.Invalid
        }
    }

    fun onMemoChange(value: String) {
        _uiState.update {
            GiftRamUiState.Success(
                uiModel = (it as GiftRamUiState.Success).uiModel.copy(
                    memoText = value,
                )
            )
        }
    }

    override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
        val uiState = (_uiState.value as? GiftRamUiState.Success) ?: return
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
        _uiState.value = GiftRamUiState.Error("Failed to transfer RAM")
    }

    override fun onRequestTransactionResourceCovered() {
        val uiState = (_uiState.value as? GiftRamUiState.Success) ?: return
        val uiModel = uiState.uiModel

        _uiState.value =
            uiState.copy(uiModel = uiModel.copy(showPinPrompt = true, isLoading = false))
    }

    override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
        val currentState = _uiState.value as? GiftRamUiState.Success
        if (_uiState.value !is GiftRamUiState.Success) return Result.failure(
            Exception("Invalid state")
        )

        return giftRamUseCase.requestGiftRamTransaction(
            blockchainType = blockchainType,
            senderAccountName = accountName,
            quantityInBytes = getQuantityInBytes(currentState?.uiModel?.ramAmountText ?: ""),
            recipientAccountName = currentState?.uiModel?.recipientAccountText ?: "",
            memo = currentState?.uiModel?.memoText ?: ""
        )
    }

    override fun onDismissTransactionFeeBreakdown() {
        val currentState = _uiState.value as? GiftRamUiState.Success ?: return
        _uiState.update {
            GiftRamUiState.Success(
                uiModel = currentState.uiModel.copy(
                    resourceRequiredBreakdown = null,
                    resourceRequiredTotal = null
                )
            )
        }
    }

    override fun onPinPromptShown() {
        val currentState = _uiState.value as? GiftRamUiState.Success ?: return

        _uiState.update {

            GiftRamUiState.Success(
                uiModel = currentState.uiModel.copy(
                    showPinPrompt = false
                )
            )
        }
    }

    override fun onConfirmResourceProviderFee() {
        val currentState = _uiState.value as? GiftRamUiState.Success ?: return
        val uiModel = currentState.uiModel

        _uiState.value =
            currentState.copy(uiModel = uiModel.copy(showPinPrompt = true, isLoading = false))
    }

    override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
        val currentState = _uiState.value as? GiftRamUiState.Success

        if (_uiState.value !is GiftRamUiState.Success) return Result.failure(Exception("Invalid state"))


        return giftRamUseCase.giftRam(
            blockchainType = blockchainType,
            senderAccountName = accountName,
            recipientAccountName = currentState?.uiModel?.recipientAccountText ?: "",
            quantityInBytes = getQuantityInBytes(currentState?.uiModel?.ramAmountText ?: ""),
            memo = currentState?.uiModel?.memoText ?: ""
        )
    }

    override fun showLoadingState() {
        val uiState = (_uiState.value as GiftRamUiState.Success)
        val uiModel = uiState.uiModel

        _uiState.value = uiState.copy(uiModel = uiModel.copy(isLoading = true))
    }

    override fun onPushTransactionSuccess(txHash: String) {
        loadAccountJob?.cancel()
        _uiState.value = GiftRamUiState.ExecuteRamTransferSuccess(txHash)
    }

    override fun onPushTransactionFail(throwable: Throwable) {
        _uiState.value = GiftRamUiState.Error(throwable.message ?: "Failed to buy/sell RAM")
    }

    private fun handleFailure(message: String) {
        _uiState.value = GiftRamUiState.Error(message)
    }

    private fun getQuantityInBytes(ramAmountText: String) = ramAmountText.toDoubleOrNull()
        ?.let { floor(it.kilobytesToBytes()).toLong() } ?: 0L

    companion object {
        private const val ACCOUNT_NAME_INPUT_DEBOUNCE_PERIOD = 300L
        private const val RAM_AMOUNT_DECIMAL_FORMAT_PATTERN = "#.####"
        private const val RECEIVER_ACCOUNT_NAME_INPUT_DEBOUNCE_PERIOD = 300L
        private const val RAM_FEE_PERCENTAGE = 0.005 // 0.5%
        private const val BUY_RAM_BYTES_BUFFER = 0.001 // 0.1% buffer
        const val BUY_RAM_BYTES_DISPLAY_SCALE = 3L
        const val BUY_RAM_BYTES_SCALE = 4L
    }
}