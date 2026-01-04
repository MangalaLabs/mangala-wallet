package com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.stakeforresource

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.rentviarex.RentViaRexUiState
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountWithBalanceInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.DelegateAndUnDelegateBandwidthUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.BaseAntelopeTransactScreenModel
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AmountInputValidationUtils
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StakeForResourceScreenModel(
    private val accountName: String,
    private val isStakeRex: Boolean,
    private val isCpu: Boolean,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountWithBalanceInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val delegateAndUnDelegateBandwidthUseCase: DelegateAndUnDelegateBandwidthUseCase
) : BaseAntelopeTransactScreenModel(
    delegateAndUnDelegateBandwidthUseCase,
    blockchainUid = ""
) {

    private val _uiState = MutableStateFlow<StakeForResourceScreenUiState>(
        StakeForResourceScreenUiState.Success(
            uiModel = StakeForResourceUiModel()
        )
    )
    val uiState: StateFlow<StakeForResourceScreenUiState> get() = _uiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    private var balanceVisible = true
    private var loadAccountJob: Job? = null

    private val listSuggestionInput = listOf(
        StakeForResourceInputUiModel(10),
        StakeForResourceInputUiModel(15),
        StakeForResourceInputUiModel(25),
        StakeForResourceInputUiModel(50),
        StakeForResourceInputUiModel(75),
        StakeForResourceInputUiModel(100)
    )

    init {
        loadData()
    }

    fun pullToRefresh() {
        _isRefreshing.value = true
        loadData(true)
        _isRefreshing.value = false
    }

    fun onContinueTransaction() {
        loadData()
    }

    private fun loadData(forceRefresh: Boolean = false) {
        loadAccountJob?.cancel()
        loadAccountJob = screenModelScope.launch {
            blockchainUid = getSelectedNetworkUseCase().blockChainUid

            _uiState.value = StakeForResourceScreenUiState.Success(
                uiModel = StakeForResourceUiModel()
            )

            initializeState(forceRefresh)
        }
    }

    private suspend fun initializeState(
        forceReload: Boolean = false
    ) {
        getAccountWithBalanceInfoUseCase.invokeFlow(
            accountName,
            blockchainType,
            forceReload
        ).collectLatest { resource ->
            resource.data?.let {
                _uiState.update { currentState ->
                    (currentState as? StakeForResourceScreenUiState.Success)?.let { uiState ->
                        StakeForResourceScreenUiState.Success(
                            uiModel = StakeForResourceUiModel(
                                accountName = accountName,
                                account = it,
                                isBalanceVisible = balanceVisible,
                                balance = if(isStakeRex) {
                                    it.safeCoreBalance
                                } else {
                                    if (isCpu){
                                        it.delegateCpu
                                    } else {
                                        it.delegateNet
                                    }
                                },
                                isStake = isStakeRex,
                                suggestionInputUiModels = listSuggestionInput,
                                nativeToken = it.safeCoreBalance.symbol,
                                isLoading = false,
                                inputSectionEnabled = true
                        )
                        )
                    } ?: currentState
                }
            }
        }
    }

    private fun handleFailure(message: String) {
        _uiState.value = StakeForResourceScreenUiState.Error(message)
    }

    fun onEosValueChange(value: String) {
        val currentState = _uiState.value as? StakeForResourceScreenUiState.Success ?: return
        val uiModel = currentState.uiModel
        if (AmountInputValidationUtils.isValidInput(value, uiModel.balance.precision).not()) return

        if (value.isEmpty()) {
            _uiState.update {
                StakeForResourceScreenUiState.Success(
                    uiModel = uiModel.copy(
                        suggestionInputUiModels = listSuggestionInput,
                        eosAmount = ""
                    )
                )
            }
        } else {
            try {
                val valueBigDecimal = value.toBigDecimal()
                _uiState.update {
                    StakeForResourceScreenUiState.Success(
                        uiModel = uiModel.copy(
                            isInsufficientInputAmount = valueBigDecimal > uiModel.eosBalance.toBigDecimal(),
                            eosAmount = value
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

    fun onSelectSuggestionInput(suggestionInputUiModel: StakeForResourceInputUiModel) {
        val currentState = _uiState.value as? StakeForResourceScreenUiState.Success ?: return
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

        val eosBalance = uiModel.eosBalance.toBigDecimal()
        val percent = (suggestionInputUiModel.amount / 100.0).toBigDecimal()
        val eosAmount = (eosBalance * percent).roundToDigitPositionAfterDecimalPoint(
            currentState.uiModel.balance.precision.toLong(),
            RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
        )
        onEosValueChange(eosAmount.toPlainString())
    }

    override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
        val currentState = _uiState.value as? StakeForResourceScreenUiState.Success ?: return
        val uiModel = currentState.uiModel

        _uiState.value = currentState.copy(
            uiModel.copy(
                resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
                resourceRequiredTotal = resourceProviderResponse.fee,
                isLoading = false
            )
        )
    }

    override fun onRequestTransactionInvalidRequest() {
        _uiState.value = StakeForResourceScreenUiState.Error("Invalid request")
    }

    override fun onRequestTransactionResourceCovered() {
        val currentState = _uiState.value as? StakeForResourceScreenUiState.Success ?: return
        val uiModel = currentState.uiModel

        _uiState.update {
            currentState.copy(uiModel.copy(promptConfirmTransaction = true, isLoading = false))
        }
    }

    override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
        return if (isStakeRex) {
            requestDelegateBandwidth()
        } else {
            requestUndelegateBandwidth()
        }
    }

    override fun onDismissTransactionFeeBreakdown() {
        val currentState = (_uiState.value as? StakeForResourceScreenUiState.Success) ?: return
        val uiModel = currentState.uiModel

        _uiState.update {
            currentState.copy(
                uiModel.copy(
                    resourceRequiredBreakdown = null,
                    resourceRequiredTotal = null,
                    isLoading = false
                )
            )
        }
    }

    override fun onPinPromptShown() {
        val currentState = _uiState.value as? StakeForResourceScreenUiState.Success ?: return
        val uiModel = currentState.uiModel

        _uiState.update {
            currentState.copy(uiModel.copy(promptConfirmTransaction = false, resourceRequiredBreakdown = null, resourceRequiredTotal = null))
        }
    }

    override fun onConfirmResourceProviderFee() {
        val currentState = _uiState.value as? StakeForResourceScreenUiState.Success ?: return
        val uiModel = currentState.uiModel

        _uiState.update {
            currentState.copy(uiModel = uiModel.copy(promptConfirmTransaction = true, isLoading = true))
        }
    }

    override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
        return if (isStakeRex) {
            pushDelegateBandwidth()
        } else {
            pushUndelegateBandwidth()
        }
    }

    override fun showLoadingState() {
        val uiState = (_uiState.value as? StakeForResourceScreenUiState.Success) ?: return
        val uiModel = uiState.uiModel

        _uiState.value = uiState.copy(uiModel = uiModel.copy(isLoading = true))
    }

    override fun onPushTransactionSuccess(txHash: String) {
        _uiState.value = StakeForResourceScreenUiState.ExecuteStakeForResourceSuccess(txHash)
    }

    override fun onPushTransactionFail(throwable: Throwable) {
        _uiState.value = StakeForResourceScreenUiState.Error(throwable.message.orEmpty())
    }

    private suspend fun requestDelegateBandwidth(): Result<ResourceProviderResponse> {
        val uiModel = (_uiState.value as? StakeForResourceScreenUiState.Success)?.uiModel ?: return Result.failure(Exception("Invalid UI state"))

        return if (isCpu) {
            delegateAndUnDelegateBandwidthUseCase.requestDelegateBandwidthCpu(
                blockchainType = blockchainType,
                accountName = accountName,
                stakeCpuQuantity = uiModel.eosAmount.toDouble(),
                uiModel.balance.symbol,
                uiModel.balance.precision
            )
        } else {
            delegateAndUnDelegateBandwidthUseCase.requestDelegateBandwidthNet(
                blockchainType = blockchainType,
                accountName = accountName,
                stakeNetQuantity = uiModel.eosAmount.toDouble(),
                uiModel.balance.symbol,
                uiModel.balance.precision
            )
        }
    }

    private suspend fun requestUndelegateBandwidth(): Result<ResourceProviderResponse> {
        val uiModel = (_uiState.value as? StakeForResourceScreenUiState.Success)?.uiModel ?: return Result.failure(Exception("Invalid UI state"))

        return if (isCpu) {
            delegateAndUnDelegateBandwidthUseCase.requestUnDelegateBandwidthCpu(
                blockchainType = blockchainType,
                accountName = accountName,
                stakeCpuQuantity = uiModel.eosAmount.toDouble(),
                uiModel.balance.symbol,
                uiModel.balance.precision
            )
        } else {
            delegateAndUnDelegateBandwidthUseCase.requestUnDelegateBandwidthNet(
                blockchainType = blockchainType,
                accountName = accountName,
                stakeNetQuantity = uiModel.eosAmount.toDouble(),
                uiModel.balance.symbol,
                uiModel.balance.precision
            )
        }
    }

    private suspend fun pushDelegateBandwidth(): Result<String> {
        val uiModel = (_uiState.value as? StakeForResourceScreenUiState.Success)?.uiModel ?: return Result.failure(Exception("Invalid UI state"))

        return if (isCpu) {
            delegateAndUnDelegateBandwidthUseCase.pushDelegateBandwidthCpu(
                blockchainType = blockchainType,
                accountName = accountName,
                stakeCpuQuantity = uiModel.eosAmount.toDouble(),
                uiModel.balance.symbol,
                uiModel.balance.precision
            )
        } else {
            delegateAndUnDelegateBandwidthUseCase.pushDelegateBandwidthNet(
                blockchainType = blockchainType,
                accountName = accountName,
                stakeNetQuantity = uiModel.eosAmount.toDouble(),
                uiModel.balance.symbol,
                uiModel.balance.precision
            )
        }
    }

    private suspend fun pushUndelegateBandwidth(): Result<String> {
        val uiModel = (_uiState.value as? StakeForResourceScreenUiState.Success)?.uiModel ?: return Result.failure(Exception("Invalid UI state"))

        return if (isCpu) {
            delegateAndUnDelegateBandwidthUseCase.pushUnDelegateBandwidthCpu(
                blockchainType = blockchainType,
                accountName = accountName,
                stakeCpuQuantity = uiModel.eosAmount.toDouble(),
                uiModel.balance.symbol,
                uiModel.balance.precision
            )
        } else {
            delegateAndUnDelegateBandwidthUseCase.pushUnDelegateBandwidthNet(
                blockchainType = blockchainType,
                accountName = accountName,
                stakeNetQuantity = uiModel.eosAmount.toDouble(),
                uiModel.balance.symbol,
                uiModel.balance.precision
            )
        }
    }
}
