package com.mangala.wallet.features.chains.antelope_base.data.repository.actions.abis

import com.mangala.antelope.base.api.model.GetAbiResponse
import com.mangala.antelope.base.api.model.Struct
import com.mangala.antelope.base.api.model.TypeDef
import com.mangala.antelope.base.api.model.Variant
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionAbiEntity
import com.mangala.wallet.utils.ext.toLong
import kotlinx.datetime.Clock

fun GetAbiResponse.toAntelopeActionAbiEntities(accountName: String): List<AntelopeActionAbiEntity> {
    val types = this.abi?.types.orEmpty()
    val variants = this.abi?.variants.orEmpty()

    val typesMap = types.associateBy { it.type }
    val variantsMap = variants.associateBy { it.name }

    return this.abi?.structs?.toAntelopeActionAbiEntities(accountName, typesMap, variantsMap) ?: emptyList()
}

fun List<Struct>.toAntelopeActionAbiEntities(
    accountName: String,
    typesMap: Map<String?, TypeDef>,
    variantsMap: Map<String?, Variant>
): List<AntelopeActionAbiEntity> {
    var sttCounter = 1L

    val createdAt = Clock.System.now().toEpochMilliseconds()

    val structs = this.flatMap { struct ->
        struct.fields?.map { field ->
            AntelopeActionAbiEntity(
                action_name = struct.name ?: "",
                account_name = accountName,
                field_name = field.name ?: "",
                field_type = field.type ?: "",
                createdAt,
                stt = sttCounter++,
                is_variant = (typesMap[field.type] != null).toLong()
            )
        } ?: emptyList()
    }

    val variantStructs = typesMap.entries.mapNotNull {
        val type = it.value ?: return@mapNotNull null

        val variant = variantsMap[type.type]

        AntelopeActionAbiEntity(
            action_name = type.newTypeName.orEmpty(),
            account_name = accountName,
            field_name = type.type.orEmpty(),
            field_type = variant?.types?.joinToString(",").orEmpty(),
            createdAt,
            stt = sttCounter++,
            is_variant = (typesMap[type.type] != null).toLong()
        )
    }

    return structs + variantStructs
}

fun List<AntelopeActionAbiEntity>.toAntelopeActionsAbi(): List<AntelopeActionAbi> {
    return this.map { actionAbiEntity ->
        AntelopeActionAbi(
            actionName = actionAbiEntity.action_name,
            accountName = actionAbiEntity.account_name,
            fieldName = actionAbiEntity.field_name,
            fieldType = actionAbiEntity.field_type,
            isVariant = actionAbiEntity.is_variant == 1L
        )
    }
}


