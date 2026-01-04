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
data class GetInfoResponse(
    @SerialName("server_version") val serverVersion: String?,
    @SerialName("chain_id") val chainId: String?,
    @SerialName("head_block_num") val headBlockNum: Int?,
    @SerialName("head_block_id") val headBlockId: String?,
    @SerialName("head_block_time") val headBlockTime: String?,
    @SerialName("head_block_producer") val headBlockProducer: String?,
    @SerialName("last_irreversible_block_num") val lastIrreversibleBlockNum: Int?,
    @SerialName("last_irreversible_block_id") val lastIrreversibleBlockId: String?,
    @SerialName("virtual_block_cpu_limit") val virtualBlockCpuLimit: Int?,
    @SerialName("virtual_block_net_limit") val virtualBlockNetLimit: Int?,
    @SerialName("block_cpu_limit") val blockCpuLimit: Int?,
    @SerialName("block_net_limit") val blockNetLimit: Int?,
    @SerialName("server_version_string") val serverVersionString: String?,
    @SerialName("fork_db_head_block_num") val forkDbHeadBlockNum: Int?,
    @SerialName("fork_db_head_block_id") val forkDbHeadBlockId: String?
)
