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
package com.mangala.wallet.chart.demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.chart.circle.CircleChart
import com.mangala.wallet.chart.circle.model.CircleData

internal val colors = listOf(Color(0xFF20A100), Color(0xFFABEDCD))

@Composable
fun CircleChartDemo() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            CircleChart(
                modifier = Modifier
                    .scale(1f)
                    .size(400.dp)
                    .padding(20.dp),
                circleData = listOf(
                    CircleData(10F, 235F, color = Color(0xFFfafa6e)),
                    CircleData(10F, 135F, color = Color(0xFFc4ec74)),
                    CircleData(10F, 315F, color = Color(0xFF92dc7e)),
                    CircleData(20F, 50F, color = Color(0xFF64c987)),
                    CircleData(30F, 315F, color = Color(0xFF39b48e))
                ),
                isAnimated = false
            )
        }
        item {
            Text(
                text = "CircleChart with individual colors",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center
            )
        }
        item {
            CircleChart(
                modifier = Modifier
                    .scale(1f)
                    .size(400.dp)
                    .padding(20.dp),
                circleData = listOf(
                    CircleData(10F, 235F, Color.Magenta),
                    CircleData(10F, 135F, Color.Red),
                    CircleData(10F, 315F, Color.Green),
                    CircleData(20F, 50F, Color.Gray),
                    CircleData(30F, 315F)
                ),
                color = Color.Yellow
            )
        }
        item {
            Text(
                text = "CircleChart with optional colors",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center
            )
        }
        item {
            CircleChart(
                modifier = Modifier
                    .scale(1f)
                    .size(400.dp)
                    .padding(20.dp),
                circleData = listOf(
                    CircleData(10F, 235F),
                    CircleData(10F, 135F),
                    CircleData(10F, 315F),
                    CircleData(20F, 50F),
                    CircleData(30F, 315F)
                ),
                colors = colors,
                isAnimated = false
            )
        }
        item {
            Text(
                text = "CircleChart with one gradient color shade",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
