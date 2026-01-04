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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.chart.candle.CandleStickChart
import com.mangala.wallet.chart.candle.model.CandleEntry

val candleDummyData = listOf(
    CandleEntry(49151.66f, 48899.99f, 49000f, 49058.67f),
    CandleEntry(49146.02f, 48840.84f, 48949.8f, 49000f),
    CandleEntry(49222.54f, 48905.83f, 49157.65f, 48947.89f),
    CandleEntry(49474.09f, 49109.58f, 49335.38f, 49149.15f),
    CandleEntry(49500f, 49277.56f, 49314.68f, 49340.75f),
    CandleEntry(49896.7f, 49252.85f, 49844.7f, 49314.67f),
    CandleEntry(49894.47f, 49760.66f, 49765.13f, 49844.7f),
    CandleEntry(49893.15f, 49604.31f, 49893.15f, 49737.15f),
    CandleEntry(49146.02f, 48840.84f, 48949.8f, 49000f),
    CandleEntry(49222.54f, 48905.83f, 49157.65f, 48947.89f),
    CandleEntry(49474.09f, 49109.58f, 49335.38f, 49149.15f),
    CandleEntry(49500f, 49277.56f, 49314.68f, 49340.75f),
    CandleEntry(49896.7f, 49252.85f, 49844.7f, 49314.67f),
    CandleEntry(49894.47f, 49760.66f, 49765.13f, 49844.7f),
    CandleEntry(49893.15f, 49604.31f, 49893.15f, 49737.15f),
    CandleEntry(49151.66f, 48899.99f, 49000f, 49058.67f),
    CandleEntry(49146.02f, 48840.84f, 48949.8f, 49000f),
    CandleEntry(49222.54f, 48905.83f, 49157.65f, 48947.89f),
    CandleEntry(49474.09f, 49109.58f, 49335.38f, 49149.15f),
    CandleEntry(49500f, 49277.56f, 49314.68f, 49340.75f),
    CandleEntry(49896.7f, 49252.85f, 49844.7f, 49314.67f),
    CandleEntry(49894.47f, 49760.66f, 49765.13f, 49844.7f),
    CandleEntry(49893.15f, 49604.31f, 49893.15f, 49737.15f),
)

@Composable
fun CandleStickChartDemo() {
    CandleStickChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        candleEntryData = candleDummyData,
    )
}
