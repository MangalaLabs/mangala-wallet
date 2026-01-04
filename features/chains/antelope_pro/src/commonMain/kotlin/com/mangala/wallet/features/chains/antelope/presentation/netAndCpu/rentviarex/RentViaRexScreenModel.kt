package com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.rentviarex

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.presentation.powerup.PowerUpScreenUiState
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountWithBalanceInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rentViaRex.GetRexRateUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rentViaRex.RentViaRexUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.BaseAntelopeTransactScreenModel
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AmountInputValidationUtils
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.utils.toBigDecimalOrNull
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RentViaRexScreenModel(
    private val accountName: String,
    private val isCpu: Boolean,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val rentViaRexUseCase: RentViaRexUseCase,
    private val getAccountWithBalanceInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val getRexRateUseCase: GetRexRateUseCase
) : BaseAntelopeTransactScreenModel(
    transactUseCase = rentViaRexUseCase,
    blockchainUid = ""
) {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    private val _uiState: MutableStateFlow<RentViaRexUiState> =
        MutableStateFlow(
            RentViaRexUiState.Loaded(
                amountUnit = if (isCpu) "ms" else "kb"
            )
        )
    val uiState: StateFlow<RentViaRexUiState> = _uiState.asStateFlow()

    private var loadAccountJob: Job? = null

    init {
        loadData()
    }

    fun onPullToRefresh() {
        _isRefreshing.value = true
        loadData(true)
        _isRefreshing.value = false
    }

    fun continueRentViaRex() {
        loadData()
    }

    private fun loadData(forceReload: Boolean = false) {
        loadAccountJob?.cancel()
        loadAccountJob = screenModelScope.launch {
            blockchainUid = getSelectedNetworkUseCase().blockChainUid

            _uiState.value = RentViaRexUiState.Loaded(
                amountUnit = if (isCpu) "ms" else "kb"
            )
            collectAccountInfor(forceReload)
        }
    }

    private suspend fun collectAccountInfor(forceReload: Boolean) {
        val rentViaRexRate =
            if (isCpu) {
                getRexRateUseCase.getCpuPricePerMs(blockchainType, forceRefresh = forceReload)
            } else {
                getRexRateUseCase.getNetPricePerKb(blockchainType, forceRefresh = forceReload)
            }

        getAccountWithBalanceInfoUseCase.invokeFlow(
            accountName = accountName,
            blockchainType = blockchainType,
            forceRefresh = forceReload
        ).collectLatest { accountInfor ->
            accountInfor.data?.let { account ->

                val netWeight = account.totalResources?.netWeight
                val nativeTokenSymbol = netWeight?.symbol.orEmpty()

                val resourceLimit =
                    if (isCpu) account.cpuLimit else account.netLimit
                val resourceUsagePercentage = resourceLimit?.getUsedPercentage()

                _uiState.update { currentState ->
                    (currentState as? RentViaRexUiState.Loaded)?.let { uiModel ->
                        RentViaRexUiState.Loaded(
                            amount = "",
                            amountUnit = if (isCpu) "ms" else "kb",
                            buttonEnabled = false,
                            isLoading = false,
                            rentViaRexRate = rentViaRexRate.getOrNull() ?: BigDecimal.ZERO,
                            resourceUsedPercentage = resourceUsagePercentage,
                            balance = account.safeCoreBalance,
                            nativeToken = nativeTokenSymbol,
                            nativeTokenPrecision = netWeight?.precision ?: 0,
                            promptConfirmTransaction = false,
                            inputSectionEnabled = true
                        )
                    } ?: currentState
                }
            }
        }
    }

    override fun onPinPromptShown() {
        val currentState = _uiState.value as? RentViaRexUiState.Loaded ?: return

        _uiState.update {
            currentState.copy(
                promptConfirmTransaction = false,
                resourceRequiredBreakdown = null,
                resourceRequiredTotal = null,
                buttonEnabled = true
            )
        }
    }

    override fun onDismissTransactionFeeBreakdown() {
        val currentState = _uiState.value as? RentViaRexUiState.Loaded ?: return
        _uiState.update {
            currentState.copy(
                resourceRequiredBreakdown = null,
                resourceRequiredTotal = null,
                isLoading = false,
                buttonEnabled = true
            )
        }
    }

    override fun onConfirmResourceProviderFee() {
        val currentState = _uiState.value as? RentViaRexUiState.Loaded ?: return

        _uiState.value =
            currentState.copy(promptConfirmTransaction = true, isLoading = true)
    }

    override fun onRequestTransactionResourceCovered() {
        val currentState =
            _uiState.value as? RentViaRexUiState.Loaded ?: return

        _uiState.value = currentState.copy(
            promptConfirmTransaction = true,
            isLoading = false,
            buttonEnabled = false
        )
    }

    override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
        val uiState = (_uiState.value as? RentViaRexUiState.Loaded) ?: return

        _uiState.value =
            uiState.copy(
                resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
                resourceRequiredTotal = resourceProviderResponse.fee,
                isLoading = false,
                buttonEnabled = false
            )
    }

    override fun showLoadingState() {
        val uiState = (_uiState.value as? RentViaRexUiState.Loaded) ?: return

        _uiState.update {
            uiState.copy(
                isLoading = true,
                buttonEnabled = false
            )
        }
    }

    override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
        val uiState = uiState.value as? RentViaRexUiState.Loaded ?: return Result.failure(
            Exception(
                "Invalid state"
            )
        )
        val uiModel = uiState
        val rentViaRexRate = uiModel.rentViaRexRate

        return rentViaRexUseCase.requestRentViaRexTransaction(
            blockchainType = blockchainType,
            senderAccountName = accountName,
            receiver = accountName,
            loadAmount = Balance(
                amount = rentViaRexRate?.doubleValue(exactRequired = false)
                    ?.times(uiModel.amount.toDouble()) ?: 0.0,
                symbol = "A"
            ),
            loadFund = Balance(
                amount = 0.0000,
                symbol = "A",
            ),
            isCpu = isCpu
        )
    }

    override fun onRequestTransactionInvalidRequest() {
        _uiState.value = RentViaRexUiState.Error("Failed to rent REX")
    }

    override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
        val uiState = uiState.value as? RentViaRexUiState.Loaded ?: return Result.failure(
            Exception(
                "Invalid state"
            )
        )
        val uiModel = uiState
        val rentViaRexRate = uiModel.rentViaRexRate

        val amountCost = rentViaRexRate
            ?.multiply(uiModel.amount.toBigDecimal()) ?: BigDecimal.ZERO

        return rentViaRexUseCase.pushRentViaRexTransaction(
            blockchainType = blockchainType,
            senderAccountName = accountName,
            receiver = accountName,
            loadAmount = Balance(
                amount = amountCost.doubleValue(exactRequired = false),
                symbol = "A",
                precision = uiModel.nativeTokenPrecision
            ),
            loadFund = Balance(
                amount = 0.0000,
                symbol = "A",
                precision = uiModel.nativeTokenPrecision
            ),
            isCpu = isCpu
        )
    }

    override fun onPushTransactionSuccess(txHash: String) {
        _uiState.value = RentViaRexUiState.ExecuteRentViaRexSuccess(txHash)
    }

    override fun onPushTransactionFail(throwable: Throwable) {
        _uiState.value = RentViaRexUiState.Error(throwable.message ?: "Failed to rent rex")
    }

    fun onUpdateAmount(amount: String) {
        if (AmountInputValidationUtils.isValidInput(
                amount,
                AmountInputValidationUtils.RESOURCES_PRECISION
            ).not()
        ) return

        _uiState.update {
            val currentState = it as? RentViaRexUiState.Loaded ?: return@update it

            if (currentState.isLoading) return

            val amountCost = currentState.rentViaRexRate
                ?.multiply(amount.toBigDecimalOrNull() ?: BigDecimal.ZERO ) ?: BigDecimal.ZERO

            val balance = currentState.balance?.amount ?: 0.0

            val error = amount.let { _ ->
                if (amountCost > balance) {
                    MR.strings.message_not_enough_eos_to_rent.desc()
                } else {
                    null
                }
            }

            val amountValue = amount.toDoubleOrNull() ?: 0.0

            currentState.copy(
                amount = amount,
                error = error,
                buttonEnabled = error == null && amountValue > 0.0
            )
        }
    }
}