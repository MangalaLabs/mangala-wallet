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

package com.mangala.antelope.base.api.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetRequiredKeysResponse(
    val transaction: TransactionDetails?,
    @SerialName("available_keys") val availableKeys: List<String>?
) {
    @Serializable
    data class TransactionDetails(
        val expiration: String?,
        @SerialName("ref_block_num") val refBlockNum: Int?,
        @SerialName("ref_block_prefix") val refBlockPrefix: Long?,
        @SerialName("max_net_usage_words") val maxNetUsageWords: String?,
        @SerialName("max_cpu_usage_ms") val maxCpuUsageMs: String?,
        @SerialName("delay_sec") val delaySec: Int?,
        @SerialName("context_free_actions") val contextFreeActions: List<Action>?,
        val actions: List<Action>?,
        @SerialName("transaction_extensions") val transactionExtensions: List<List<Int>>?
    )

    @Serializable
    data class Action(
        val account: String,
        val name: String?,
        val authorization: List<Authorization>?,
        val data: Map<String, @Contextual Any>?,
        @SerialName("hex_data") val hexData: String?
    )

    @Serializable
    data class Authorization(
        val actor: String?,
        val permission: String?
    )
}
