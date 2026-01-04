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

import com.mangala.wallet.utils.hexStringToByteArray
import com.mangala.wallet.utils.toInt
import com.memtrip.eos.abi.reader.ByteReader
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey
import com.memtrip.eos.core.crypto.KeyType
import okio.Buffer
import okio.internal.commonToUtf8String

class DefaultByteReader : ByteReader {

    private val nameReader: NameReader = NameReader()
    private val accountNameReader: AccountNameReader = AccountNameReader()
    private val assetReader: AssetReader = AssetReader()

    private var buffer: ByteArrayReaderBuffer? = null

    override fun load(hex: String) {
        val byteArray = hex.hexStringToByteArray()
        buffer = ByteArrayReaderBuffer(Buffer().write(byteArray))
    }

    override fun load(byteArray: ByteArray) {
        buffer = ByteArrayReaderBuffer(Buffer().write(byteArray))
    }

    override fun getAccountName(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): String {
        return accountNameReader.get(getBuffer(buffer), this)
    }

    override fun getName(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): String {
        return nameReader.get(getBuffer(buffer), this)
    }

    override fun getKeyType(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): KeyType {
        val keyTypeIndex = getByte(byteReader, buffer)

        return KeyType.fromIndex(keyTypeIndex)
    }

    override fun getPublicKey(
        byteReader: ByteReader,
        buffer: ByteArrayReaderBuffer?
    ): EosPublicKey {
        // https://github.com/wharfkit/antelope/blob/350e565e0e320b72cf437af4792efa49b3034bc6/src/chain/public-key.ts#L50
        val keyType = getKeyType(byteReader, buffer)
        val bytes = getBytes(byteReader, 33, buffer)

        return EosPublicKey(bytes, keyType)
    }

    override fun getAsset(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): String {
        return assetReader.get(getBuffer(buffer), this)
    }

    override fun getString(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): String {
        val size = getVariableUInt(byteReader, buffer)
        val stringByteArray = getBuffer(buffer).readArray(len = size.toInt())

        return stringByteArray.commonToUtf8String()
    }

    override fun getByte(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): Int {
        return getBuffer(buffer).readByte().toInt()
    }

    override fun getUByte(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): Int {
        return getBuffer(buffer).readUByte().toInt()
    }

    override fun getTimestampMs(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): Long {
        return getBuffer(buffer).readInt().toLong() * 1000
    }

    override fun getBlockNum(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): Int {
        return getBuffer(buffer).readShort().toInt()
    }

    override fun getBlockPrefix(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): Long {
        return getBuffer(buffer).readInt().toLong()
    }

    override fun getVariableUInt(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): Long {
        var result: Long = 0
        var shift = 0
        while (true) {
            val byte = getBuffer(buffer).readByte()
            val b = byte.toInt()
            result = result or ((b and 0x7F).toLong() shl shift)
            if ((b and 0x80) == 0) {
                return result
            }
            shift += 7
        }
    }

    override fun getULong(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): ULong {
        return getBuffer(buffer).readLong().toULong()
    }

    override fun getStringCollection(
        byteReader: ByteReader,
        buffer: ByteArrayReaderBuffer?
    ): List<String> {
        val size = getVariableUInt(byteReader, buffer)
        val stringList = mutableListOf<String>()

        for (i in 0 until size) {
            stringList.add(getString(byteReader, buffer))
        }

        return stringList
    }

    override fun getUInt(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): UInt {
        return getBuffer(buffer).readInt().toUInt()
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun getChecksum256(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): String {
        return getBuffer(buffer).readArray(len = 32).toHexString()
    }

    override fun getBytesInt(
        byteReader: ByteReader,
        bytesLength: Int,
        buffer: ByteArrayReaderBuffer?
    ): Int {
        return getBytes(byteReader, bytesLength, buffer).toInt()
    }

    override fun getBytes(
        byteReader: ByteReader,
        bytesLength: Int,
        buffer: ByteArrayReaderBuffer?
    ): ByteArray {
        return getBuffer(buffer).readArray(len = bytesLength)
    }

    override fun getBytes(
        byteReader: ByteReader,
        buffer: ByteArrayReaderBuffer?,
    ): ByteArray {
        val size = getVariableUInt(byteReader, buffer)

        return getBuffer(buffer).readArray(len = size.toInt())
    }

    override fun getBool(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): Boolean {
        return getBuffer(buffer).readByte().toInt() == 1
    }

    override fun getUShort(byteReader: ByteReader, buffer: ByteArrayReaderBuffer?): UShort {
        return getBuffer(buffer).readShort().toUShort()
    }

    private fun getBuffer(buffer: ByteArrayReaderBuffer?): ByteArrayReaderBuffer {
        return buffer ?: this.buffer
        ?: throw IllegalStateException("Call load() to load the data to buffer first")
    }
}