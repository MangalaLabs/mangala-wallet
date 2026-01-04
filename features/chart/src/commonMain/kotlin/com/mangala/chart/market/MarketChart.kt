package com.mangala.chart.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.mangala.wallet.chart.market.Candle
import com.mangala.wallet.chart.market.MarketChart
import com.mangala.wallet.chart.market.MarketChartColors
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import kotlinx.datetime.Instant

@Composable
fun MangalaMarketChart(
    modifier: Modifier = Modifier,
    candles: List<Candle>,
    marketChartColors: MarketChartColors = MarketChartColors.defaults(),
    textSize: TextUnit = 12.sp,
    dateTransform: (Instant) -> String,
    priceTransform: (Float) -> String
) {
    Box(Modifier.then(modifier).mangalaWalletPlaceholder(candles.isEmpty(), modifier = modifier)) {
        if (candles.isEmpty()) {
            Box(
                modifier = Modifier.then(modifier)
            )
        } else {
            MarketChart(
                modifier = modifier,
                candles = candles,
                marketChartColors = marketChartColors,
                textSize = textSize,
                dateTransform = dateTransform,
                priceTransform = priceTransform
            )
        }
    }
}