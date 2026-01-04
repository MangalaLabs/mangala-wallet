package com.mangala.wallet.features.chains.evmcompatible.crypto
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class EIP712Encoder {
    fun encodeTypedDataHash(rawJsonMessage: String): ByteArray {
//        return StructuredDataEncoder(rawJsonMessage).hashStructuredData()
        return byteArrayOf()
    }

    fun parseTypedData(rawJsonMessage: String): TypedData? {
        return try {
            Json.decodeFromString<TypedData>(rawJsonMessage)
        } catch (error: Throwable) {
            null
        }
    }
}

data class TypedData(
    val types: Map<String, List<TypeParam>>,
    val primaryType: String,
    val domain: Map<String, Any>,
    val message: Map<String, Any>
)

data class TypeParam(
    val name: String,
    val type: String
)