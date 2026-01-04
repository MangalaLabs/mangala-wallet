package com.mangala.wallet.features.chains.antelope.ram.presentation.details.bottomSheet

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamChartUseCase
import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.chart.market.Candle
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChartRamScreenModel(
    private val getRamChartUseCase: GetRamChartUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) : BaseScreenModel() {
    private val _uiState =
        MutableStateFlow<ChartRamUiState>(ChartRamUiState.Loading)
    val uiState: StateFlow<ChartRamUiState> get() = _uiState

    init {
        loadData(SamplingInterval.ONE_DAY)
    }

    fun loadData(samplingInterval: SamplingInterval) {
        screenModelScope.launch {
            val blockchainType = getSelectedNetworkUseCase().blockchainType
            _uiState.value = ChartRamUiState.Success(
                chartRamUiModel = ChartRamUiModel(null, samplingInterval)
            )
            val sparklineData =
                getRamChartUseCase.getOhlc(blockchainType, samplingInterval).getOrNull()
            val candles = sparklineData?.dataPoints?.map {
                Candle(
                    time = it.date.toEpochMilliseconds(),
                    open = it.open.toFloat(),
                    high = it.high.toFloat(),
                    low = it.low.toFloat(),
                    close = it.close.toFloat()
                )
            }
            if (sparklineData != null) {
                _uiState.value = ChartRamUiState.Success(
                    chartRamUiModel = ChartRamUiModel(candles, samplingInterval)
                )
            } else {
                _uiState.value = ChartRamUiState.Error(WrappedStringResource.StringRes(MR.strings.message_chart_ram_screen_model_failed_to_load_data))
            }
        }
    }
}