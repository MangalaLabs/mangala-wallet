package com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonArray

@Serializable
sealed class Param {
    @Serializable
    data class StringParam(val value: String) : Param()

    @Serializable
    data class IntParam(val value: Int) : Param()

    @Serializable
    data class BooleanParam(val value: Boolean) : Param()

    @Serializable
    data class MapParam(val value: Map<String, String?>) : Param()
}

object ParamListSerializer : KSerializer<List<Param?>> {
    val listSerializer = ListSerializer(ParamSerializer)

    override fun deserialize(decoder: Decoder): List<Param?> = with(decoder as JsonDecoder) {
        decodeJsonElement().jsonArray.mapNotNull {
            try {
                json.decodeFromJsonElement(ParamSerializer, it)
            } catch (e: SerializationException) {
                e.printStackTrace()
                null
            }
        }
    }


    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: List<Param?>) {
        listSerializer.serialize(encoder, value)
    }
}

object ParamSerializer: KSerializer<Param?> {
    override fun deserialize(decoder: Decoder): Param? {
        TODO()
    }

    @OptIn(ExperimentalSerializationApi::class, SealedSerializationApi::class)
    override val descriptor: SerialDescriptor = object : SerialDescriptor {
        override val elementsCount: Int get() = 0
        override fun getElementName(index: Int): String = error()
        override fun getElementIndex(name: String): Int = error()
        override fun isElementOptional(index: Int): Boolean = error()
        override fun getElementDescriptor(index: Int): SerialDescriptor = error()
        override fun getElementAnnotations(index: Int): List<Annotation> = error()
        override fun toString(): String = "PrimitiveDescriptor($serialName)"
        override val kind: SerialKind get() = SerialKind.CONTEXTUAL
        override val serialName: String get() = "value"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return false
        }
        override fun hashCode() = serialName.hashCode() + 31 * kind.hashCode()
        private fun error(): Nothing = throw IllegalStateException("Primitive descriptor does not have elements")
    }

    override fun serialize(encoder: Encoder, value: Param?) {
        when (value) {
            is Param.IntParam -> encoder.encodeSerializableValue(Int.serializer(), value.value)
            is Param.BooleanParam -> encoder.encodeSerializableValue(Boolean.serializer(), value.value)
            is Param.StringParam -> encoder.encodeSerializableValue(String.serializer(), value.value)
            is Param.MapParam -> encoder.encodeSerializableValue(MapSerializer(String.serializer(), String.serializer().nullable), value.value)
            else -> encoder.encodeSerializableValue(String.serializer(), value.toString())
        }
    }
}