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

import com.mangala.antelope.base.api.model.resourceprovider.ResourceProviderRequestTransactionResponse
import com.mangala.antelope.base.api.model.resourceprovider.toRequestItemOrNull
import com.mangala.wallet.common.test.utils.SharedFileReader
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertNotNull

class GetAccountResponseTest {

    @Test
    fun `Given response with refund request, when parse GetAccountResponse, then return parsed response`() {
        val jsonInstance = Json
        val json = SharedFileReader().loadJsonFile("get-account-response.json")!!

        val response: GetAccountResponse = jsonInstance.decodeFromString(json)

        assertNotNull(response.refundRequest)
    }

    @Test
    fun `Given response with session key, when parse GetAccountResponse, then return parsed response`() {
        val jsonInstance = Json
        val json = SharedFileReader().loadJsonFile("get-account-response-2.json")!!

        val response: GetAccountResponse = jsonInstance.decodeFromString(json)

        assertNotNull(response.permissions?.find { it.permName == "agc.shipload" })
    }
}