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

package com.mangala.antelope.base.domain.repository.mapper

import com.mangala.antelope.base.api.model.ChainError
import com.mangala.antelope.base.api.model.resourceprovider.ErrorItem
import com.mangala.antelope.base.api.model.resourceprovider.RequestItem
import com.mangala.antelope.base.api.model.resourceprovider.ResourceProviderRequestTransactionResponse
import com.mangala.antelope.base.api.model.resourceprovider.toErrorItemOrNull
import com.mangala.antelope.base.api.model.resourceprovider.toRequestItemOrNull
import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.antelope.base.domain.model.InvalidRequestData
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.domain.model.Transaction
import kotlinx.serialization.json.Json

fun ResourceProviderRequestTransactionResponse.toResourceProviderResponse(json: Json): ResourceProviderResponse {
    return when (this) {
        is ResourceProviderRequestTransactionResponse.ResourceNotRequired -> ResourceProviderResponse.ResourceNotRequired
        is ResourceProviderRequestTransactionResponse.FeeRequired -> {
            val newTransaction =
                data?.requestJsonElement?.map { it.toRequestItemOrNull(json) }
                    ?.firstOrNull { it is RequestItem.Transaction } as? RequestItem.Transaction

            ResourceProviderResponse.FeeRequired(
                feeBreakdown = FeeBreakdown(
                    cpu = data?.costs?.cpu.orEmpty(),
                    net = data?.costs?.net.orEmpty(),
                    ram = data?.costs?.ram.orEmpty()
                ),
                fee = data?.fee.orEmpty(),
                newTransaction = newTransaction.mapToTransaction(data?.signatures)
            )
        }

        is ResourceProviderRequestTransactionResponse.ResourcePaidForFree -> {
            val newTransaction =
                data?.requestJsonElement?.map { it.toRequestItemOrNull(json) }
                    ?.firstOrNull { it is RequestItem.Transaction } as? RequestItem.Transaction

            ResourceProviderResponse.ResourcePaidForFree(
                newTransaction = newTransaction.mapToTransaction(data?.signatures)
            )
        }

        is ResourceProviderRequestTransactionResponse.InvalidRequest -> {
            val errorJson = data?.errorJsonElement?.toErrorItemOrNull(json)

            ResourceProviderResponse.InvalidRequest(
                invalidRequestData = when (errorJson) {
                    is ErrorItem.NodeError -> {
                        val responseJson = errorJson.response?.json

                        InvalidRequestData.NodeError(
                            ChainError(
                                code = responseJson?.code?.toLong() ?: 0,
                                message = responseJson?.message.orEmpty(),
                                error = com.mangala.antelope.base.api.model.Error(
                                    responseJson?.error?.code?.toLong() ?: 0L,
                                    responseJson?.error?.name.orEmpty(),
                                    responseJson?.error?.what.orEmpty(),
                                    responseJson?.error?.details?.map { detail ->
                                        com.mangala.antelope.base.api.model.Details(
                                            message = detail?.message.orEmpty(),
                                            method = detail?.method.orEmpty()
                                        )
                                    } ?: emptyList()
                                )
                            )
                        )
                    }

                    is ErrorItem.ProviderError -> {
                        InvalidRequestData.ResourceProviderError(errorJson.error)
                    }

                    else -> InvalidRequestData.ResourceProviderError("Unknown error")
                }
            )
        }
    }
}

fun RequestItem.Transaction?.mapToTransaction(signatures: List<String?>?) = Transaction(
    expiration = this?.expiration.orEmpty(),
    refBlockNum = this?.refBlockNum ?: 0,
    refBlockPrefix = this?.refBlockPrefix ?: 0,
    maxNetUsageWords = this?.maxNetUsageWords ?: 0,
    maxCpuUsageMs = this?.maxCpuUsageMs ?: 0,
    delaySecs = this?.delaySec ?: 0,
    actions = this?.actions?.map { action ->
        Transaction.Action(
            account = action?.account.orEmpty(),
            name = action?.name.orEmpty(),
            authorization = action?.authorization?.map { authorization ->
                Transaction.Authorization(
                    actor = authorization?.actor.orEmpty(),
                    permission = authorization?.permission.orEmpty()
                )
            } ?: emptyList(),
            data = action?.data.orEmpty()
        )
    } ?: emptyList(),
    signatures = signatures?.mapNotNull { it }.orEmpty()
)