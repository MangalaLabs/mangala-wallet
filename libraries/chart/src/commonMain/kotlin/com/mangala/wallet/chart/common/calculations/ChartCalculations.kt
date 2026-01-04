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
package com.mangala.wallet.chart.common.calculations

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.mangala.wallet.chart.line.model.LineData

private const val BoundFactor = 1.2F

internal fun dataToOffSet(
    index: Int,
    bound: Float,
    size: Size,
    data: Float,
    yScaleFactor: Float
): Offset {
    val startX = index.times(bound.times(BoundFactor))
    val endX = index.plus(1).times(bound.times(BoundFactor))
    val y = size.height.minus(data.times(yScaleFactor))
    return Offset(((startX.plus(endX)).div(2F)), y)
}

internal fun dataToOffSetLineChart(
    index: Int,
    bound: Float,
    size: Size,
    distance: Float,
    data: LineData,
    yScaleFactor: Float
): Offset {
    val startX = index.times(bound.times(BoundFactor))
    val endX = index.plus(1).times(bound.times(BoundFactor))
    val y = size.height.minus(data.yValue.times(yScaleFactor))

//    val y = size.height / data.yValue * distance
    return Offset(((startX.plus(endX)).div(2F)), y)
}