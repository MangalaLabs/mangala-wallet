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

import com.mangala.antelope.base.api.model.GetCurrencyStatsResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class GetCurrencyStatsResponseMapperTest {

    @Test
    fun `Given response with data, when map to GetCurrencyStatsResponse, then return GetCurrencyStatsResponse`() {
        val jsonInput = """
        {
            "JUNGLE": {
                "supply": "10000000000.0000 JUNGLE",
                "max_supply": "100000000000.0000 JUNGLE",
                "issuer": "eosio"
            }
        }
        """.trimIndent()

        val json = Json { ignoreUnknownKeys = true }
        val parsedData = json.decodeFromString<JsonObject>(jsonInput)

        assertEquals(
            GetCurrencyStatsResponse(
                supply = "10000000000.0000 JUNGLE",
                maxSupply = "100000000000.0000 JUNGLE",
                issuer = "eosio"
            ),
            parsedData.toGetCurrencyStatsResponse(json)
        )
    }

    @Test
    fun `Given response with empty data, when map to GetCurrencyStatsResponse, then return GetCurrencyStatsResponse`() {
        val jsonInput = "{}".trimIndent()

        val json = Json { ignoreUnknownKeys = true }
        val parsedData = json.decodeFromString<JsonObject>(jsonInput)

        assertEquals(
            null,
            parsedData.toGetCurrencyStatsResponse(json)
        )
    }
}