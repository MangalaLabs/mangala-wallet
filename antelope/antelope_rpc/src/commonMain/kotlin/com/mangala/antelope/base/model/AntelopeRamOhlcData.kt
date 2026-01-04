/*
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mangala.antelope.base.model

import kotlinx.datetime.Instant

data class AntelopeRamOhlcData(
    val dataPoints: List<OhlcDataPoint>,
    val samplingInterval: SamplingInterval
) {
    data class OhlcDataPoint(
        val date: Instant,
        val open: Double,
        val high: Double,
        val low: Double,
        val close: Double,
        val usd: Double,
        val volume: Long
    )

    fun getSparklineData(): List<Double> {
        return dataPoints.map { it.close }
    }

    fun getPriceChangePercentage24h(): Double? {
        return when (samplingInterval) {
            SamplingInterval.ONE_HOUR -> {
                val startPoint = dataPoints.getOrNull(dataPoints.lastIndex - 24) ?: return null
                val startPrice = startPoint.open
                val endPrice = dataPoints.last().close

                val result = getPriceChangePercentage(startPrice, endPrice)

                result
            }
            else -> TODO()
        }
    }

    fun getPriceChangePercentage7d(): Double? {
        return when (samplingInterval) {
            SamplingInterval.ONE_HOUR -> {
                val startPoint = dataPoints.getOrNull(dataPoints.lastIndex - (24 * 7)) ?: return null
                val startPrice = startPoint.open
                val endPrice = dataPoints.last().close

                getPriceChangePercentage(startPrice, endPrice)
            }
            else -> TODO()
        }
    }

    private fun getPriceChangePercentage(startPrice: Double, endPrice: Double) = if (startPrice == 0.0) 0.0 else
        ((endPrice - startPrice) / startPrice) * 100
}

enum class SamplingInterval(val value: String, val cacheRefreshInterval: Long) {
    FIVE_MINUTES("5m", 5 * 60 * 1000),
    FIFTEEN_MINUTES("15m", 10 * 60 * 1000),
    THIRTY_MINUTES("30m", 15 * 60 * 1000),
    ONE_HOUR("1h", 15 * 60 * 1000),
    FOUR_HOURS("4h", 15 * 60 * 1000),
    ONE_DAY("1d", 30 * 60 * 1000),
    ONE_WEEK("1w", 30 * 60 * 1000),
    ONE_MONTH("1m", 30 * 60 * 1000)
}