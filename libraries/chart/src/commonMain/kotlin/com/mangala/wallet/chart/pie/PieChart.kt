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
package com.mangala.wallet.chart.pie

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.mangala.wallet.chart.pie.config.PieConfig
import com.mangala.wallet.chart.pie.config.PieConfigDefaults
import com.mangala.wallet.chart.pie.config.PieData
import com.mangala.wallet.chart.utils.DrawingUtils
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.atan2
import kotlin.math.PI

private const val TotalProgress = 100
private const val TotalAngle = 360
private const val ReflexAnge = 270

@Composable
fun PieChart(
    pieData: List<PieData>,
    modifier: Modifier = Modifier,
    config: PieConfig = PieConfigDefaults.pieConfigDefaults(),
    onSectionClicked: (Float, Float) -> Unit = { _, _ -> }
) {

    if (pieData.isEmpty()) return
    val pieChartData = pieData.map { it.data }
    val total = pieChartData.sum()
    var startAngle = ReflexAnge.toFloat()
    val pieChartPortion = pieChartData.map { it.times(TotalProgress).div(total) }
    val angleProgress = pieChartPortion.map { TotalAngle.times(it).div(TotalProgress) }
    val padding = if (config.isDonut) 40.dp else 0.dp
    val currentProgressSize = buildList {
        add(angleProgress.first())
        (1 until angleProgress.size).forEach { angle ->
            val newAngle: Float = this@buildList[angle.minus(1)]
            add(angleProgress[angle].plus(newAngle))
        }
    }.toList()
    val currentPie = remember {
        mutableStateOf(-1)
    }

    BoxWithConstraints(
        modifier = modifier
            .padding(padding)
            .aspectRatio(1F)
    ) {

        val minSide = min(constraints.minWidth, constraints.maxHeight)
        Canvas(
            modifier = Modifier
                .height(minSide.dp)
                .width(minSide.dp)
                .pointerInput(true) {
                    detectTapGestures { offset ->
                        val clickedAngle = convertTouchEventPointToAngle(
                            minSide.toFloat(),
                            minSide.toFloat(),
                            offset.x,
                            offset.y
                        )
                        currentProgressSize.forEachIndexed { index, item ->
                            if (clickedAngle <= item) {
                                if (currentPie.value != index) {
                                    currentPie.value = index
                                }
                                onSectionClicked(pieChartPortion[index], pieChartData[index])
                                return@detectTapGestures
                            }
                        }
                    }
                }
        ) {
            angleProgress.forEachIndexed { index, individualAngle ->
                if (!config.isDonut) {
                    drawArc(
                        color = pieData[index].color,
                        startAngle = startAngle,
                        sweepAngle = individualAngle,
                        useCenter = true,
                    )
                } else {
                    val isClickedAndEnabled = config.expandDonutOnClick && currentPie.value != -1 && currentPie.value == index
                    if (isClickedAndEnabled) {
                        drawPieSection(
                            pieChartPortion[currentPie.value],
                            config.textColor,
                            minSide
                        )
                    }
                    drawArc(
                        color = pieData[index].color,
                        startAngle = startAngle,
                        sweepAngle = individualAngle,
                        useCenter = false,
                        style = Stroke(
                            width = if (isClickedAndEnabled) size.width.div(4.5F) else size.width.div(
                                5
                            )
                        )
                    )
                }

                startAngle += individualAngle
            }
        }
    }
}

private fun convertTouchEventPointToAngle(
    width: Float,
    height: Float,
    xPos: Float,
    yPos: Float
): Double {
    val x = xPos.minus(width.times(0.5f))
    val y = yPos.minus(height.times(0.5f))

    var angle = atan2(y.toDouble(), x.toDouble()).plus(PI.div(2)).toDegrees()
    angle = if (angle < 0) angle.plus(TotalAngle) else angle
    return angle
}

fun DrawScope.drawPieSection(
    value: Float,
    percentColor: Color,
    sideSize: Int
) {
    val drawingUtils = DrawingUtils()
    drawContext.canvas.nativeCanvas.apply {
        val fontSize = size.width.div(20).toDp().toPx()

//        drawText(
//            "${value.roundToInt()} %",
//            (sideSize.div(2)).plus(fontSize.div(4)), (sideSize.div(2)).plus(fontSize.div(3)),
//            Paint().apply {
//                color = percentColor.toArgb()
//                textSize = fontSize
//                textAlign = Paint.Align.CENTER
//            }
//        )
        drawingUtils.drawText(
            "${value.roundToInt()} %",
            (sideSize.div(2)).plus(fontSize.div(4)), (sideSize.div(2)).plus(fontSize.div(3)),
            textSize = fontSize,
            textColor = percentColor
        )
    }
}

fun Double.toDegrees(): Double {
    return this * (180.0 / PI)
}