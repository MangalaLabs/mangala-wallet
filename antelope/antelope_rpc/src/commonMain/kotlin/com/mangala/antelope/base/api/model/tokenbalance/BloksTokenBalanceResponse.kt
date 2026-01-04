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
data class BloksTokenBalanceResponse(
    @SerialName("tokens")
    val tokens: List<Token?>? = listOf(),
    @SerialName("total_tokens_usd")
    val totalTokensUsd: Double? = 0.0
) {
    @Serializable
    data class Token(
        @SerialName("amount")
        val amount: Double? = 0.0,
        @SerialName("contract")
        val contract: String? = "",
        @SerialName("currency")
        val currency: String? = "",
        @SerialName("decimals")
        val decimals: String? = "",
        @SerialName("exchanges")
        val exchanges: List<Exchange?>? = listOf(),
        @SerialName("key")
        val key: String? = "",
        @SerialName("metadata")
        val metadata: Metadata? = Metadata(),
        @SerialName("usd_value")
        val usdValue: Double? = 0.0
    ) {
        @Serializable
        data class Exchange(
            @SerialName("name")
            val name: String? = "",
            @SerialName("price")
            val price: Double? = 0.0,
            @SerialName("price_usd")
            val priceUsd: Double? = 0.0
        )

        @Serializable
        data class Metadata(
            @SerialName("created_at")
            val createdAt: String? = "",
            @SerialName("logo")
            val logo: String? = "",
            @SerialName("name")
            val name: String? = "",
            @SerialName("website")
            val website: String? = ""
        )
    }
}