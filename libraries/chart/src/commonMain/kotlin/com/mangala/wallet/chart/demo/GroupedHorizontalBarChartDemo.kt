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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.chart.horizontalbar.GroupedHorizontalBarChart
import com.mangala.wallet.chart.horizontalbar.config.HorizontalBarConfig
import com.mangala.wallet.chart.horizontalbar.config.StartDirection
import com.mangala.wallet.chart.horizontalbar.model.GroupedHorizontalBarData
import com.mangala.wallet.chart.horizontalbar.model.HorizontalBarData

internal val pcolors = listOf(Color(0xFF9DB39A), Color(0xFFADBA9A), Color(0xFFBEC196))

@Composable
fun GroupedHorizontalBarChartDemo() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            GroupedHorizontalBarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 300.dp),
                groupedBarData = listOf(
                    GroupedHorizontalBarData(
                        listOf(
                            HorizontalBarData(15F, 30F),
                            HorizontalBarData(25F, 40F),
                            HorizontalBarData(13F, 50F),
                        ),
                        colors = pcolors
                    ),
                    GroupedHorizontalBarData(
                        listOf(
                            HorizontalBarData(15F, 50F),
                            HorizontalBarData(25F, 70F),
                            HorizontalBarData(13F, 80F),
                        ),
                        colors = pcolors
                    ),
                    GroupedHorizontalBarData(
                        listOf(
                            HorizontalBarData(15F, 90F),
                            HorizontalBarData(25F, 100F),
                            HorizontalBarData(13F, 50F),
                        ),
                        colors = pcolors
                    ),
                ),
                onBarClick = {}
            )
        }
        item {
            Text(
                text = "Group Chart with Start Direction Right",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center
            )
        }
        item {
            GroupedHorizontalBarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 300.dp),
                horizontalBarConfig = HorizontalBarConfig(startDirection = StartDirection.Left),
                groupedBarData = listOf(
                    GroupedHorizontalBarData(
                        listOf(
                            HorizontalBarData(15F, 30F),
                            HorizontalBarData(25F, 40F),
                            HorizontalBarData(13F, 50F),
                        ),
                        colors = pcolors
                    ),
                    GroupedHorizontalBarData(
                        listOf(
                            HorizontalBarData(15F, 50F),
                            HorizontalBarData(25F, 70F),
                            HorizontalBarData(13F, 80F),
                        ),
                        colors = pcolors
                    ),
                    GroupedHorizontalBarData(
                        listOf(
                            HorizontalBarData(15F, 90F),
                            HorizontalBarData(25F, 100F),
                            HorizontalBarData(13F, 50F),
                        ),
                        colors = pcolors
                    ),
                ),
                onBarClick = {}
            )
        }
        item {
            Text(
                text = "Group Chart with Start Direction Left",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
