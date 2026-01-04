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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.chart.bar.GroupedBarChart
import com.mangala.wallet.chart.bar.model.BarData
import com.mangala.wallet.chart.bar.model.GroupedBarData

@Composable
fun GroupBarChartDemo() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            GroupedBarChart(
                modifier = Modifier
                    .size(width = 500.dp, height = 300.dp)
                    .padding(20.dp),
                groupedBarData = listOf(
                    GroupedBarData(
                        listOf(
                            BarData(10F, 35F),
                            BarData(20F, 25F),
                            BarData(10F, 50F),
                        ),
                        colors = pcolors
                    ),
                    GroupedBarData(
                        listOf(
                            BarData(10F, 35F),
                            BarData(20F, 25F),
                            BarData(10F, 50F),
                        ),
                        colors = pcolors
                    ),
                    GroupedBarData(
                        listOf(
                            BarData(10F, 35F),
                            BarData(20F, 25F),
                            BarData(10F, 50F),
                        ),
                        colors = pcolors
                    ),
                ),
            )
        }
    }
}
