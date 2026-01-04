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
package com.mangala.wallet.chart.common.axis

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.mangala.wallet.chart.utils.DrawingUtils
import com.mangala.wallet.utils.DecimalFormat

internal fun DrawScope.drawYAxisWithLabels(
    axisConfig: AxisConfig,
    maxValue: Float,
    isCandleChart: Boolean = false,
    textColor: Color = Color.Black
) {
    val graphYAxisEndPoint = size.height.div(4)
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 20f), 0f)
    val labelScaleFactor = maxValue.div(4)

    repeat(5) { index ->
        val yAxisEndPoint = graphYAxisEndPoint.times(index)
        val drawingUtils = DrawingUtils()
        if (axisConfig.showUnitLabels) {
            drawIntoCanvas {
                it.nativeCanvas.apply {
                    drawingUtils.drawText(
                        getLabelText(labelScaleFactor.times(4.minus(index)), isCandleChart),
                        0F.minus(25),
                        yAxisEndPoint.minus(10),
                        size.width.div(30),
                        textColor = textColor
                    )

//                    drawText(
//                        getLabelText(labelScaleFactor.times(4.minus(index)), isCandleChart),
//                        0F.minus(25),
//                        yAxisEndPoint.minus(10),
//                        Paint().apply {
//                            color = textColor.toArgb()
//                            textSize = size.width.div(30)
//                            textAlign = Paint.Align.CENTER
//                        }
//                    )
                }
            }
        }
        if (index != 0) {
            drawLine(
                start = Offset(x = 0f, y = yAxisEndPoint),
                end = Offset(x = size.width, y = yAxisEndPoint),
                color = axisConfig.xAxisColor,
                pathEffect = if (axisConfig.isAxisDashed) pathEffect else null,
                alpha = 0.1F,
                strokeWidth = size.width.div(200)
            )
        }
    }
}

private fun getLabelText(value: Float, isCandleChart: Boolean): String {
    val pattern = if (isCandleChart) "#" else "#.##"
    return DecimalFormat(pattern).format(value.toDouble()).toString()
}

internal fun DrawScope.drawXLabel(
    data: Any,
    centerOffset: Offset,
    radius: Float,
    count: Int,
    textColor: Color = Color.Black
) {
//    val divisibleFactor = if (count > 10) count else 1
//    val textSizeFactor = if (count > 10) 3 else 30
//    drawIntoCanvas {
//        it.nativeCanvas.apply {
//            drawText(
//                data.toString(),
//                centerOffset.x,
//                size.height.plus(radius.times(4)),
//                Paint().apply {
//                    color = textColor.toArgb()
//                    textSize = size.width.div(textSizeFactor).div(divisibleFactor)
//                    textAlign = Paint.Align.CENTER
//                }
//            )
//        }
//    }

    val divisibleFactor = if (count > 10) count else 1
    val textSizeFactor = if (count > 10) 3 else 30
    val drawingUtils = DrawingUtils()
    drawIntoCanvas {
        it.nativeCanvas.apply {
            drawingUtils.drawText(
                text = data.toString(),
                x = centerOffset.x,
                y = size.height + radius * 4,
                textSize = size.width / textSizeFactor / divisibleFactor,
                textColor = textColor
            )
        }
    }
}


