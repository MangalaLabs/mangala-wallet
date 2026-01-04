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
package com.mangala.wallet.chart.combined.common.component

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.mangala.wallet.chart.combined.model.CombinedBarData
import com.mangala.wallet.chart.utils.DrawingUtils

internal fun DrawScope.drawCombinedBarLabel(
    combinedBarData: CombinedBarData,
    barWidth: Float,
    barHeight: Float,
    topLeft: Offset,
    labelTextColor: Color
) {
    val drawingUtils = DrawingUtils()
    drawIntoCanvas {
        it.nativeCanvas.apply {
//            drawText(
//                combinedBarData.xValue.toString(),
//                topLeft.x.plus(barWidth.div(2)),
//                topLeft.y.plus(barHeight.plus(barWidth.div(2))),
//                Paint().apply {
//                    color = labelTextColor.toArgb()
//                    textSize = size.width.div(30)
//                    textAlign = Paint.Align.CENTER
//                }
//            )
            drawingUtils.drawText(
                combinedBarData.xValue.toString(),
                topLeft.x.plus(barWidth.div(2)),
                topLeft.y.plus(barHeight.plus(barWidth.div(2))),
                size.width.div(30),
                textColor = labelTextColor
            )
        }
    }
}

internal fun DrawScope.drawLineLabels(
    offset: Offset,
    combinedBarData: CombinedBarData,
    lineLabelColor: Pair<Color, Color>,
) {
    val textSp = size.width.div(25)
    val drawingUtils = DrawingUtils()
    drawIntoCanvas {
        drawRoundRect(
            cornerRadius = CornerRadius(50F),
            color = lineLabelColor.first,
            topLeft = Offset(
                offset.x.minus(textSp.times(1.3F)),
                offset.y.minus(textSp.times(2.1F))
            ),
            size = Size(
                textSp.times(2.7F),
                textSp.times(1.5F)
            )
        )
        it.nativeCanvas.apply {
            drawingUtils.drawText(
                combinedBarData.yLineValue.toString(),
                offset.x,
                offset.y.minus(textSp),
                textSp,
                textColor = lineLabelColor.second
            )
//            drawText(
//                combinedBarData.yLineValue.toString(),
//                offset.x,
//                offset.y.minus(textSp),
//                Paint().apply {
//                    color = lineLabelColor.second.toArgb()
//                    textSize = textSp
//                    textAlign = Paint.Align.CENTER
//                }
//            )
        }
    }
}
