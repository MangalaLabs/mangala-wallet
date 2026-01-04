/*
 * Copyright 2022 Himanshu Singh
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file has been modified from the original Charty library.
 */
package com.mangala.wallet.chart.line

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import com.mangala.wallet.chart.common.axis.AxisConfig
import com.mangala.wallet.chart.common.axis.AxisConfigDefaults
import com.mangala.wallet.chart.common.axis.drawXLabel
import com.mangala.wallet.chart.common.axis.drawYAxisWithLabels
import com.mangala.wallet.chart.common.calculations.dataToOffSet
import com.mangala.wallet.chart.common.calculations.dataToOffSetLineChart
import com.mangala.wallet.chart.common.dimens.ChartDimens
import com.mangala.wallet.chart.common.dimens.ChartDimensDefaults
import com.mangala.wallet.chart.line.config.LineConfig
import com.mangala.wallet.chart.line.config.LineConfigDefaults
import com.mangala.wallet.chart.line.model.LineData
import com.mangala.wallet.chart.line.model.maxYValue
import com.mangala.wallet.chart.line.model.minYValue

@Composable
fun LineChart(
    lineData: List<LineData>,
    color: Color,
    modifier: Modifier = Modifier,
    chartDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    lineConfig: LineConfig = LineConfigDefaults.lineConfigDefaults()
) {
    LineChart(
        lineData = lineData,
        colors = listOf(color, color),
        modifier = modifier,
        chartDimens = chartDimens,
        axisConfig = axisConfig,
        lineConfig = lineConfig
    )
}

@Composable
fun LineChart(
    lineData: List<LineData>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    chartDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    lineConfig: LineConfig = LineConfigDefaults.lineConfigDefaults()
) {
    val maxYValueState = remember(lineData) { derivedStateOf { lineData.maxYValue() } }
    val minYValueState = remember(lineData) { derivedStateOf { lineData.minYValue() } }
    val maxYValue = maxYValueState.value
    val minYValue = minYValueState.value
    val lineBound = remember(lineData) { mutableStateOf(0F) }

    Canvas(
        modifier = modifier
//            .drawBehind {
//                if (axisConfig.showAxis) {
//                    drawYAxisWithLabels(axisConfig, maxYValue, textColor = axisConfig.textColor)
//                }
//            }
//            .padding(horizontal = chartDimens.padding)

    ) {
        lineBound.value = size.width.div(lineData.count().times(1.2F))
        val scaleFactor = size.height.div(maxYValue)
        val distance = maxYValue/minYValue
        val brush = Brush.linearGradient(colors)
        val radius = size.width.div(70)
        val strokeWidth = lineConfig.strokeSize.toPx()
        val path = Path().apply {
            moveTo(0f, size.height)
        }

        lineData.forEachIndexed { index, data ->
            val centerOffset = dataToOffSetLineChart(index, lineBound.value, size, distance, data, scaleFactor)
            if (lineData.size > 1) {
                when (index) {
                    0 -> path.moveTo(centerOffset.x, centerOffset.y)
                    else -> path.lineTo(centerOffset.x, centerOffset.y)
                }
            }
            if (lineConfig.hasDotMarker) {
                drawCircle(
                    center = centerOffset,
                    radius = radius,
                    brush = brush
                )
            }
            if (axisConfig.showXLabels) {
                drawXLabel(
                    data.xValue,
                    centerOffset,
                    radius,
                    lineData.count(),
                    axisConfig.textColor
                )
            }
        }
        if (lineData.size > 1) {
            val pathEffect =
                if (lineConfig.hasSmoothCurve) PathEffect.cornerPathEffect(strokeWidth) else null
            drawPath(
                path = path,
                brush = brush,
                style = Stroke(width = strokeWidth, pathEffect = pathEffect),
            )
        }
    }
}
