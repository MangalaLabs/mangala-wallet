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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.chart.pie.PieChart
import com.mangala.wallet.chart.pie.config.PieConfig
import com.mangala.wallet.chart.pie.config.PieData

@Composable
fun PieChartDemo() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val pieData = listOf(
            PieData(3F),
            PieData(1F),
            PieData(1F),
        )

        item {
            PieChart(
                modifier = Modifier
                    .fillMaxSize(),
                pieData = pieData,
                config = PieConfig(isDonut = true, expandDonutOnClick = true),
                onSectionClicked = { percent, value ->

                }
            )
        }
        item {
            Text(
                text = "Donut Chart",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )
        }
        item {
            PieChart(
                modifier = Modifier
                    .fillMaxSize(),
                pieData = pieData,
                config = PieConfig(isDonut = true, expandDonutOnClick = false),
                onSectionClicked = { percent, value ->

                }
            )
        }
        item {
            Text(
                text = "Donut Chart Non Expandable",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )
        }
        item {
            PieChart(
                modifier = Modifier
                    .fillMaxSize(),
                pieData = pieData,
                config = PieConfig(isDonut = false, expandDonutOnClick = true),
                onSectionClicked = { percent, value ->
                }
            )
        }
        item {
            Text(
                text = "Pie Chart",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}