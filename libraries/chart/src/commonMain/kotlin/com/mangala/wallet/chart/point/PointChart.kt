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
package com.mangala.wallet.chart.point

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.mangala.wallet.chart.common.axis.AxisConfig
import com.mangala.wallet.chart.common.axis.AxisConfigDefaults
import com.mangala.wallet.chart.common.axis.drawXLabel
import com.mangala.wallet.chart.common.axis.drawYAxisWithLabels
import com.mangala.wallet.chart.common.calculations.dataToOffSet
import com.mangala.wallet.chart.common.dimens.ChartDimens
import com.mangala.wallet.chart.common.dimens.ChartDimensDefaults
import com.mangala.wallet.chart.point.cofig.PointConfig
import com.mangala.wallet.chart.point.cofig.PointConfigDefaults
import com.mangala.wallet.chart.point.cofig.PointType
import com.mangala.wallet.chart.point.model.PointData
import com.mangala.wallet.chart.point.model.maxYValue

@Composable
fun PointChart(
    pointData: List<PointData>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    chartDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    pointConfig: PointConfig = PointConfigDefaults.pointConfigDefaults()
) {
    val maxYValueState = rememberSaveable { mutableStateOf(pointData.maxYValue()) }
    val maxYValue = maxYValueState.value
    val pointBound = remember { mutableStateOf(0F) }

    Canvas(
        modifier = modifier
            .drawBehind {
                if (axisConfig.showAxis) {
                    drawYAxisWithLabels(axisConfig, maxYValue, textColor = axisConfig.textColor)
                }
            }
            .padding(horizontal = chartDimens.padding)

    ) {
        pointBound.value = size.width.div(pointData.count().times(1.2F))
        val yScaleFactor = size.height.div(maxYValue)
        val brush = Brush.linearGradient(colors)
        val radius = size.width.div(70)

        pointData.forEachIndexed { index, data ->
            val centerOffset =
                dataToOffSet(index, pointBound.value, size, data.yValue, yScaleFactor)
            val style = when (pointConfig.pointType) {
                is PointType.Stroke -> Stroke(width = size.width.div(100))
                else -> Fill
            }

            drawCircle(
                center = centerOffset, style = style, radius = radius, brush = brush
            )

            if (axisConfig.showXLabels) {
                drawXLabel(
                    data = data.xValue,
                    centerOffset = centerOffset,
                    radius = radius,
                    count = pointData.count(),
                    textColor = axisConfig.textColor
                )
            }
        }
    }
}

@Composable
fun PointChart(
    pointData: List<PointData>,
    color: Color,
    modifier: Modifier = Modifier,
    chartDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    pointConfig: PointConfig = PointConfigDefaults.pointConfigDefaults()
) {
    PointChart(
        pointData = pointData,
        colors = listOf(color, color),
        modifier = modifier,
        chartDimens = chartDimens,
        axisConfig = axisConfig,
        pointConfig = pointConfig
    )
}
