package com.mangala.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mangala.wallet.chart.line.LineChart
import com.mangala.wallet.chart.line.config.LineConfig
import com.mangala.wallet.chart.line.model.LineData

@Composable
internal fun TokenChart(
    modifier: Modifier = Modifier,
    sparklineData: List<Double>,
    color: Color = Color.Green,
) {
    val minInSparklineData = sparklineData.minOrNull() ?: 0.0
    val processedSparklineData = sparklineData.map { it - minInSparklineData }
    val lineData = convertSparklineToLineData(processedSparklineData)
    LineChart(
        modifier = Modifier.then(modifier),
        color = color,
        lineConfig = LineConfig(hasSmoothCurve = true),
        lineData = lineData
    )
}

fun convertSparklineToLineData(sparkline: List<Double>): List<LineData> {
    return sparkline.mapIndexed { index, price ->
        LineData(index.toFloat(), price.toFloat())
    }
}