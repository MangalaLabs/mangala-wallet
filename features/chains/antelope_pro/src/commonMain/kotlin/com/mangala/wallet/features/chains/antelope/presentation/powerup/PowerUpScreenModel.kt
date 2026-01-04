package com.mangala.wallet.features.chains.antelope.presentation.powerup

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.rentviarex.RentViaRexUiState
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountWithBalanceInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup.GetPowerUpRateUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup.PowerUpUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.BaseAntelopeTransactScreenModel
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AmountInputValidationUtils
import com.mangala.wallet.utils.max
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PowerUpScreenModel(
    private val accountName: String,
    private val isCpu: Boolean,
    private val getAccountWithBalanceInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getPowerUpRateUseCase: GetPowerUpRateUseCase,
    private val powerUpUseCase: PowerUpUseCase
) : BaseAntelopeTransactScreenModel(
    transactUseCase = powerUpUseCase,
    blockchainUid = ""
) {

    private val _uiState: MutableStateFlow<PowerUpScreenUiState> =
        MutableStateFlow(
            PowerUpScreenUiState.Loaded(
                amountUnit = if (isCpu) "ms" else "kb"
            )
        )

    val uiState: StateFlow<PowerUpScreenUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    private var loadAccountJob: Job? = null

    init {
        loadData()
    }

    fun continuePowerUp() {
        loadData()
    }

    fun pullToRefresh() {
        _isRefreshing.value = true
        loadData(true)
        _isRefreshing.value = false
    }


    private fun loadData(forceReload: Boolean = false) {
        loadAccountJob?.cancel()
        loadAccountJob = screenModelScope.launch {
            blockchainUid = getSelectedNetworkUseCase().blockChainUid

            _uiState.value = PowerUpScreenUiState.Loaded(
                amountUnit = if (isCpu) "ms" else "kb"
            )

            collectPowerUpRate()
            collectAccountInfo(forceReload = forceReload)
        }
    }

    private suspend fun collectPowerUpRate() {
        val powerUpRate =
            if (isCpu) {
                getPowerUpRateUseCase.getCpuPricePerMs(blockchainType, forceRefresh = false)
            } else {
                getPowerUpRateUseCase.getNetPricePerKb(blockchainType, forceRefresh = false)
            }

        powerUpRate.getOrNull()?.let {
            _uiState.value = PowerUpScreenUiState.Loaded(
                powerUpRate = it,
                amountUnit = if (isCpu) "ms" else "kb"
            )
        } ?: run {
            _uiState.value = PowerUpScreenUiState.Loaded(
                amountUnit = if (isCpu) "ms" else "kb"
            )
        }
    }

    private suspend fun collectAccountInfo(forceReload: Boolean) {
        getAccountWithBalanceInfoUseCase.invokeFlow(
            accountName,
            blockchainType,
            forceRefresh = forceReload
        )
            .collectLatest { accountInfo ->
                accountInfo.data?.let { account ->
                    val resourceLimit =
                        if (isCpu) account.cpuLimit else account.netLimit
                    val resourceUsagePercentage = resourceLimit?.getUsedPercentage()

                    _uiState.update { currentState ->
                        (currentState as? PowerUpScreenUiState.Loaded)?.let { uiState ->
                            PowerUpScreenUiState.Loaded(
                                balance = account.coreLiquidBalance?.let { it1 ->
                                    BalanceFormatter.deserialize(
                                        it1
                                    )
                                },
                                resourceUsedPercentage = resourceUsagePercentage,
                                powerUpRate = uiState.powerUpRate,
                                amountUnit = if (isCpu) "ms" else "kb",
                                inputSectionEnabled = true,
                            )
                        } ?: currentState
                    }
                }
            }
    }

    fun onUpdateAmount(amount: String) {
        if (AmountInputValidationUtils.isValidInput(
                amount,
                AmountInputValidationUtils.RESOURCES_PRECISION
            ).not()
        ) return

        _uiState.update {
            val currentState = it as? PowerUpScreenUiState.Loaded ?: return@update it

            if (currentState.isLoading) return

            val powerUpInfo = currentState.powerUpRate

            val powerUpCost =
                amount.toDoubleOrNull()?.let {
                    max(
                        getPowerUpCostInNativeCoin(it, powerUpInfo),
                        BigDecimal.ONE.times(10).pow(-powerUpInfo.minPowerUpFee.precision)
                    )
                }

            val balance = currentState.balance?.amount?.toBigDecimal() ?: BigDecimal.ZERO

            val error = powerUpCost?.let { cost ->
                if (cost > balance) {
                    "Insufficient balance"
                } else {
                    null
                }
            }

            val amountValue = amount.toDoubleOrNull() ?: 0.0

            currentState.copy(
                amount = amount,
                powerUpCost = powerUpCost,
                error = error,
                resourceUsedPercentage = currentState.resourceUsedPercentage,
                buttonEnabled = error == null && amountValue > 0.0
            )
        }
    }

    private fun getPowerUpCostInNativeCoin(
        amount: Double,
        powerUpRate: GetPowerUpRateUseCase.PowerUpRate
    ): BigDecimal {
        return amount.toBigDecimal().times(powerUpRate.rate).roundToDigitPositionAfterDecimalPoint(
            powerUpRate.sampleUsage.nativeCoinPrecision.toLong(),
            RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
        )
    }

    override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
        val currentState = (_uiState.value as? PowerUpScreenUiState.Loaded) ?: return

        _uiState.update {
            currentState.copy(
                resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
                resourceRequiredTotal = resourceProviderResponse.fee,
                isLoading = false,
                buttonEnabled = false
            )
        }
    }

    override fun onRequestTransactionInvalidRequest() {
        val uiState = (_uiState.value as? PowerUpScreenUiState.Loaded) ?: return

        _uiState.value = uiState.copy(isLoading = false, error = "Invalid request", buttonEnabled = true)
    }

    override fun onRequestTransactionResourceCovered() {
        val currentState = _uiState.value as? PowerUpScreenUiState.Loaded ?: return

        _uiState.update {
            currentState.copy(promptConfirmTransaction = true, isLoading = false, buttonEnabled = false)
        }
    }

    override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
        val currentState = _uiState.value as? PowerUpScreenUiState.Loaded ?: return Result.failure(Exception("Invalid state"))

        return if (isCpu) {
            powerUpUseCase.requestPowerUpCpu(
                blockchainType,
                currentState.powerUpRate,
                currentState.amount,
                accountName,
                accountName
            )
        } else {
            powerUpUseCase.requestPowerUpNet(
                blockchainType,
                currentState.powerUpRate,
                currentState.amount,
                accountName,
                accountName
            )
        }
    }

    override fun onDismissTransactionFeeBreakdown() {
        val currentState = (_uiState.value as? PowerUpScreenUiState.Loaded) ?: return

        _uiState.update {
            currentState.copy(
                resourceRequiredBreakdown = null,
                resourceRequiredTotal = null,
                isLoading = false,
                buttonEnabled = true
            )
        }
    }

    override fun onPinPromptShown() {
        val currentState = _uiState.value as? PowerUpScreenUiState.Loaded ?: return

        _uiState.update {
            currentState.copy(
                promptConfirmTransaction = false,
                resourceRequiredBreakdown = null,
                resourceRequiredTotal = null,
                buttonEnabled = true
            )
        }
    }

    override fun onConfirmResourceProviderFee() {
        val currentState = _uiState.value as? PowerUpScreenUiState.Loaded ?: return

        _uiState.update {
            currentState.copy(promptConfirmTransaction = true, isLoading = true)
        }
    }

    override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
        val currentState = _uiState.value as? PowerUpScreenUiState.Loaded ?: return Result.failure(Exception("Invalid state"))

        return if (isCpu) {
            powerUpUseCase.pushPowerUpCpu(
                blockchainType,
                currentState.powerUpRate,
                currentState.amount,
                accountName,
                accountName
            )
        } else {
            powerUpUseCase.pushPowerUpNet(
                blockchainType,
                currentState.powerUpRate,
                currentState.amount,
                accountName,
                accountName
            )
        }
    }

    override fun showLoadingState() {
        val currentState = _uiState.value as? PowerUpScreenUiState.Loaded ?: return

        _uiState.update {
            currentState.copy(isLoading = true, buttonEnabled = false)
        }
    }

    override fun onPushTransactionSuccess(txHash: String) {
        _uiState.update {
            PowerUpScreenUiState.ExecutePowerUpSuccess(txHash)
        }
    }

    override fun onPushTransactionFail(throwable: Throwable) {
        val currentState = _uiState.value as? PowerUpScreenUiState.Loaded ?: return

        _uiState.update {
            currentState.copy(error = throwable.message, isLoading = false, buttonEnabled = true)
        }
    }
}