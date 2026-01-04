/*
 * Copyright 2013-present memtrip LTD.
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

// ------------------------------------------------------------------
// MODIFICATION NOTICE:
// Modified by Mangala Wallet
// Description: Adapted for Kotlin Multiplatform compatibility.
// ------------------------------------------------------------------
package com.memtrip.eos.abi.writer.bytewriter

import com.memtrip.eos.abi.writer.ByteWriter

class AssetWriter(
    private val currencySymbolWriter: CurrencySymbolWriter = CurrencySymbolWriter()
) {

    fun put(asset: String, writer: ByteWriter) {

        val value = asset.trim()

        val regex = Regex("^([0-9]+)\\.?([0-9]*)([ ][a-zA-Z0-9]{1,7})?$")
        val matchResult = regex.find(value)

        if (matchResult != null) {
            val beforeDotVal = matchResult.groupValues[1]
            val afterDotVal = matchResult.groupValues[2]

            val symbol = if (matchResult.groupValues[3].isEmpty()) null else matchResult.groupValues[3].trim()

            val amount = (beforeDotVal + afterDotVal).toLong()

            writer.putLong(amount)

            if (symbol != null) {
                currencySymbolWriter.put(afterDotVal.length, symbol, writer)
            } else {
                writer.putLong(0)
            }
        } else {
            throw IllegalArgumentException("invalid asset format")
        }
    }
}