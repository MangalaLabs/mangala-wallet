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

package com.mangala.antelope.base.domain.model

import com.mangala.antelope.base.api.model.ChainError
import kotlinx.serialization.Serializable

sealed interface ResourceProviderResponse {
    data object ResourceNotRequired : ResourceProviderResponse
    data class ResourcePaidForFree(val newTransaction: Transaction) : ResourceProviderResponse
    data class InvalidRequest(val invalidRequestData: InvalidRequestData) : ResourceProviderResponse
    data class FeeRequired(
        val feeBreakdown: FeeBreakdown,
        val fee: String,
        val newTransaction: Transaction
    ) : ResourceProviderResponse
}

@Serializable
data class Transaction(
    val expiration: String,
    val refBlockNum: Long,
    val refBlockPrefix: Long,
    val maxNetUsageWords: Long,
    val maxCpuUsageMs: Long,
    val delaySecs: Long,
    val actions: List<Action>,
    val signatures: List<String>
) {
    @Serializable
    data class Action(
        val account: String,
        val name: String,
        val authorization: List<Authorization>,
        val data: String
    )

    @Serializable
    data class Authorization(
        val actor: String,
        val permission: String
    )
}

sealed interface InvalidRequestData {
    data class NodeError(val chainError: ChainError) : InvalidRequestData
    data class ResourceProviderError(val error: String) : InvalidRequestData
}

data class FeeBreakdown(
    val cpu: String,
    val net: String,
    val ram: String
)