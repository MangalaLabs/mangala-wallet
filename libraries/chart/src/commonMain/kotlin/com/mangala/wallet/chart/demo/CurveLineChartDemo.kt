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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.chart.line.CurveLineChart
import com.mangala.wallet.chart.line.config.CurveLineConfig
import com.mangala.wallet.chart.line.model.LineData
import kotlin.random.Random

@Composable
fun CurveLineChartDemo(colors: List<Color>) {
    val dynamicData = remember {
        mutableStateListOf(
            LineData(10F, 35F),
            LineData(10F, 45F),
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        item {
            Column {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        val lastItem = dynamicData.last()
                        dynamicData.add(
                            LineData((lastItem.xValue as Float) + 10f, lastItem.yValue + Random.nextFloat() * 10f)
                        )
                    }
                ) {
                    Text(text = "Add random item")
                }
                CurveLineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    chartColors = colors,
                    lineColors = pcolors,
                    lineData = dynamicData
                )
            }
        }
        item {
            Text(
                text = "Line Chart with dynamic data",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center
            )
        }

        item {
            CurveLineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 300.dp),
                chartColors = colors,
                lineColors = pcolors,
                lineData = listOf(
                    LineData(10F, 35F),
                    LineData(20F, 25F),
                    LineData(50F, 100F),
                    LineData(20F, 25F),
                )
            )
        }
        item {
            Text(
                text = "Curved Line Graph with Dot Marker",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center
            )
        }

        item {
            CurveLineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 300.dp),
                chartColors = colors,
                lineColors = pcolors,
                lineData = listOf(
                    LineData(10F, 35F),
                    LineData(20F, 25F),
                    LineData(50F, 100F),
                    LineData(20F, 25F),
                ),
                curveLineConfig = CurveLineConfig(false)
            )
        }
        item {
            Text(
                text = "Curved Line Graph without Dot Marker",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}