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
data class GetProducerScheduleResponse(
    val active: ProducerSchedule,
    val pending: ProducerSchedule,
    val proposed: ProducerSchedule
) {
    @Serializable
    data class ProducerSchedule(
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
}
