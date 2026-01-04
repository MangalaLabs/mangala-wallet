package com.mangala.wallet.passkey.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import java.util.Base64

// Custom serializer for Base64-encoded ByteArray
object Base64ByteArraySerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Base64ByteArray", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteArray) {
        encoder.encodeString(Base64.getUrlEncoder().withoutPadding().encodeToString(value))
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        val string = decoder.decodeString()
        return Base64.getUrlDecoder().decode(string)
    }
}

// Data class for parsing registration response JSON
@Serializable
data class RegistrationResponseJson(
    val id: String,
    @Serializable(with = Base64ByteArraySerializer::class)
    val rawId: ByteArray,
    val type: String = "public-key",
    val authenticatorAttachment: String? = null,
    val clientExtensionResults: Map<String, JsonElement>? = null,
    val response: AttestationResponseJson
) {
    @Serializable
    data class AttestationResponseJson(
        @Serializable(with = Base64ByteArraySerializer::class)
        val clientDataJSON: ByteArray,
        @Serializable(with = Base64ByteArraySerializer::class)
        val attestationObject: ByteArray
    )
}

// Data class for parsing authentication response JSON
@Serializable
data class AuthenticationResponseJson(
    val id: String,
    @Serializable(with = Base64ByteArraySerializer::class)
    val rawId: ByteArray,
    val type: String = "public-key",
    val authenticatorAttachment: String? = null,
    val clientExtensionResults: Map<String, JsonElement>? = null,
    val response: AssertionResponseJson
) {
    @Serializable
    data class AssertionResponseJson(
        @Serializable(with = Base64ByteArraySerializer::class)
        val clientDataJSON: ByteArray,
        @Serializable(with = Base64ByteArraySerializer::class)
        val authenticatorData: ByteArray,
        @Serializable(with = Base64ByteArraySerializer::class)
        val signature: ByteArray,
        @Serializable(with = Base64ByteArraySerializer::class)
        val userHandle: ByteArray? = null
    )
}