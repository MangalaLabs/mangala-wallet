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

package com.mangala.antelope.base.data.repository.mapper

import com.mangala.antelope.base.api.model.eoseyes.RamOhlcResponse
import com.mangala.antelope.base.model.AntelopeRamOhlcData
import kotlinx.datetime.Instant

fun RamOhlcResponse.toAntelopeRamOhlcDataPoints(): List<AntelopeRamOhlcData.OhlcDataPoint> {
    return this.data?.map {
        AntelopeRamOhlcData.OhlcDataPoint(
            close = it?.close ?: 0.0,
            date = Instant.fromEpochMilliseconds(it?.date ?: 0),
            high = it?.high ?: 0.0,
            low = it?.low ?: 0.0,
            open = it?.open ?: 0.0,
            usd = it?.usd ?: 0.0,
            volume = it?.volume ?: 0
        )
    } ?: emptyList()
}