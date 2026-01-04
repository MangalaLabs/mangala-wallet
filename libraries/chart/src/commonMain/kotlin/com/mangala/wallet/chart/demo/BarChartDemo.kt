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
import com.mangala.wallet.chart.bar.BarChart
import com.mangala.wallet.chart.bar.model.BarData

@Composable
fun BarChartDemo(colors: List<Color>) {
    LazyColumn(
        Modifier
            .fillMaxSize()
    ) {
        item {
            BarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(32.dp),
                onBarClick = {},
                colors = colors,
                barData = listOf(
                    BarData(10F, 35F),
                    BarData(20F, 25F),

                    )
            )
        }
        item {
            Text(
                text = "Gradient Bar Chart",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        item {
            BarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(32.dp),
                onBarClick = {},
                color = colors.first(),
                barData = listOf(
                    BarData(10F, 35F),
                    BarData(20F, 25F),
                    BarData(10F, 50F),
                    BarData(60F, 10F),
                    BarData(10F, 15F),
                    BarData(50F, 100F),
                    BarData(20F, 25F),
                )
            )
        }
        item {
            Text(
                fontSize = 16.sp,
                text = "Solid Bar Chart",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
