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
import kotlin.math.min

class CurrencySymbolWriter {

    fun put(precision: Int, symbol: CharSequence, writer: ByteWriter) {

        var result: Long = 0

        // No check for empty symbol string to allow "0," as a valid symbol

        for (index in 0 until symbol.length) {
            val value = symbol[index].toLong()

            // check range 'A' to 'Z'
            if (value < 65 || value > 90) {
                throw IllegalArgumentException("invalid currency symbol string: $symbol")
            }

            result = result or (value shl 8 * (1 + index))
        }

        result = result or precision.toLong()

        writer.putLong(result)
    }

    fun put(symbol: CharSequence, writer: ByteWriter) {

        val length = min(symbol.length, MAX_SYMBOL_LENGTH)
        val bytes = ByteArray(8) { 0 }  // UInt64

        for (index in 0 until length) {
            val value = symbol[index].toLong()

            // check range 'A' to 'Z'
            if (value < 65 || value > 90) {
                throw IllegalArgumentException("invalid currency symbol string: $symbol")
            }

            bytes[index] = value.toByte()
        }

        writer.putBytes(bytes)
    }

    companion object {
        private const val MAX_SYMBOL_LENGTH = 7
    }
}