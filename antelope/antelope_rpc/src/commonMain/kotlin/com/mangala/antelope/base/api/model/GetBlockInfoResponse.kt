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

import kotlinx.serialization.*

@Serializable
data class GetBlockInfoResponse(
    @SerialName("block_num") val blockNum: Int?,
    @SerialName("ref_block_num") val refBlockNum: Int?,
    val id: String?,
    val timestamp: String?,
    val producer: String?,
    val confirmed: Int?,
    val previous: String?,
    @SerialName("transaction_mroot") val transactionMroot: String?,
    @SerialName("action_mroot") val actionMroot: String?,
    @SerialName("schedule_version") val scheduleVersion: Int?,
    @SerialName("producer_signature") val producerSignature: String?,
    @SerialName("ref_block_prefix") val refBlockPrefix: Long?
)
