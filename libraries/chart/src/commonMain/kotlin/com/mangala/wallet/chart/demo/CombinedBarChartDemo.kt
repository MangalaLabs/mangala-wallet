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
import com.mangala.wallet.chart.combined.CombinedBarChart
import com.mangala.wallet.chart.combined.model.CombinedBarData
import com.mangala.wallet.chart.demo.colors

@Composable
fun CombinedBarChartDemo() {
    LazyColumn(
        Modifier
            .fillMaxSize()
    ) {
        item {
            CombinedBarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(32.dp),
                onClick = {},
                barColors = colors,
                combinedBarData = listOf(
                    CombinedBarData(10F, 80F, 5F),
                    CombinedBarData(10F, 45F, 40F),
                    CombinedBarData(10F, 30F, 15F),
                    CombinedBarData(10F, 25F, 20F),
                    CombinedBarData(10F, 30F, 25F),
                    CombinedBarData(10F, 25F, 30F),
                    CombinedBarData(10F, 5F, 35F),
                ),
                lineColors = listOf(Color.White, Color.Green, Color.Cyan)
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
    }
}
