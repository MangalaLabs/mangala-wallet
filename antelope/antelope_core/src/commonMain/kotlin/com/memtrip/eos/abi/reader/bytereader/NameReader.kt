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
package com.memtrip.eos.abi.reader.bytereader

import com.memtrip.eos.abi.reader.ByteReader
import com.memtrip.eos.abi.writer.bytewriter.NameWriter

class NameReader {

    fun get(buffer: ByteArrayReaderBuffer, reader: ByteReader): String {
        val value = buffer.readLong()

        val chars = CharArray(NameWriter.MAX_NAME_IDX + 1)

        for (i in 0..NameWriter.MAX_NAME_IDX) {
            val c = if (i == NameWriter.MAX_NAME_IDX) {
                value and 0x0F
            } else {
                (value ushr (64 - 5 * (i + 1))) and 0x1F
            }

            chars[i] = symbolToChar(c.toByte())
        }

        // Convert char array to string and remove trailing null characters
        return chars.concatToString().trimEnd('.')
    }

    private fun symbolToChar(symbol: Byte): Char {
        return when (symbol) {
            in 6..31 -> (symbol - 6 + 'a'.code).toChar()
            in 1..5 -> (symbol - 1 + '1'.code).toChar()
            else -> '.'
        }
    }
}