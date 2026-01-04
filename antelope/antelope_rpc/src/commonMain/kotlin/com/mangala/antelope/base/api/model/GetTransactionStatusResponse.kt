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

import kotlinx.serialization.SerialName

data class GetTransactionStatusResponse (
    val state: String,
    @SerialName("block_number") val blockNumber: Int,
    @SerialName("block_id") val blockId: String,
    @SerialName("block_timestamp") val blockTimestamp: String,
    val expiration: String,
    @SerialName("head_number") val headNumber: Int,
    @SerialName("head_id") val headId: String,
    @SerialName("head_timestamp") val headTimestamp: String,
    @SerialName("irreversible_number") val irreversibleNumber: Int,
    @SerialName("irreversible_id") val irreversibleId: String,
    @SerialName("irreversible_timestamp") val irreversibleTimestamp: String,
    @SerialName("last_tracked_block_id") val lastTrackedBlockId: String
)