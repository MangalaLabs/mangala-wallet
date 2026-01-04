package com.mangala.wallet.features.chains.antelope.ram.presentation.details

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamChartUseCase
import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.domain.datastore.usecases.GetBalanceVisibleStatusUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.datastore.usecases.SaveBalanceVisibleStatusUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountWithBalanceInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamPriceUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RamDetailScreenModel(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getRamPriceUseCase: GetRamPriceUseCase,
    private val getAccountWithBalanceInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val getActionsUseCase: GetActionsUseCase,
    private val getBalanceVisibleStatusUseCase: GetBalanceVisibleStatusUseCase,
    private val saveBalanceVisibleStatusUseCase: SaveBalanceVisibleStatusUseCase,
    private val getRamChartUseCase: GetRamChartUseCase,
    val accountName: String,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(
        RamDetailUiModel(
            showBalance = null,
            account = Resource.Loading(null),
            ramMarketData = Resource.Loading(null),
            buyRam = Resource.Loading(emptyList()),
            sellRamTransferActions = Resource.Loading(emptyList()),
            logRam = Resource.Loading(emptyList()),
            ramFee = Resource.Loading(emptyList()),
            ramChartData = Resource.Loading(null),
            last24hLogRamChangeAction = Resource.Loading(null),
            blockchainType = null
        )
    )
    val uiState: StateFlow<RamDetailUiModel> get() = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    private var balanceVisible = true
    private var loadDataJob: Job? = null

    init {
        loadActionByAccount(accountName)
        screenModelScope.launch {
            try {
                collectBalanceVisibleStatus()
            } catch (e: Exception) {
                _uiState.update { it.copy(generalErrorMessage = e.message) }
            }
        }
    }

    fun pullToRefresh() {
        _isLoading.value = true
        try {
            loadActionByAccount(accountName, forceRefresh = true)
        } catch (e: Exception) {
            _uiState.update { it.copy(generalErrorMessage = e.message) }
        } finally {
            _isLoading.value = false
        }
    }

    private fun loadActionByAccount(
        accountName: String,
        forceRefresh: Boolean = false
    ) {
        loadDataJob?.cancel()
        loadDataJob = screenModelScope.launch {
            try {
                val blockchainType = getSelectedNetworkUseCase().blockchainType

                combine(
                    getRamPriceUseCase.invokeFlow(blockchainType, forceRefresh),
                    getAccountWithBalanceInfoUseCase.invokeFlow(accountName, blockchainType, forceRefresh),
                    getActionsUseCase.getBuyRamTransferActions(accountName, blockchainType, forceRefresh),
                    getActionsUseCase.getSellRamTransferActions(accountName, blockchainType, forceRefresh),
                    getActionsUseCase.getRamFeeActions(accountName, blockchainType, forceRefresh),
                    getActionsUseCase.getLogRamActions(accountName, blockchainType, forceRefresh),
                    getRamChartUseCase.getOhlcFlow(blockchainType, SamplingInterval.FIVE_MINUTES),
                    getActionsUseCase.getLast24hLogRamChangeAction(accountName, blockchainType, forceRefresh)
                ) { ramPrice, account, buyRam, sellRam, ramFee, logRam, ramChart, last24hLogRamChange ->
                    RamDetailUiModel(
                        showBalance = _uiState.value.showBalance,
                        account = account,
                        ramMarketData = ramPrice,
                        buyRam = buyRam,
                        sellRamTransferActions = sellRam,
                        logRam = logRam,
                        ramFee = ramFee,
                        ramChartData = ramChart,
                        last24hLogRamChangeAction = last24hLogRamChange,
                        blockchainType = blockchainType
                    )
                }.collectLatest { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(generalErrorMessage = e.message) }
            }
        }
    }

    fun saveRamVisibleStatus(showBalance: Boolean) {
        screenModelScope.launch {
            saveBalanceVisibleStatusUseCase(showBalance)
        }
    }

    fun onRamUnitChange(ramUnit: RamDetailUiModel.RamUnit) {
        _uiState.update {
            it.copy(ramUnitSelected = ramUnit)
        }
    }

    private suspend fun collectBalanceVisibleStatus() {
        getBalanceVisibleStatusUseCase().collect { showBalance ->
            _uiState.update {
                it.copy(showBalance = showBalance)
            }
            balanceVisible = showBalance
        }
    }

    fun isDevelopmentEnvironment(): Boolean {
        return buildEnvironmentProvider.isDevelopmentEnvironment()
    }
}

inline fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Flow<R> {
    return combine(
        flow,
        flow2,
        flow3,
        flow4,
        flow5,
        flow6,
        flow7,
        flow8
    ) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7,
            args[7] as T8,
        )
    }
}