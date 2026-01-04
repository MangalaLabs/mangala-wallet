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
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = RequestItemSerializer::class)
sealed class RequestItem {
    @Serializable
    @SerialName("transaction")
    data class Transaction(
        @SerialName("actions")
        val actions: List<Action?>? = listOf(),
        @SerialName("context_free_actions")
        val contextFreeActions: List<Action?>? = listOf(),
        @SerialName("delay_sec")
        val delaySec: Long? = 0,
        @SerialName("expiration")
        val expiration: String? = "",
        @SerialName("max_cpu_usage_ms")
        val maxCpuUsageMs: Long? = 0,
        @SerialName("max_net_usage_words")
        val maxNetUsageWords: Long? = 0,
        @SerialName("ref_block_num")
        val refBlockNum: Long? = 0,
        @SerialName("ref_block_prefix")
        val refBlockPrefix: Long? = 0,
        @SerialName("transaction_extensions")
        val transactionExtensions: List<Action?>? = listOf()
    ): RequestItem() {
        @Serializable
        data class Action(
            @SerialName("account")
            val account: String? = "",
            @SerialName("authorization")
            val authorization: List<Authorization?>? = listOf(),
            @SerialName("data")
            val `data`: String? = "",
            @SerialName("name")
            val name: String? = ""
        ) {
            @Serializable
            data class Authorization(
                @SerialName("actor")
                val actor: String? = "",
                @SerialName("permission")
                val permission: String? = ""
            )
        }
    }

    @Serializable
    @SerialName("transaction_data")
    data class TransactionString(val value: String) : RequestItem()
}

object RequestItemSerializer : JsonContentPolymorphicSerializer<RequestItem>(RequestItem::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<RequestItem> {
        return if (element is JsonPrimitive && element.isString) {
            RequestItem.TransactionString.serializer()
        } else if (element is JsonObject) {
            RequestItem.Transaction.serializer()
        } else {
            throw SerializationException("Unknown item type")
        }
    }

    override val descriptor: SerialDescriptor
        get() = super.descriptor
}

fun JsonElement?.toRequestItemOrNull(json: Json): RequestItem? {
    return when {
        this is JsonPrimitive && this.isString -> return RequestItem.TransactionString(this.jsonPrimitive.content)
        this is JsonObject -> return json.decodeFromJsonElement(
            RequestItem.Transaction.serializer(),
            this
        )
        else -> null
    }
}
