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
data class BaseGetTableRowsRequest(
    @SerialName("json") val json: Boolean,
    @SerialName("code") val code: String,
    @SerialName("table") val table: String,
    @SerialName("scope") val scope: String,
    @SerialName("lower_bound") val lowerBound: String,
    @SerialName("upper_bound") val upperBound: String,
    @SerialName("index_position") val indexPosition: Int,
    @SerialName("key_type") val keyType: String,
    @SerialName("limit") val limit: Int,
    @SerialName("reverse") val reverse: Boolean,
    @SerialName("show_payer") val showPayer: Boolean,
)