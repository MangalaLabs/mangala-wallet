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
data class PushBlockRequest(
    val timestamp: String,
    val producer: String,
    val confirmed: Int,
    val previous: String,
    @SerialName("transaction_mroot") val transactionMroot: String,
    @SerialName("action_mroot") val actionMroot: String,
    @SerialName("schedule_version") val scheduleVersion: Int,
    @SerialName("new_producers") val newProducers: NewProducers?,
    @SerialName("header_extensions") val headerExtensions: List<Int>,
    @SerialName("new_protocol_features") val newProtocolFeatures: List<@Contextual Any>,
    @SerialName("producer_signature") val producerSignature: String,
    val transactions: List<Transaction>,
    @SerialName("block_extensions") val blockExtensions: List<Int>,
    val id: String,
    @SerialName("block_num") val blockNum: Int,
    @SerialName("ref_block_prefix") val refBlockPrefix: Int
) {

    @Serializable
    data class NewProducers(
        val version: Int,
        val producers: List<Producer>
    )

    @Serializable
    data class Producer(
        @SerialName("producer_name") val producerName: String,
        val authority: List<Authority>
    )

    @Serializable
    data class Authority(
        val threshold: Int,
        val keys: List<ProducerKey>
    )

    @Serializable
    data class ProducerKey(
        val key: String,
        val weight: Int
    )

    @Serializable
    data class Transaction(
        val status: String,
        @SerialName("cpu_usage_us") val cpuUsageUs: Int,
        @SerialName("net_usage_words") val netUsageWords: Int,
        val trx: String
    )
}