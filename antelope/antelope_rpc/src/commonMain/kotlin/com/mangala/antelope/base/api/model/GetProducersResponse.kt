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
data class GetProducersResponse(
    val rows: List<Producer>?,
    @SerialName("total_producer_vote_weight") val totalProducerVoteWeight: String?,
    val more: String?
) {
    @Serializable
    data class Producer(
        val owner: String?,
        @SerialName("producer_authority") val producerAuthority: List<ProducerAuthority>?,
        val url: String?,
        @SerialName("total_votes") val totalVotes: String?,
        @SerialName("producer_key") val producerKey: String?
    )

    @Serializable
    data class ProducerAuthority(
        val threshold: Int?,
        val keys: List<ProducerKey>?
    )

    @Serializable
    data class ProducerKey(
        val key: String?,
        val weight: Int?
    )
}
