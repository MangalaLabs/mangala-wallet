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

import com.memtrip.eos.abi.writer.bytewriter.ByteArrayBuffer
import okio.Buffer

class ByteArrayReaderBuffer(
    private val buffer: Buffer = Buffer()
) {
    fun readByte(): Byte = buffer.readByte()

    fun readUByte(): UByte = buffer.readByte().toUByte()

    fun readLong(): Long = buffer.readLongLe() // Read long value as little endian

    fun readInt(): Int = buffer.readIntLe() // Read int value as little endian

    fun readShort(): Short = buffer.readShortLe() // Read short value as little endian

    fun readArray(off: Int = 0, len: Int): ByteArray {
        val resultByteArray = ByteArray(len)
        buffer.read(resultByteArray, off, len)

        return resultByteArray
    }
}