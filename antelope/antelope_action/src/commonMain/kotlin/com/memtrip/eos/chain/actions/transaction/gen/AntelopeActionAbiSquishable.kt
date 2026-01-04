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

package com.memtrip.eos.chain.actions.transaction.gen

import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.memtrip.eos.abi.writer.ByteWriter
import com.memtrip.eos.abi.writer.Squishable
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopeActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes.Companion.EXTENDED_TYPE_DELIMITER
import com.memtrip.eos.core.crypto.EosPublicKey
import com.memtrip.eos.core.crypto.EosSignature

class AntelopeActionAbiSquishable internal constructor() : Squishable<List<AntelopeActionAbi>> {

    override fun squish(obj: List<AntelopeActionAbi>, writer: ByteWriter) {
        obj.forEachIndexed { index, abi ->
            if (abi.isPrimitive) {
                if (abi.variantTypeIndex != null) {
                    writer.putVariableUInt(abi.variantTypeIndex.toLong())
                }

                writer.writePrimitiveAbiField(abi)
                return@forEachIndexed
            }

            if (abi.isOptional) {
                writer.putByte(if (abi.isOptionalValueSet) 1 else 0)
            }

            // For extension types, we don't have to write any index, so can handle like a normal object

            if (abi.isArray) {
                // Array has higher precedent than an object, because we need to write the array size first
                writer.putVariableUInt(abi.elementsCount.toLong())

                // Since elements are flattened in depth-first manner, which is the order we need to write to the buffer
                // we can skip to next iteration to write the elements
                return@forEachIndexed
            } else {
                if (abi.variantTypeIndex != null) {
                    writer.putVariableUInt(abi.variantTypeIndex.toLong())
                }
                // In the case of an object, we only write its elements, so can skip and move to next iteration
                return@forEachIndexed
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun ByteWriter.writePrimitiveAbiField(abi: AntelopeActionAbi) {
        // https://kotlinlang.org/docs/numbers.html
        // https://kotlinlang.org/docs/unsigned-integer-types.html
        when (AntelopePrimitiveDataTypes.fromValue(abi.fieldType)) {
            AntelopePrimitiveDataTypes.INT8 -> abi.value.toByteOrNull()?.let { putByte(it) }
            AntelopePrimitiveDataTypes.INT16 -> abi.value.toShortOrNull()?.let { putShort(it) }
            AntelopePrimitiveDataTypes.INT32 -> abi.value.toIntOrNull()?.let { putInt(it) }
            AntelopePrimitiveDataTypes.INT64,
            AntelopePrimitiveDataTypes.TIME_POINT -> abi.value.toLongOrNull()?.let { putLong(it) }
            AntelopePrimitiveDataTypes.INT128, AntelopePrimitiveDataTypes.UINT128 -> abi.value.toBigInteger().let { putBytes(it.toByteArray().reversedArray()) } // Reverse to convert from big endian to little endian
            AntelopePrimitiveDataTypes.VARINT32 -> abi.value.toLongOrNull()?.let { putVariableInt(it) }
            AntelopePrimitiveDataTypes.UINT8 -> abi.value.toUByteOrNull()?.let { putUByte(it) }
            AntelopePrimitiveDataTypes.UINT16 -> abi.value.toUShortOrNull()?.let { putUShort(it) }
            AntelopePrimitiveDataTypes.UINT32,
            AntelopePrimitiveDataTypes.BLOCK_TIMESTAMP_TYPE,
            AntelopePrimitiveDataTypes.TIME_POINT_SEC -> abi.value.toUIntOrNull()?.let { putUInt(it) }
            AntelopePrimitiveDataTypes.UINT64 -> abi.value.toULongOrNull()?.let { putULong(it) }
            AntelopePrimitiveDataTypes.VARUINT32 -> abi.value.toLongOrNull()?.let { putVariableUInt(it) }
            AntelopePrimitiveDataTypes.FLOAT32 -> abi.value.toFloatOrNull()?.let { putInt(it.toRawBits()) }
            AntelopePrimitiveDataTypes.FLOAT64 -> abi.value.toDoubleOrNull()?.let { putLong(it.toRawBits()) }
            AntelopePrimitiveDataTypes.FLOAT128,
            AntelopePrimitiveDataTypes.CHECKSUM160,
            AntelopePrimitiveDataTypes.CHECKSUM256,
            AntelopePrimitiveDataTypes.CHECKSUM512 -> abi.value.let { putBytes(it.hexToByteArray()) }
            AntelopePrimitiveDataTypes.BOOL -> putByte(if (abi.value.toBoolean()) 1 else 0)
            AntelopePrimitiveDataTypes.STRING -> putString(abi.value)
            AntelopePrimitiveDataTypes.PUBLIC_KEY -> putPublicKey(EosPublicKey(abi.value))
            AntelopePrimitiveDataTypes.ASSET -> putAsset(abi.value)
            AntelopePrimitiveDataTypes.NAME -> putName(abi.value)
            AntelopePrimitiveDataTypes.BYTES -> putData(abi.value)
            AntelopePrimitiveDataTypes.EXTENDED_ASSET -> {
                val parts = abi.value.split(EXTENDED_TYPE_DELIMITER)
                val asset = parts.getOrNull(0) ?: throw IllegalArgumentException("Invalid extended asset format")
                val contract = parts.getOrNull(1) ?: throw IllegalArgumentException("Invalid extended asset format")

                putAsset(asset)
                putName(contract)
            }
            AntelopePrimitiveDataTypes.EXTENDED_SYMBOL -> {
                val parts = abi.value.split(EXTENDED_TYPE_DELIMITER)
                val symbol = parts.getOrNull(0) ?: throw IllegalArgumentException("Invalid extended symbol format")
                val contract = parts.getOrNull(1) ?: throw IllegalArgumentException("Invalid extended symbol format")

                putSymbol(symbol)
                putName(contract)
            }
            AntelopePrimitiveDataTypes.SIGNATURE -> putSignature(EosSignature.fromString(abi.value))
            AntelopePrimitiveDataTypes.SYMBOL -> putSymbol(abi.value)
            AntelopePrimitiveDataTypes.SYMBOL_CODE -> putCurrencySymbol(abi.value)
            null -> throw IllegalArgumentException("Unsupported fieldType: ${abi.fieldType}")
        }
    }
}