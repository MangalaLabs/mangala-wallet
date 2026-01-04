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
data class GetBlockHeaderStateResponse(
    val id: String,
    @SerialName("block_num") val blockNum: Int?,
    val header: Header?,
    @SerialName("dpos_proposed_irreversible_blocknum") val dposProposedIrreversibleBlocknum: String?,
    @SerialName("dpos_irreversible_blocknum") val dposIrreversibleBlocknum: String?,
    @SerialName("bft_irreversible_blocknum") val bftIrreversibleBlocknum: String?,
    @SerialName("pending_schedule_lib_num") val pendingScheduleLibNum: String?,
    @SerialName("pending_schedule_hash") val pendingScheduleHash: String?,
    @SerialName("pending_schedule") val pendingSchedule: ScheduleBlockHeaderState?,
    @SerialName("active_schedule") val activeSchedule: ScheduleBlockHeaderState?,
    @SerialName("blockroot_merkle") val blockrootMerkle: BlockrootMerkle?,
    @SerialName("producer_to_last_produced") val producerToLastProduced: List<List<String>>?,
    @SerialName("producer_to_last_implied_irb") val producerToLastImpliedIrb: List<List<String>>?,
    @SerialName("block_signing_key") val blockSigningKey: String?,
    @SerialName("confirm_count") val confirmCount: List<String>?,
    val confirmations: List<@Contextual Any>?
) {

    @Serializable
    data class ScheduleBlockHeaderState(
        val version: Int?, val producers: List<ProducerBlockHeaderState>?
    )

    @Serializable
    data class ProducerBlockHeaderState(
        @SerialName("producer_name") val producerName: String?,
        val authority: List<AuthorityBlockHeaderState>?
    )

    @Serializable
    data class AuthorityBlockHeaderState(
        val threshold: Int?, val keys: List<Key>?
    )

    @Serializable
    data class Header(
        val timestamp: String?,
        val producer: String?,
        val confirmed: Int?,
        val previous: String?,
        @SerialName("transaction_mroot") val transactionMroot: String?,
        @SerialName("action_mroot") val actionMroot: String?,
        @SerialName("schedule_version") val scheduleVersion: Int?,
        @SerialName("new_producers") val newProducers: NewProducers?,
        @SerialName("header_extensions") val headerExtensions: List<Int>?,
        @SerialName("new_protocol_features") val newProtocolFeatures: List<@Contextual Any>?,
        @SerialName("producer_signature") val producerSignature: String?,
        val transactions: List<Transaction>?,
        @SerialName("block_extensions") val blockExtensions: List<Int>?,
        @SerialName("ref_block_prefix") val refBlockPrefix: Int?
    )

    @Serializable
    data class Transaction(
        val status: String?,
        @SerialName("cpu_usage_us") val cpuUsageUs: Int?,
        @SerialName("net_usage_words") val netUsageWords: Int?,
        val trx: String?
    )

    @Serializable
    data class NewProducers(
        val version: Int?,
        val producers: List<Producer>?
    )

    @Serializable
    data class Producer(
        @SerialName("producer_name") val producerName: String?, val authority: List<Authority>?
    )

    @Serializable
    data class BlockrootMerkle(
        @SerialName("_active_nodes") val activeNodes: List<String>?,
        @SerialName("_node_count") val nodeCount: String?
    )

    @Serializable
    data class Authority(
        val threshold: Int?,
        val keys: List<Key>?
    )

}