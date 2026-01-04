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
package com.memtrip.eos.abi.reader

import com.memtrip.eos.abi.reader.bytereader.ByteArrayReaderBuffer
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey
import com.memtrip.eos.core.crypto.KeyType

interface ByteReader {
    fun load(hex: String)
    fun load(byteArray: ByteArray)
    fun getAccountName(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): String
    fun getName(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): String
    fun getKeyType(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): KeyType
    fun getPublicKey(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): EosPublicKey
    fun getAsset(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): String
    fun getString(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): String
    fun getByte(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): Int
    fun getUByte(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): Int
    fun getTimestampMs(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): Long
    fun getBlockNum(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): Int
    fun getBlockPrefix(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): Long
    fun getVariableUInt(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): Long
    fun getULong(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): ULong
    fun getStringCollection(
        byteReader: ByteReader,
        buffer: ByteArrayReaderBuffer? = null
    ): List<String>
    fun getUInt(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): UInt

    fun getChecksum256(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): String
    fun getBytesInt(
        byteReader: ByteReader,
        bytesLength: Int,
        buffer: ByteArrayReaderBuffer? = null
    ): Int

    fun getBytes(
        byteReader: ByteReader,
        bytesLength: Int,
        buffer: ByteArrayReaderBuffer? = null
    ): ByteArray

    fun getBytes(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): ByteArray

    fun getBool(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): Boolean

    fun getUShort(byteReader: ByteReader, buffer: ByteArrayReaderBuffer? = null): UShort
}