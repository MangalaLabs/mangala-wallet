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
package com.mangala.wallet.chart.bar

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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import com.mangala.wallet.chart.bar.common.calculations.getStackedTopLeft
import com.mangala.wallet.chart.bar.common.calculations.getTopLeft
import com.mangala.wallet.chart.bar.common.calculations.getTopRight
import com.mangala.wallet.chart.bar.common.component.drawBarLabel
import com.mangala.wallet.chart.bar.config.BarConfig
import com.mangala.wallet.chart.bar.config.BarConfigDefaults
import com.mangala.wallet.chart.bar.model.StackedBarData
import com.mangala.wallet.chart.bar.model.isValid
import com.mangala.wallet.chart.bar.model.maxYValue
import com.mangala.wallet.chart.common.axis.AxisConfig
import com.mangala.wallet.chart.common.axis.AxisConfigDefaults
import com.mangala.wallet.chart.common.axis.drawYAxisWithLabels
import com.mangala.wallet.chart.common.dimens.ChartDimens
import com.mangala.wallet.chart.common.dimens.ChartDimensDefaults

@Composable
fun StackedBarChart(
    stackBarData: List<StackedBarData>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    onBarClick: (StackedBarData) -> Unit = {},
    chartDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    barConfig: BarConfig = BarConfigDefaults.barConfigDimesDefaults()
) {
    if (stackBarData.isValid(colors.count()).not()) {
        throw IllegalArgumentException("Colors count should be total to number of values in StackedBarData's yValue")
    }

    val maxYValueState = rememberSaveable { mutableStateOf(stackBarData.maxYValue()) }
    val maxYValue = maxYValueState.value
    val barWidth = remember { mutableStateOf(0F) }
    val clickedBar = remember { mutableStateOf(Offset(-10F, -10F)) }

    Canvas(
        modifier = modifier
            .drawBehind {
                if (axisConfig.showAxis) {
                    drawYAxisWithLabels(axisConfig, maxYValue, textColor = axisConfig.textColor)
                }
            }
            .padding(horizontal = chartDimens.padding)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { offset ->
                    clickedBar.value = offset
                })
            }
    ) {
        barWidth.value = size.width.div(stackBarData.count().times(1.2F))
        val yScalableFactor = size.height.div(maxYValue)

        stackBarData.reversed().forEachIndexed { index, stackBarDataIndividual ->
            drawIndividualStackedBar(
                index, stackBarDataIndividual, barWidth.value, yScalableFactor, barConfig, colors
            )
        }
        drawLabels(
            stackBarData,
            yScalableFactor,
            barWidth.value,
            axisConfig,
            clickedBar.value,
            onBarClick,
            axisConfig.textColor
        )
    }
}

private fun DrawScope.drawLabels(
    stackBarData: List<StackedBarData>,
    yScalableFactor: Float,
    width: Float,
    axisConfig: AxisConfig,
    clickedBarValue: Offset,
    onBarClick: (StackedBarData) -> Unit,
    labelTextColor: Color,
) {
    stackBarData.forEachIndexed { index, stackBarDataIndividual ->
        val barHeight = stackBarDataIndividual.yValue.sum().times(yScalableFactor)
        val barTopLeft = getTopLeft(
            index, width, size, stackBarDataIndividual.yValue.sum(), yScalableFactor
        )
        val barTopRight = getTopRight(
            index, width, size, stackBarDataIndividual.yValue.sum(), yScalableFactor
        )
        if (clickedBarValue.x in (barTopLeft.x..barTopRight.x)) {
            onBarClick(stackBarDataIndividual)
        }
        if (axisConfig.showXLabels) {
            drawBarLabel(
                stackBarDataIndividual.xValue,
                width.times(1.4F),
                barHeight,
                barTopLeft,
                stackBarData.count(),
                labelTextColor
            )
        }
    }
}

private fun DrawScope.drawIndividualStackedBar(
    index: Int,
    stackBarData: StackedBarData,
    barWidth: Float,
    yScalableFactor: Float,
    barConfig: BarConfig,
    colors: List<Color>,
) {

    var individualHeight = 0F
    stackBarData.yValue.forEachIndexed { individualIndex, value ->
        val individualBarHeight = value.times(yScalableFactor)
        val topLeft = getStackedTopLeft(
            index,
            barWidth,
            individualHeight,
        )
        rotate(180F) {
            drawRoundRect(
                cornerRadius = CornerRadius(if (barConfig.hasRoundedCorner) individualBarHeight else 0F),
                topLeft = topLeft,
                color = colors[individualIndex],
                size = Size(barWidth, individualBarHeight)
            )
        }

        individualHeight += (individualBarHeight)
    }
}
