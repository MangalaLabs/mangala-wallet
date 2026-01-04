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
package com.mangala.wallet.chart.horizontalbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import com.mangala.wallet.chart.common.dimens.ChartDimens
import com.mangala.wallet.chart.common.dimens.ChartDimensDefaults
import com.mangala.wallet.chart.horizontalbar.axis.HorizontalAxisConfig
import com.mangala.wallet.chart.horizontalbar.axis.HorizontalAxisConfigDefaults
import com.mangala.wallet.chart.horizontalbar.axis.horizontalYAxis
import com.mangala.wallet.chart.horizontalbar.common.drawHorizontalBarLabel
import com.mangala.wallet.chart.horizontalbar.common.getBottomLeft
import com.mangala.wallet.chart.horizontalbar.common.getTopLeft
import com.mangala.wallet.chart.horizontalbar.config.HorizontalBarConfig
import com.mangala.wallet.chart.horizontalbar.config.HorizontalBarConfigDefaults
import com.mangala.wallet.chart.horizontalbar.config.StartDirection
import com.mangala.wallet.chart.horizontalbar.model.HorizontalBarData
import com.mangala.wallet.chart.horizontalbar.model.maxXValue

@Composable
fun HorizontalBarChart(
    horizontalBarData: List<HorizontalBarData>,
    color: Color,
    onBarClick: (HorizontalBarData) -> Unit,
    modifier: Modifier = Modifier,
    barDimens: ChartDimens = ChartDimensDefaults.horizontalChartDimesDefaults(),
    horizontalAxisConfig: HorizontalAxisConfig = HorizontalAxisConfigDefaults.axisConfigDefaults(),
    horizontalBarConfig: HorizontalBarConfig = HorizontalBarConfigDefaults.horizontalBarConfig()
) {
    HorizontalBarChart(
        horizontalBarData = horizontalBarData,
        colors = listOf(color, color),
        onBarClick = onBarClick,
        modifier = modifier,
        barDimens = barDimens,
        horizontalAxisConfig = horizontalAxisConfig,
        horizontalBarConfig = horizontalBarConfig
    )
}

@Composable
fun HorizontalBarChart(
    horizontalBarData: List<HorizontalBarData>,
    colors: List<Color>,
    onBarClick: (HorizontalBarData) -> Unit,
    modifier: Modifier = Modifier,
    barDimens: ChartDimens = ChartDimensDefaults.horizontalChartDimesDefaults(),
    horizontalAxisConfig: HorizontalAxisConfig = HorizontalAxisConfigDefaults.axisConfigDefaults(),
    horizontalBarConfig: HorizontalBarConfig = HorizontalBarConfigDefaults.horizontalBarConfig()
) {
    val labelTextColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val startAngle = if (horizontalBarConfig.startDirection == StartDirection.Left) 180F else 0F
    val maxXValueState = rememberSaveable { mutableStateOf(horizontalBarData.maxXValue()) }
    val clickedBar = remember { mutableStateOf(Offset(-10F, -10F)) }
    val maxXValue = maxXValueState.value
    val barHeight = remember { mutableStateOf(0F) }

    Canvas(
        modifier = modifier
            .drawBehind {
                if (horizontalAxisConfig.showAxes) {
                    horizontalYAxis(horizontalAxisConfig, maxXValue, startAngle)
                }
            }
            .padding(horizontal = barDimens.padding)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { offset ->
                    clickedBar.value = offset
                })
            }
    ) {
        barHeight.value = size.height.div(horizontalBarData.count().times(1.2F))
        val xScalableFactor = size.width.div(maxXValue)

        when (horizontalBarConfig.startDirection) {
            StartDirection.Right -> {
                horizontalBarData.forEachIndexed { index, data ->
                    val topLeft = getTopLeft(index, barHeight, size, data, xScalableFactor)
                    val bottomLeft = getBottomLeft(index, barHeight, size, data, xScalableFactor)
                    val barWidth = data.xValue.times(xScalableFactor)

                    if (clickedBar.value.y in (topLeft.y..bottomLeft.y)) {
                        onBarClick(data)
                    }
                    drawBars(
                        data,
                        barHeight.value,
                        colors,
                        horizontalBarConfig.showLabels,
                        topLeft,
                        barWidth,
                        labelTextColor
                    )
                }
            }
            else -> {
                horizontalBarData.forEachIndexed { index, data ->
                    val barWidth = data.xValue.times(xScalableFactor)
                    val topLeft = Offset(0F, barHeight.value.times(index).times(1.2F))
                    val bottomLeft = getBottomLeft(index, barHeight, size, data, xScalableFactor)

                    if (clickedBar.value.y in (topLeft.y..bottomLeft.y)) {
                        onBarClick(data)
                    }
                    drawBars(
                        data,
                        barHeight.value,
                        colors,
                        horizontalBarConfig.showLabels,
                        topLeft,
                        barWidth,
                        labelTextColor
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawBars(
    horizontalBarData: HorizontalBarData,
    barHeight: Float,
    colors: List<Color>,
    showLabels: Boolean,
    topLeft: Offset,
    barWidth: Float,
    labelTextColor: Color,
) {
    drawRoundRect(
        topLeft = topLeft,
        brush = Brush.linearGradient(colors),
        size = Size(barWidth, barHeight)
    )
    if (showLabels) {
        drawHorizontalBarLabel(
            horizontalBarData = horizontalBarData,
            barHeight = barHeight,
            topLeft = topLeft,
            labelTextColor = labelTextColor,
        )
    }
}
