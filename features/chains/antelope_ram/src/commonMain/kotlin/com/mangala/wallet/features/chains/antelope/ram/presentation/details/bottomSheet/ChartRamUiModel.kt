package com.mangala.wallet.features.chains.antelope.ram.presentation.details.bottomSheet

import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.chart.market.Candle

class ChartRamUiModel(
    val ohlc: List<Candle>?,
    val selectedInterval: SamplingInterval
) {
    val chartTimeLabelMode = when (selectedInterval) {
        SamplingInterval.FIVE_MINUTES, SamplingInterval.FIFTEEN_MINUTES, SamplingInterval.THIRTY_MINUTES -> ChartTimeLabelMode.TIME_ONLY
        SamplingInterval.ONE_HOUR, SamplingInterval.FOUR_HOURS -> ChartTimeLabelMode.DATE_TIME
        SamplingInterval.ONE_DAY, SamplingInterval.ONE_WEEK, SamplingInterval.ONE_MONTH -> ChartTimeLabelMode.DATE_ONLY
    }
}

enum class ChartTimeLabelMode {
    TIME_ONLY,
    DATE_TIME,
    DATE_ONLY
}
