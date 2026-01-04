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

package com.mangala.antelope.base.api.model.eoseyes


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RamOhlcResponse(
    @SerialName("code")
    val code: Int? = 0,
    @SerialName("data")
    val `data`: List<Data?>? = listOf(),
    @SerialName("message")
    val message: String? = ""
) {
    @Serializable
    data class Data(
        @SerialName("close")
        val close: Double? = 0.0,
        @SerialName("date")
        val date: Long? = 0,
        @SerialName("high")
        val high: Double? = 0.0,
        @SerialName("low")
        val low: Double? = 0.0,
        @SerialName("open")
        val `open`: Double? = 0.0,
        @SerialName("usd")
        val usd: Double? = 0.0,
        @SerialName("volume")
        val volume: Long? = 0
    )
}