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

import com.mangala.antelope.base.domain.model.rpcerror.NotEnoughResourceException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.mangala.antelope.base.api.model.Error as AntelopeError

class ChainExceptionMapperTest {

    @Test
    fun `Given chain exception with tx_cpu_usage_exceeded error, when try map to not enough resource exception, then return not enough cpu exception`() {
        val chainException = ChainException(
            ChainError(
                code = 500,
                message = "Internal Service Error",
                AntelopeError(
                    3080004,
                    "tx_cpu_usage_exceeded",
                    "Transaction exceeded the current CPU usage limit imposed on the transaction",
                    details = listOf(
                        Details(
                            "billed CPU time (342 us) is greater than the maximum billable CPU time for the transaction (0 us) reached account cpu limit 10us",
                            "validate_account_cpu_usage"
                        )
                    )
                )
            )
        )

        val result = chainException.tryMapToNotEnoughResourceException()

        assertTrue(result is NotEnoughResourceException.NotEnoughCpuException)
        assertEquals(10, result.accountCpuLimit ?: 0)
        assertEquals(0, result.maximumBillableCpuMicroseconds ?: 0)
        assertEquals(342, result.billedCpuMicroseconds ?: 0)
    }

    @Test
    fun `Given chain exception with ram_usage_exceeded error, when try map to not enough resource exception, then return not enough ram exception`() {
        val chainException = ChainException(
            ChainError(
                code = 500,
                message = "Internal Service Error",
                AntelopeError(
                    3080001,
                    "ram_usage_exceeded",
                    "Account using more than allotted RAM usage",
                    details = listOf(
                        Details(
                            "account abc.gm has insufficient ram; needs 6022 bytes has 5794 bytes",
                            "verify_account_ram_usage"
                        )
                    )
                )
            )
        )

        val result = chainException.tryMapToNotEnoughResourceException()

        assertTrue(result is NotEnoughResourceException.NotEnoughRamException)
        assertEquals(6022, result.neededBytes)
        assertEquals(5794, result.hasBytes)
        assertEquals(228, result.extraBytesNeeded)
    }

    @Test
    fun `Given chain exception with tx_net_usage_exceeded error, when try map to not enough resource exception, then return not enough net exception`() {
        val chainException = ChainException(
            ChainError(
                code = 500,
                message = "Internal Service Error",
                AntelopeError(
                    3080002,
                    "tx_net_usage_exceeded",
                    "Transaction exceeded the current network usage limit imposed on the transaction",
                    details = listOf(
                        Details(
                            "transaction net usage is too high: 192 > 0",
                            "check_net_usage"
                        )
                    )
                )
            )
        )

        val result = chainException.tryMapToNotEnoughResourceException()

        assertTrue(result is NotEnoughResourceException.NotEnoughNetException)
        assertEquals(192, result.microsecondsNeeded)
        assertEquals(0, result.microsecondsAvailable)
        assertEquals(192, result.extraMicrosecondsNeeded)
    }
}