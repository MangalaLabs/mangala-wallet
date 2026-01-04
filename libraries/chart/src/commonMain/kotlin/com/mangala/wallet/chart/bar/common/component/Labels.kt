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
package com.mangala.wallet.chart.bar.common.component


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.drawText
import com.mangala.wallet.chart.utils.DrawingUtils
import com.mangala.wallet.mokoresources.MR.colors.textColor

internal fun DrawScope.drawBarLabel(
    xValue: Any,
    barWidth: Float,
    barHeight: Float,
    topLeft: Offset,
    count: Int,
    labelTextColor: Color
) {
    val heightDisplacement = if (count < 7) barWidth.div(4F) else barWidth.div(2)
    val divisibleFactor = if (count > 10) count else 1
    val textSizeFactor = if (count > 10) 3 else 28
    val drawingUtils = DrawingUtils()
    drawIntoCanvas {
        it.nativeCanvas.apply {
            drawingUtils.drawText(
                xValue.toString(),
                topLeft.x.plus(barWidth.div(2)),
                topLeft.y.plus(barHeight.plus(heightDisplacement)),
                size.width.div(textSizeFactor).div(divisibleFactor),
                textColor = labelTextColor
            )
//            drawText(
//                xValue.toString(),
//                topLeft.x.plus(barWidth.div(2)),
//                topLeft.y.plus(barHeight.plus(heightDisplacement)),
//                Paint().apply {
//                    color = labelTextColor.toArgb()
//                    textSize = size.width.div(textSizeFactor).div(divisibleFactor)
//                    textAlign = Paint.Align.CENTER
//                }
//            )
        }
    }

}
