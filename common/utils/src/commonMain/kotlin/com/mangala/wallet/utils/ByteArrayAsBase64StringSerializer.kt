package com.mangala.wallet.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object ByteArrayAsBase64StringSerializer: KSerializer<ByteArray> {

    @OptIn(ExperimentalEncodingApi::class)
    override fun deserialize(decoder: Decoder): ByteArray {
        val string = decoder.decodeString()
        return Base64.Default.decode(string)
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("ByteArrayAsBase64StringSerializer", PrimitiveKind.STRING)

    @OptIn(ExperimentalEncodingApi::class)
    override fun serialize(encoder: Encoder, value: ByteArray) {
        return encoder.encodeString(Base64.Default.encode(value))
    }
}