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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.chart.bar.StackedBarChart
import com.mangala.wallet.chart.bar.model.StackedBarData

private val stackedBarData = listOf(
    StackedBarData(1F, listOf(40F, 50F, 10F)),
    StackedBarData(2F, listOf(30F, 40F, 5F)),
    StackedBarData(3F, listOf(90F, 40F, 71F)),
    StackedBarData(4F, listOf(13F, 2F, 8F)),
    StackedBarData(5F, listOf(30F, 40F, 20F)),
    StackedBarData(6F, listOf(30F, 40F, 20F)),
    StackedBarData(7F, listOf(40F, 50F, 10F)),
    StackedBarData(8F, listOf(30F, 40F, 5F)),
    StackedBarData(9F, listOf(90F, 40F, 71F)),
    StackedBarData(10F, listOf(13F, 2F, 8F)),
    StackedBarData(11F, listOf(30F, 40F, 20F)),
    StackedBarData(1F, listOf(40F, 50F, 10F)),
    StackedBarData(2F, listOf(30F, 40F, 5F)),
    StackedBarData(3F, listOf(90F, 40F, 71F)),
    StackedBarData(4F, listOf(13F, 2F, 8F)),
    StackedBarData(5F, listOf(30F, 40F, 20F)),
    StackedBarData(6F, listOf(30F, 40F, 20F)),
    StackedBarData(7F, listOf(40F, 50F, 10F)),
    StackedBarData(8F, listOf(30F, 40F, 5F)),
    StackedBarData(9F, listOf(90F, 40F, 71F)),
    StackedBarData(10F, listOf(13F, 2F, 8F)),
    StackedBarData(11F, listOf(30F, 40F, 20F)),
)

@Composable
fun StackedBarChartDemo() {
    LazyColumn(
        Modifier
            .fillMaxSize()
    ) {
        item {
            StackedBarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(32.dp),
                stackBarData = stackedBarData,
                colors = pcolors
            )
        }
        item {
            StackedBarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(32.dp),
                stackBarData = stackedBarData.takeLast(3),
                onBarClick = {
                },
                colors = pcolors

            )
        }
        item {
            StackedBarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(32.dp),
                colors = pcolors,
                stackBarData = stackedBarData.takeLast(22)
            )
        }
    }
}
