package com.mangala.wallet.model.provider.ipfs

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class TokenMetadataResponse(
    @SerialName("external_url")
    val externalUrl: String? = "",
    @SerialName("image_url")
    val imageUrl: String? = "",
    @SerialName("name")
    val name: String? = "",
    @SerialName("properties")
    val properties: List<Property?>? = listOf()
) {
    @Serializable
    data class Property(
        @SerialName("display_type")
        val displayType: String? = "",
        @SerialName("max_value")
        val maxValue: Int? = 0,
        @SerialName("trait_type")
        val traitType: String? = "",
        @Serializable(ValueSerializer::class)
        val value: Any? = null
    )
}

object ValueSerializer: KSerializer<Any> {
    override fun deserialize(decoder: Decoder): Any {
        return try {
            return decoder.decodeSerializableValue(Int.serializer())
        } catch (e: Exception) {
            try {
                decoder.decodeSerializableValue(String.serializer())
            } catch (e: Exception) {
                try {
                    decoder.decodeSerializableValue(Boolean.serializer())
                } catch (e: Exception) {
                    throw SerializationException("Unknown type")
                }
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
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

    override fun serialize(encoder: Encoder, value: Any) {
        encoder.encodeString(value.toString())
    }
}
