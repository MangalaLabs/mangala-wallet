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
package com.mangala.wallet.chart.pie.config

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class PieData(
    val data: Float,
    val color: Color = generateRandomColor()
)

data class PieConfig(
    val isDonut: Boolean = true,
    val expandDonutOnClick: Boolean = true,
    val textColor: Color = Color.Black,
)

internal object PieConfigDefaults {
    fun pieConfigDefaults() = PieConfig()
}

private fun generateRandomColor(): Color {
    return Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
}
