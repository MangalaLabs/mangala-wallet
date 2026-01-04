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

import com.mangala.wallet.common.test.utils.SharedFileReader
import kotlinx.serialization.json.Json
import org.junit.Test

class ResourceProviderRequestTransactionResponseTest {
    @Test
    fun `Given resource not required JSON response, when deserialized, then should return ResourceNotRequired object`() {
        val json = """
            {"code":400,"message":"Network resources not required by this account.","data":{}}
        """.trimIndent()

        val response: ResourceProviderRequestTransactionResponse = Json.decodeFromString(json)

        assert(response is ResourceProviderRequestTransactionResponse.ResourceNotRequired)
    }

    @Test
    fun `Given resource paid for free JSON response, when deserialized, then should return ResourcePaidForFree object`() {
        val jsonInstance = Json
        val json = SharedFileReader().loadJsonFile("resource-paid-for-free.json")!!

        val response: ResourceProviderRequestTransactionResponse = jsonInstance.decodeFromString(json)

        assert(response is ResourceProviderRequestTransactionResponse.ResourcePaidForFree)

        val castedResponse = response as ResourceProviderRequestTransactionResponse.ResourcePaidForFree
        castedResponse.data?.requestJsonElement?.map { it.toRequestItemOrNull(jsonInstance) }
    }

    @Test
    fun `Given invalid request JSON response, when deserialized, then should return InvalidRequest object`() {
        val jsonInstance = Json
        val json = SharedFileReader().loadJsonFile("resource-invalid-request.json")!!

        val response: ResourceProviderRequestTransactionResponse = Json.decodeFromString(json)

        assert(response is ResourceProviderRequestTransactionResponse.InvalidRequest)
        val castedResponse = response as ResourceProviderRequestTransactionResponse.InvalidRequest
        val castedData = castedResponse.data?.errorJsonElement?.toErrorItemOrNull(jsonInstance)
        assert(castedData is ErrorItem.NodeError)
    }

    @Test
    fun `Given resource provider error JSON response, when deserialized, then should return InvalidRequest object`() {
        val jsonInstance = Json
        val json = SharedFileReader().loadJsonFile("resource-provider-error.json")!!

        val response: ResourceProviderRequestTransactionResponse = Json.decodeFromString(json)

        assert(response is ResourceProviderRequestTransactionResponse.InvalidRequest)
        val castedResponse = response as ResourceProviderRequestTransactionResponse.InvalidRequest
        val castedData = castedResponse.data?.errorJsonElement?.toErrorItemOrNull(jsonInstance)
        assert(castedData is ErrorItem.ProviderError)
    }

    @Test
    fun `Given additional resource required JSON response, when deserialized, then should return FeeRequired object`() {
        val jsonInstance = Json
        val json = SharedFileReader().loadJsonFile("resource-additional-resource-required.json")!!

        val response: ResourceProviderRequestTransactionResponse = jsonInstance.decodeFromString(json)

        assert(response is ResourceProviderRequestTransactionResponse.FeeRequired)
        val castedResponse = response as ResourceProviderRequestTransactionResponse.FeeRequired
        castedResponse.data?.requestJsonElement?.map { it.toRequestItemOrNull(jsonInstance) }
    }
}