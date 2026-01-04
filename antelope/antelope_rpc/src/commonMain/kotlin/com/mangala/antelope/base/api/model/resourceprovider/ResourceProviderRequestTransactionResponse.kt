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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = RequestTransactionResponseSerializer::class)
sealed class ResourceProviderRequestTransactionResponse {
    abstract val code: Int?
    abstract val message: String?

    @Serializable
    data class ResourceNotRequired(
        @SerialName("code")
        override val code: Int? = 0,
        @SerialName("data")
        val `data`: Data? = Data(),
        @SerialName("message")
        override val message: String? = ""
    ): ResourceProviderRequestTransactionResponse() {
        @Serializable
        class Data
    }

    @Serializable
    data class ResourcePaidForFree(
        @SerialName("code")
        override val code: Int? = 0,
        @SerialName("data")
        val `data`: Data? = Data(),
        @SerialName("message")
        override val message: String? = ""
    ): ResourceProviderRequestTransactionResponse() {
        @Serializable
        data class Data(
            @SerialName("request")
            val requestJsonElement: List<JsonElement>? = listOf(),
            @SerialName("signatures")
            val signatures: List<String?>? = listOf(),
            @SerialName("version")
            val version: String? = null
        ) {
//            @Transient
//            val request = requestJsonElement?.map { it.toRequestItemOrNull() }
        }
    }

    @Serializable
    data class InvalidRequest(
        @SerialName("code")
        override val code: Int? = 0,
        @SerialName("data")
        val `data`: Data? = Data(),
        @SerialName("message")
        override val message: String? = ""
    ): ResourceProviderRequestTransactionResponse() {
        @Serializable
        data class Data(
            @SerialName("error")
            val errorJsonElement: JsonElement? = null,
            @SerialName("url")
            val url: String? = null
        )
    }

    @Serializable
    data class FeeRequired(
        @SerialName("code")
        override val code: Int? = 0,
        @SerialName("data")
        val `data`: Data? = Data(),
        @SerialName("message")
        override val message: String? = ""
    ): ResourceProviderRequestTransactionResponse() {
        @Serializable
        data class Data(
            @SerialName("costs")
            val costs: Costs? = Costs(),
            @SerialName("fee")
            val fee: String? = "",
            @SerialName("request")
            val requestJsonElement: List<JsonElement?>? = listOf(),
            @SerialName("signatures")
            val signatures: List<String?>? = listOf(),
            @SerialName("version")
            val version: String? = null
        ) {
//            @Transient
//            val request = requestJsonElement?.map { it.toRequestItemOrNull() }

            @Serializable
            data class Costs(
                @SerialName("cpu")
                val cpu: String? = "",
                @SerialName("net")
                val net: String? = "",
                @SerialName("ram")
                val ram: String? = ""
            )
        }
    }
}

object RequestTransactionResponseSerializer: JsonContentPolymorphicSerializer<ResourceProviderRequestTransactionResponse>(ResourceProviderRequestTransactionResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ResourceProviderRequestTransactionResponse> {
        return when (element.jsonObject["code"]?.jsonPrimitive?.contentOrNull?.toInt()) {
            200 -> ResourceProviderRequestTransactionResponse.ResourcePaidForFree.serializer()
            400 -> {
                if (element.jsonObject["message"]?.jsonPrimitive?.contentOrNull == "Network resources not required by this account.") {
                    ResourceProviderRequestTransactionResponse.ResourceNotRequired.serializer()
                } else {
                    ResourceProviderRequestTransactionResponse.InvalidRequest.serializer()
                }
            }
            402 -> ResourceProviderRequestTransactionResponse.FeeRequired.serializer()
            else -> throw Exception("Unknown Item type")
        }
    }
}