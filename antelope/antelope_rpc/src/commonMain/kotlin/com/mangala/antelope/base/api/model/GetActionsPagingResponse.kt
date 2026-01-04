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
import kotlinx.serialization.Serializable

@Serializable
class GetActionsPagingResponse(
    @SerialName("actions")
    val actions: List<EosAction>? = null,
    @SerialName("head_block_num")
    val headBlockNum: Long? = null,
    @SerialName("last_irreversible_block")
    val lastIrreversibleBlock: Long? = null
)

@Serializable
data class ActionTrace(
//    @SerialName("account_ram_deltas")
//    val accountRamDeltas: List<Any>? = null,  // Assuming this could have a structure; using Any for placeholder
    @SerialName("act")
    val act: Act? = null,
    @SerialName("action_ordinal")
    val actionOrdinal: Int? = null,
    @SerialName("block_num")
    val blockNum: Long? = null,
    @SerialName("block_time")
    val blockTime: String? = null,
    @SerialName("closest_unnotified_ancestor_action_ordinal")
    val closestUnnotifiedAncestorActionOrdinal: Int? = null,
    @SerialName("context_free")
    val contextFree: Boolean? = null,
    @SerialName("creator_action_ordinal")
    val creatorActionOrdinal: Int? = null,
    @SerialName("elapsed")
    val elapsed: Int? = null,
    @SerialName("producer_block_id")
    val producerBlockId: String? = null,
//    @SerialName("receipt")
//    val receipt: Receipt? = null, //Wrong at auth_sequence
    @SerialName("receiver")
    val receiver: String? = null,
    @SerialName("trx_id")
    val trxId: String? = null
)

@Serializable
data class Drop(
    @SerialName("bound")
    val bound: Boolean? = null,
    @SerialName("created")
    val created: String? = null,
    @SerialName("owner")
    val owner: String? = null,
    @SerialName("seed")
    val seed: String? = null
)
