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

fun ChainException.tryMapToNotEnoughResourceException(): NotEnoughResourceException? {
    val errorName = chainError.error.name
    val firstErrorDetails = chainError.error.details.firstOrNull()

    when (errorName) {
        "tx_cpu_usage_exceeded" -> {
            val regex = Regex("""\((\d+)\s+us\).*?\((\d+)\s+us\).*?(\d+)\s*us""")
            val matches = regex.find(firstErrorDetails?.message.orEmpty())

            if (matches != null) {
                val (billedCpuTime, maximumBillableCpuTime, accountCpuLimit) = matches.destructured
                return NotEnoughResourceException.NotEnoughCpuException(
                    billedCpuTime.toLongOrNull(),
                    maximumBillableCpuTime.toLongOrNull(),
                    accountCpuLimit.toLongOrNull(),
                    chainError
                )
            } else {
                return null
            }
        }

        "tx_net_usage_exceeded" -> {
            val regex = Regex("""(\d+)\s*>\s*(\d+)""")
            val matches = regex.find(firstErrorDetails?.message.orEmpty())

            if (matches != null) {
                val (netMicrosecondsNeededStr, netMicrosecondsAvailableStr) = matches.destructured
                val netMicrosecondsNeeded = netMicrosecondsNeededStr.toLong()
                val netMicrosecondsAvailable = netMicrosecondsAvailableStr.toLong()

                return NotEnoughResourceException.NotEnoughNetException(
                    microsecondsNeeded = netMicrosecondsNeeded,
                    microsecondsAvailable = netMicrosecondsAvailable,
                    chainError
                )
            } else {
                return null
            }
        }

        "ram_usage_exceeded" -> {
            val regex = Regex("""needs (\d+) bytes has (\d+) bytes""")
            val matches = regex.find(firstErrorDetails?.message.orEmpty())

            if (matches != null) {
                val (neededBytesStr, hasBytesStr) = matches.destructured
                val neededBytes = neededBytesStr.toLong()
                val hasBytes = hasBytesStr.toLong()

                return NotEnoughResourceException.NotEnoughRamException(
                    neededBytes,
                    hasBytes,
                    chainError
                )
            } else {
                return null
            }
        }

        else -> return null
    }
}