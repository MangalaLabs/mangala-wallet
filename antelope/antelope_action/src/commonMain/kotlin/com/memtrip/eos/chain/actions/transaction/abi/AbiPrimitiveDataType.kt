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
package com.memtrip.eos.chain.actions.transaction.abi

import com.memtrip.eos.core.crypto.EosPublicKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface AbiPrimitiveDataType {
    val value: Any

    @Serializable
    data class NameType(override val value: String) : AbiPrimitiveDataType

    @Serializable
    data class AssetType(override val value: String) : AbiPrimitiveDataType

    @Serializable
    data class VariableUIntType(override val value: Long) : AbiPrimitiveDataType

    @Serializable
    data class PublicKeyType(@Serializable(with = EosPublicKeySerializer::class) override val value: EosPublicKey) :
        AbiPrimitiveDataType

    @Serializable
    data class StringType(override val value: String) : AbiPrimitiveDataType

    @Serializable
    data class ByteType(override val value: Int) : AbiPrimitiveDataType

    @Serializable
    data class Int64Type(override val value: Long) : AbiPrimitiveDataType

    @Serializable
    data class UInt32Type(override val value: Int) : AbiPrimitiveDataType

    @Serializable
    data class UInt64Type(override val value: Long) : AbiPrimitiveDataType

    @Serializable
    data class UInt16Type(override val value: Int) : AbiPrimitiveDataType

    @Serializable
    data class BoolType(override val value: Boolean) : AbiPrimitiveDataType
}

val abiPrimitiveDataTypeModule = SerializersModule {
    polymorphic(AbiPrimitiveDataType::class) {
        subclass(
            AbiPrimitiveDataType.NameType::class,
            AbiPrimitiveDataType.NameType.serializer()
        )
        subclass(
            AbiPrimitiveDataType.AssetType::class,
            AbiPrimitiveDataType.AssetType.serializer()
        )
        subclass(
            AbiPrimitiveDataType.VariableUIntType::class,
            AbiPrimitiveDataType.VariableUIntType.serializer()
        )
        subclass(
            AbiPrimitiveDataType.PublicKeyType::class,
            AbiPrimitiveDataType.PublicKeyType.serializer()
        )
        subclass(
            AbiPrimitiveDataType.StringType::class,
            AbiPrimitiveDataType.StringType.serializer()
        )
        subclass(
            AbiPrimitiveDataType.ByteType::class,
            AbiPrimitiveDataType.ByteType.serializer()
        )
        subclass(
            AbiPrimitiveDataType.Int64Type::class,
            AbiPrimitiveDataType.Int64Type.serializer()
        )
        subclass(
            AbiPrimitiveDataType.UInt32Type::class,
            AbiPrimitiveDataType.UInt32Type.serializer()
        )
        subclass(
            AbiPrimitiveDataType.UInt64Type::class,
            AbiPrimitiveDataType.UInt64Type.serializer()
        )
        subclass(
            AbiPrimitiveDataType.UInt16Type::class,
            AbiPrimitiveDataType.UInt16Type.serializer()
        )
        subclass(
            AbiPrimitiveDataType.BoolType::class,
            AbiPrimitiveDataType.BoolType.serializer()
        )
    }
}

object EosPublicKeySerializer : KSerializer<EosPublicKey> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("EosPublicKey", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EosPublicKey) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): EosPublicKey {
        val keyString = decoder.decodeString()
        return EosPublicKey(keyString)
    }
}