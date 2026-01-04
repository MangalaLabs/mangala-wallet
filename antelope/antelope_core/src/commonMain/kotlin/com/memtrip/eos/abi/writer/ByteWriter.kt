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
package com.memtrip.eos.abi.writer

import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey
import com.memtrip.eos.core.crypto.EosSignature

interface ByteWriter {
    fun putName(value: String)
    fun putAccountName(value: String)
    fun putBlockNum(value: Int)
    fun putBlockPrefix(value: Long)
    fun putPublicKey(value: EosPublicKey)
    fun putAsset(value: String)
    fun putSymbol(value: String)
    fun putCurrencySymbol(value: String)
    fun putChainId(value: String)
    fun putData(value: String)
    fun putTimestampMs(value: Long)
    fun putShort(value: Short)
    fun putInt(value: Int)
    fun putVariableUInt(value: Long)
    fun putVariableInt(value: Long)
    fun putLong(value: Long)
    fun putSignature(value: EosSignature)

    //    fun putFloat(value: Float)
    fun putByte(value: Byte)
    fun putUByte(value: UByte)
    fun putUShort(value: UShort)
    fun putUInt(value: UInt)
    fun putULong(value: ULong)
    fun putBytes(value: ByteArray)
    fun putString(value: String)
    fun putStringCollection(stringList: List<String>)
    fun putHexCollection(stringList: List<String>)
    fun putAccountNameCollection(accountNameList: List<String>)

    fun toBytes(): ByteArray
    fun length(): Long
}