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
import com.memtrip.eos.core.crypto.EosPublicKey
import com.memtrip.eos.core.crypto.EosSignature
import com.memtrip.eos.core.hex.DefaultHexWriter
import com.memtrip.eos.core.hex.HexWriter

class DefaultByteWriter : ByteWriter {

    private val nameWriter: NameWriter = NameWriter()
    private val accountNameWriter: AccountNameWriter =
        AccountNameWriter()
    private val publicKeyWriter: PublicKeyWriter = PublicKeyWriter()
    private val hexWriter: HexWriter = DefaultHexWriter()
    private val assetWriter: AssetWriter =
        AssetWriter()
    private val chainIdWriter: ChainIdWriter = ChainIdWriter()
    private val hexCollectionWriter: HexCollectionWriter = HexCollectionWriter()
    private val currencySymbolWriter: CurrencySymbolWriter = CurrencySymbolWriter()
    private val signatureWriter: SignatureWriter = SignatureWriter()

    private val buffer = ByteArrayBuffer()

    override fun putName(value: String) {
        nameWriter.put(value, this)
    }

    override fun putAccountName(value: String) {
        accountNameWriter.put(value, this)
    }

    override fun putBlockNum(value: Int) {
        putShort((value and 0xFFFF).toShort())
    }

    override fun putBlockPrefix(value: Long) {
        putInt((value and -0x1).toInt())
    }

    override fun putPublicKey(value: EosPublicKey) {
        publicKeyWriter.put(value, this)
    }

    override fun putAsset(value: String) {
        assetWriter.put(value, this)
    }

    override fun putSymbol(value: String) {
        val parts = value.split(",")
        val precision = parts.getOrNull(0)?.toIntOrNull() ?: throw IllegalArgumentException("Invalid symbol format")
        val symbol = parts.getOrNull(1) ?: if (value == "0,") "" else throw IllegalArgumentException("Invalid symbol format")

        currencySymbolWriter.put(precision, symbol, this)
    }

    override fun putCurrencySymbol(value: String) {
        currencySymbolWriter.put(value, this)
    }

    override fun putChainId(value: String) {
        chainIdWriter.put(value, this)
    }

    override fun putData(value: String) {
        val dataAsBytes = hexWriter.hexToBytes(value)
        putVariableUInt(dataAsBytes.size.toLong())
        putBytes(dataAsBytes)
    }

    override fun putTimestampMs(value: Long) {
        putInt((value / 1000).toInt())
    }

    override fun putShort(value: Short) {
        buffer.append(value)
    }

    override fun putInt(value: Int) {
        buffer.append(value)
    }

    override fun putVariableUInt(value: Long) {
        var v: Long = value
        while (v >= 0x80) {
            val b = ((v and 0x7f) or 0x80).toByte()
            buffer.append(b)
            v = v ushr 7
        }
        buffer.append(v.toByte())
    }

    override fun putVariableInt(value: Long) {
        // Zigzag encoding
        val encoded = (value shl 1) xor (value shr 31)
        putVariableUInt(encoded)
    }

    override fun putLong(value: Long) {
        buffer.append(value)
    }

    override fun putSignature(value: EosSignature) {
        signatureWriter.put(value, this)
    }

    override fun putUInt(value: UInt) {
        buffer.append(value)
    }

    override fun putULong(value: ULong) {
        buffer.append(value)
    }

    //    override fun putFloat(value: Float) {
//        buffer.append(value)
//    }

    override fun putByte(value: Byte) {
        buffer.append(value)
    }

    override fun putUByte(value: UByte) {
        buffer.append(value)
    }

    override fun putUShort(value: UShort) {
        buffer.append(value)
    }

    override fun putBytes(value: ByteArray) {
        buffer.append(value)
    }

    override fun putString(value: String) {
        val bytes = value.encodeToByteArray()
        putVariableUInt(bytes.size.toLong())
        buffer.append(value.encodeToByteArray())
    }

    override fun putStringCollection(stringList: List<String>) {
        putVariableUInt(stringList.size.toLong())

        if (stringList.isNotEmpty()) {
            for (string in stringList) {
                putString(string)
            }
        }
    }

    override fun putHexCollection(stringList: List<String>) {
        hexCollectionWriter.put(stringList, this)
    }

    override fun putAccountNameCollection(accountNameList: List<String>) {
        putVariableUInt(accountNameList.size.toLong())

        if (accountNameList.isNotEmpty()) {
            for (accountName in accountNameList) {
                putAccountName(accountName)
            }
        }
    }

    override fun toBytes(): ByteArray = buffer.toByteArray()

    override fun length(): Long = buffer.length()
}