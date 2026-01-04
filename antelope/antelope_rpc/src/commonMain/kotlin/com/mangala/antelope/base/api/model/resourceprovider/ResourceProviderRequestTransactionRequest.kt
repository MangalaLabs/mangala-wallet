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

package com.mangala.antelope.base.api.model.resourceprovider


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResourceProviderRequestTransactionRequest(
    @SerialName("packedTransaction")
    val packedTransaction: PackedTransaction,
    @SerialName("signer")
    val signer: Signer
) {
    @Serializable
    data class PackedTransaction(
        @SerialName("compression")
        val compression: Int,
        @SerialName("packed_context_free_data")
        val packedContextFreeData: String,
        @SerialName("packed_trx")
        val packedTrx: String,
        @SerialName("signatures")
        val signatures: List<String?>
    )

    @Serializable
    data class Signer(
        @SerialName("actor")
        val actor: String,
        @SerialName("permission")
        val permission: String
    )
}