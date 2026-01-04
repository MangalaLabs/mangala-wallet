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

package com.mangala.antelope.base.api.model.tokenbalance


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JungleTokenBalanceResponse(
    @SerialName("account")
    val account: String? = "",
    @SerialName("last_indexed_block")
    val lastIndexedBlock: Int? = 0,
    @SerialName("last_indexed_block_time")
    val lastIndexedBlockTime: String? = "",
    @SerialName("query_time_ms")
    val queryTimeMs: Double? = 0.0,
    @SerialName("tokens")
    val tokens: List<Token?>? = listOf()
) {
    @Serializable
    data class Token(
        @SerialName("amount")
        val amount: Double? = 0.0,
        @SerialName("contract")
        val contract: String? = "",
        @SerialName("precision")
        val precision: Int? = 0,
        @SerialName("symbol")
        val symbol: String? = ""
    )
}