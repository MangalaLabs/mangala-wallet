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

import okio.Buffer

class ByteArrayBuffer(
    private val buffer: Buffer = Buffer()
) {
    fun append(b: Byte): ByteArrayBuffer = apply {
        buffer.writeByte(b.toInt())
    }

    fun append(b: UByte): ByteArrayBuffer = apply {
        buffer.writeByte(b.toInt())
    }

    fun append(v: Long): ByteArrayBuffer = apply {
        buffer.writeLongLe(v) // Write long value as little endian
    }

    fun append(v: UInt): ByteArrayBuffer = apply {
        buffer.writeIntLe(v.toInt()) // Write uint value as little endian
    }

    fun append(v: ULong): ByteArrayBuffer = apply {
        buffer.writeLongLe(v.toLong()) // Write ulong value as little endian
    }

    fun append(v: Int): ByteArrayBuffer = apply {
        buffer.writeIntLe(v) // Write int value as little endian
    }

    fun append(v: Short): ByteArrayBuffer = apply {
        buffer.writeShortLe(v.toInt()) // Write short value as little endian
    }

    fun append(v: UShort): ByteArrayBuffer = apply {
        buffer.writeShortLe(v.toInt()) // Write ushort value as little endian
    }

//    fun append(v: Float): ByteArrayBuffer = apply {
//        buffer.writeIntLe(v.floatToIntBits(v)) // Write float as int bits in little endian
//    }

    fun append(b: ByteArray, off: Int = 0, len: Int = b.size): ByteArrayBuffer = apply {
        if (off < 0 || off > b.size || len < 0 || off + len < 0 || off + len > b.size) {
            throw IndexOutOfBoundsException("off: $off len: $len b.length: ${b.size}")
        }
        buffer.write(b, off, len)
    }

    fun toByteArray(): ByteArray {
        return buffer.readByteArray()
    }

    fun length(): Long = buffer.size
}