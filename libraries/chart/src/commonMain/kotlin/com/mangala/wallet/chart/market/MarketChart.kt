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
package com.mangala.wallet.chart.market

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant

@Composable
fun MarketChart(
    modifier: Modifier,
    candles: List<Candle>,
    marketChartColors: MarketChartColors = MarketChartColors.defaults(),
    textSize: TextUnit = 12.sp,
    dateTransform: (Instant) -> String,
    priceTransform: (Float) -> String
) {
    val state = rememberSaveable(candles, saver = MarketChartState.Saver) { MarketChartState.getState(candles) }
    val textMeasurer = rememberTextMeasurer()
    val yLabelWidth = remember(candles) { textMeasurer.measure(priceTransform(0.12345678f)).size.width }
    val xAxisTextHeight = remember(candles) { textMeasurer.measure(" \n ").size.height } // Account for 2 lines text

    BoxWithConstraints(modifier = modifier) {
        val chartWidth =
            constraints.maxWidth.toFloat() - yLabelWidth // Subtract to account for y-axis text
        val chartHeight = constraints.maxHeight - 64.dp.value - xAxisTextHeight * 2 // Subtract to account for x-axis text

        state.setViewSize(chartWidth, chartHeight)
        state.calculateGridWidth()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(marketChartColors.backgroundColor)
                .transformable(state.transformableState)
                .scrollable(state.scrollableState, Orientation.Horizontal)
        ) {
            clipRect {
                state.timeLines.forEach { candle ->
                    val offset = state.xOffset(candle)
                    if (offset !in 0f..chartWidth) return@forEach
                    drawLine(
                        color = marketChartColors.lineColor,
                        strokeWidth = 2.dp.value,
                        start = Offset(offset, 0f),
                        end = Offset(offset, chartHeight)
                    )
                    val lines = dateTransform(Instant.fromEpochMilliseconds(candle.time)).split("\n")
                    val singleLineHeight = textMeasurer.measure(" ").size.height
                    lines.forEachIndexed { index, text ->
                        val measureResult = textMeasurer.measure(text, TextStyle(fontSize = textSize))
                        drawText(
                            textMeasurer,
                            text,
                            Offset(
                                offset - measureResult.size.width / 2,
                                chartHeight + xAxisTextHeight + (singleLineHeight * index)
                            ),
                            TextStyle(fontSize = textSize, color = marketChartColors.textColor)
                        )
                    }
                }

                state.priceLines.forEach { value: Float ->
                    val yOffset = state.yOffset(value)
                    val text = priceTransform(value)
                    drawLine(
                        color = marketChartColors.lineColor,
                        strokeWidth = 2.dp.value,
                        start = Offset(0f, yOffset),
                        end = Offset(chartWidth, yOffset)
                    )
                    val measureResult = textMeasurer.measure(text, TextStyle(fontSize = textSize))
                    drawText(
                        textMeasurer,
                        text,
                        Offset(chartWidth + 8.dp.value, yOffset - measureResult.size.height / 2),
                        TextStyle(fontSize = textSize, color = marketChartColors.textColor)
                    )
                }

                state.lastCandle?.let {
                    val text = priceTransform(it.close)
                    val measureResult = textMeasurer.measure(text, TextStyle(fontSize = textSize))

                    val color =
                        if (it.open > it.close) marketChartColors.negativeColor else marketChartColors.positiveColor
                    val yOffset = state.yOffset(it.close) - measureResult.size.height / 2

                    drawRect(
                        color = color,
                        topLeft = Offset(chartWidth + 8.dp.value, yOffset),
                        size = Size(
                            measureResult.size.width.toFloat(),
                            measureResult.size.height.toFloat()
                        )
                    )
                    drawText(
                        textMeasurer,
                        text,
                        Offset(chartWidth + 8.dp.value, yOffset),
                        TextStyle(
                            fontSize = textSize,
                            color = marketChartColors.textOnLastPriceColor
                        )
                    )
                }

                state.latestCandle?.let {
                    val yOffset = state.yOffset(it.close)
                    if (yOffset !in 0f..chartHeight) return@let

                    val color =
                        if (it.open > it.close) marketChartColors.negativeColor else marketChartColors.positiveColor
                    drawLine(
                        color = color,
                        strokeWidth = 2.dp.value,
                        start = Offset(0f, yOffset),
                        end = Offset(chartWidth, yOffset),
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(10f, 10f),
                            phase = 5f
                        )
                    )
                }

                state.visibleCandles.forEach { candle ->
                    val xOffset = state.xOffset(candle)
                    val color = if (candle.open > candle.close) {
                        marketChartColors.negativeColor
                    } else {
                        marketChartColors.positiveColor
                    }
                    drawLine(
                        color = color,
                        strokeWidth = 2.dp.value,
                        start = Offset(xOffset, state.yOffset(candle.low)),
                        end = Offset(xOffset, state.yOffset(candle.high))
                    )
                    if (candle.open > candle.close) {
                        drawRect(
                            color = color,
                            topLeft = Offset(xOffset - 6.dp.value, state.yOffset(candle.open)),
                            size = Size(
                                12.dp.value,
                                state.yOffset(candle.close) - state.yOffset(candle.open)
                            )
                        )
                    } else {
                        drawRect(
                            color = color,
                            topLeft = Offset(xOffset - 6.dp.value, state.yOffset(candle.close)),
                            size = Size(
                                12.dp.value,
                                state.yOffset(candle.open) - state.yOffset(candle.close)
                            )
                        )
                    }
                }
            }
        }
    }
}