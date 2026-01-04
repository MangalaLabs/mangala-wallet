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
data class PowerupState(
    @SerialName("version")
    val version: Int? = null,
    @SerialName("net")
    val net: ResourceUsage? = null,
    @SerialName("cpu")
    val cpu: ResourceUsage? = null,
    @SerialName("powerup_days")
    val powerupDays: Int? = null,
    @SerialName("min_powerup_fee")
    val minPowerupFee: String? = null
)

@Serializable
data class ResourceUsage(
    @SerialName("version")
    val version: Int? = null,
    @SerialName("weight")
    val weight: String? = null,
    @SerialName("weight_ratio")
    val weightRatio: String? = null,
    @SerialName("assumed_stake_weight")
    val assumedStakeWeight: String? = null,
    @SerialName("initial_weight_ratio")
    val initialWeightRatio: String? = null,
    @SerialName("target_weight_ratio")
    val targetWeightRatio: String? = null,
    @SerialName("initial_timestamp")
    val initialTimestamp: String? = null,
    @SerialName("target_timestamp")
    val targetTimestamp: String? = null,
    @SerialName("exponent")
    val exponent: String? = null,
    @SerialName("decay_secs")
    val decaySecs: Long? = null,
    @SerialName("min_price")
    val minPrice: String? = null,
    @SerialName("max_price")
    val maxPrice: String? = null,
    @SerialName("utilization")
    val utilization: String? = null,
    @SerialName("adjusted_utilization")
    val adjustedUtilization: String? = null,
    @SerialName("utilization_timestamp")
    val utilizationTimestamp: String? = null
)