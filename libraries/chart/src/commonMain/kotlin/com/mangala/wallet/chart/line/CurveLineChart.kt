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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import com.mangala.wallet.chart.common.axis.AxisConfig
import com.mangala.wallet.chart.common.axis.AxisConfigDefaults
import com.mangala.wallet.chart.common.axis.drawYAxisWithLabels
import com.mangala.wallet.chart.common.calculations.dataToOffSet
import com.mangala.wallet.chart.common.dimens.ChartDimens
import com.mangala.wallet.chart.common.dimens.ChartDimensDefaults
import com.mangala.wallet.chart.line.config.CurveLineConfig
import com.mangala.wallet.chart.line.config.CurveLineConfigDefaults
import com.mangala.wallet.chart.line.model.LineData
import com.mangala.wallet.chart.line.model.maxYValue
import com.mangala.wallet.chart.utils.PointF

@Composable
fun CurveLineChart(
    lineData: List<LineData>,
    chartColor: Color,
    lineColor: Color,
    modifier: Modifier = Modifier,
    chartDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    curveLineConfig: CurveLineConfig = CurveLineConfigDefaults.curveLineConfigDefaults()
) {
    CurveLineChart(
        modifier = modifier,
        lineData = lineData,
        chartColors = listOf(chartColor, chartColor),
        lineColors = listOf(lineColor, lineColor),
        chartDimens = chartDimens,
        axisConfig = axisConfig,
        curveLineConfig = curveLineConfig
    )
}

@Composable
fun CurveLineChart(
    lineData: List<LineData>,
    chartColor: Color,
    lineColor: List<Color>,
    modifier: Modifier = Modifier,
    chartDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    curveLineConfig: CurveLineConfig = CurveLineConfigDefaults.curveLineConfigDefaults()
) {
    CurveLineChart(
        modifier = modifier,
        lineData = lineData,
        chartColors = listOf(chartColor, chartColor),
        lineColors = lineColor,
        chartDimens = chartDimens,
        axisConfig = axisConfig,
        curveLineConfig = curveLineConfig
    )
}

@Composable
fun CurveLineChart(
    lineData: List<LineData>,
    chartColors: List<Color>,
    lineColor: Color,
    modifier: Modifier = Modifier,
    chartDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    curveLineConfig: CurveLineConfig = CurveLineConfigDefaults.curveLineConfigDefaults()
) {
    CurveLineChart(
        modifier = modifier,
        lineData = lineData,
        chartColors = chartColors,
        lineColors = listOf(lineColor, lineColor),
        chartDimens = chartDimens,
        axisConfig = axisConfig,
        curveLineConfig = curveLineConfig
    )
}

@Composable
fun CurveLineChart(
    lineData: List<LineData>,
    chartColors: List<Color>,
    lineColors: List<Color>,
    modifier: Modifier = Modifier,
    chartDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    curveLineConfig: CurveLineConfig = CurveLineConfigDefaults.curveLineConfigDefaults()
) {
    val graphPathPoints = mutableListOf<PointF>()
    val backgroundPathPoints = mutableListOf<PointF>()
    val lineBound = remember { mutableStateOf(0F) }
    val maxYValueState = remember { derivedStateOf { lineData.maxYValue() } }
    val maxYValue = maxYValueState.value

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = chartDimens.padding)
            .drawBehind {
                if (axisConfig.showAxis) {
                    drawYAxisWithLabels(axisConfig, maxYValue, textColor = axisConfig.textColor)
                }
            },
        onDraw = {
            val xScaleFactor = size.width.div(lineData.size)
            val yScaleFactor = size.height.div(maxYValue)
            val canvasSize = size
            val radius = size.width.div(70)

            lineBound.value = size.width.div(lineData.count().times(1.2F))

            val lineDataItems: List<Offset> = lineData.mapIndexed { index, data ->
                dataToOffSet(
                    index = index,
                    bound = lineBound.value,
                    size = size,
                    data = data.yValue,
                    yScaleFactor = yScaleFactor
                )
            }.toMutableList().also {
                it.add(Offset(canvasSize.width, canvasSize.height))
            }
            val offsetItems = buildList {
                add(Offset(0f, canvasSize.height))
                addAll(lineDataItems)
            }

            val xValues = offsetItems.map { it.x }
            val pointsPath = Path()

            offsetItems.forEachIndexed { index, offset ->
                val canDrawCircle =
                    curveLineConfig.hasDotMarker && index != 0 && index != offsetItems.size.minus(1)
                if (canDrawCircle) {
                    drawCircle(
                        color = curveLineConfig.dotColor,
                        radius = radius,
                        center = Offset(offset.x, offset.y)
                    )
                }
                if (index > 0) {
                    storePoints(
                        graphPathPoints,
                        backgroundPathPoints,
                        offset,
                        offsetItems[index.minus(1)]
                    )
                }
            }

            pointsPath.apply {
                reset()
                moveTo(offsetItems.first().x, offsetItems.first().y)
                (0.until(offsetItems.size.minus(1))).forEach { index ->
                    cubicTo(
                        graphPathPoints[index].x, graphPathPoints[index].y,
                        backgroundPathPoints[index].x, backgroundPathPoints[index].y,
                        offsetItems[index.plus(1)].x, offsetItems[index.plus(1)].y
                    )
                }
            }

            val backgroundPath = Path().apply {
                addPath(pointsPath)
                lineTo(xScaleFactor * xValues.last(), size.height - yScaleFactor)
                lineTo(xScaleFactor, size.height - yScaleFactor)
                close()
            }

            drawPath(
                path = backgroundPath,
                brush = Brush.verticalGradient(
                    colors = chartColors,
                    endY = size.height - yScaleFactor
                ),
            )
            drawPath(
                path = pointsPath,
                brush = Brush.verticalGradient(
                    colors = lineColors,
                ),
                style = Stroke(
                    width = curveLineConfig.strokeSize.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
    )
}

private fun storePoints(
    controlPoints1: MutableList<PointF>,
    controlPoints2: MutableList<PointF>,
    firstOffset: Offset,
    previousOffset: Offset
) {
    controlPoints1.add(
        PointF(
            (firstOffset.x + previousOffset.x) / 2,
            previousOffset.y
        )
    )
    controlPoints2.add(
        PointF(
            (firstOffset.x + previousOffset.x) / 2,
            firstOffset.y
        )
    )
}
