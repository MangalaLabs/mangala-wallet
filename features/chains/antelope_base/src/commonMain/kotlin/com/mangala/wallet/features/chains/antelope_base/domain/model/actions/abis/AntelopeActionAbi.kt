package com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis

import com.memtrip.eos.chain.actions.transaction.abi.AbiPrimitiveDataType
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class AntelopeActionAbi(
    val actionName: String,
    val accountName: String,
    val fieldName: String = "",
    val fieldType: String = "",
    var value: String = "",
    var level: Int = 0,
    var subFields: List<AntelopeActionAbi> = emptyList(),
    var arraySize: Int = 0,
    val isArrayElement: Boolean = false,
    val isOptionalElement: Boolean = false,
    val totalSubfieldElementsCount: Int = 0,
    val isOptionalValueSet: Boolean = false,
    @Serializable(with = ActionValueMapSerializer::class) var mapValue: Map<String, List<AbiPrimitiveDataType>> = emptyMap(),
    val isVariant: Boolean = false,
    val variantTypeIndex: Int? = null,
    val symbolDecimals: Int? = null,
) {
    val isArray = fieldType.contains("[]")
    val isOptional = fieldType.contains("?")
    val isExtension = fieldType.contains("$")

    val baseType: String
        get() {
            var result = fieldType

            if (isArray) {
                result = fieldType.replace("[]", "")
            }
            if (isOptional) {
                result = fieldType.replace("?", "")
            }
            if (isExtension) {
                result = fieldType.replace("$", "")
            }

            return result
        }
    val isPrimitive = AntelopePrimitiveDataTypes.entries.any { it.value == fieldType }
    val isBaseTypePrimitive = AntelopePrimitiveDataTypes.entries.any { it.value == baseType }
    val isArrayPrimitive = AntelopePrimitiveDataTypes.entries.any { it.value == baseType } && isArray
    val isArrayObject = !AntelopePrimitiveDataTypes.entries.any { it.value == baseType } && isArray
    val isObject = !AntelopePrimitiveDataTypes.entries.any { it.value == baseType } && !isArray
    val variantTypes = if (isVariant) fieldType.split(",").map { it.trim() } else null
}

object ActionValueMapSerializer: KSerializer<Map<String, List<AbiPrimitiveDataType>>> {
    private val mapSerializer = MapSerializer(
        String.serializer(),
        ListSerializer(
            PolymorphicSerializer(AbiPrimitiveDataType::class)
        )
    )

    override val descriptor = mapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Map<String, List<AbiPrimitiveDataType>>) {
        mapSerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Map<String, List<AbiPrimitiveDataType>> {
        return mapSerializer.deserialize(decoder)
    }
}