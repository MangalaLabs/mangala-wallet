package com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.FlattenedActionFields
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes

class GetFlattenedActionAbiArrayElementUseCase(
    private val getActionAbiByContractAndActionNameUseCase: GetActionAbiByContractAndActionNameUseCase
) {

    private var actionStatesFlattedMap: MutableList<AntelopeActionAbi> = mutableListOf()
    private var actionAbiMap: MutableList<Int> = mutableListOf()
    private var initialIndex = -1

    // Flattens the list of nested AntelopeActionAbi in a depth-first manner
    // Indices are depth-first
    suspend fun addArrayElement(
        actionBaseName: String,
        accountName: String,
        index: Int,
        level: Int
    ): FlattenedActionFields {
        actionStatesFlattedMap.clear()
        actionAbiMap.clear() // TODO: Should make this stateless
        initialIndex = index

        actionStatesFlattedMap.add(
            AntelopeActionAbi(
                actionName = actionBaseName,
                accountName = accountName,
                fieldName = actionBaseName,
                fieldType = actionBaseName,
                level = level,
                isArrayElement = true,
                isVariant = false
            )
        )
        actionAbiMap.add(index)

        resolveActionSubfields(accountName, actionBaseName, index + 1, level + 1)

        actionStatesFlattedMap[0] = actionStatesFlattedMap[0].copy(
            totalSubfieldElementsCount = actionStatesFlattedMap.size - 1 // Excluding itself
        )

        return FlattenedActionFields(actionStatesFlattedMap, actionAbiMap)
    }

    suspend fun setOptionalValue(
        actionBaseName: String,
        accountName: String,
        index: Int,
        level: Int
    ): FlattenedActionFields {
        actionStatesFlattedMap.clear()
        actionAbiMap.clear() // TODO: Should make this stateless
        initialIndex = index

        actionStatesFlattedMap.add(
            AntelopeActionAbi(
                actionName = actionBaseName,
                accountName = accountName,
                fieldName = actionBaseName,
                fieldType = actionBaseName,
                level = level,
                isOptionalElement = true,
                isVariant = false
            )
        )
        actionAbiMap.add(index)

        resolveActionSubfields(accountName, actionBaseName, index + 1, level + 1)

        actionStatesFlattedMap[0] = actionStatesFlattedMap[0].copy(
            totalSubfieldElementsCount = actionStatesFlattedMap.size
        )

        return FlattenedActionFields(actionStatesFlattedMap, actionAbiMap)
    }

    suspend fun getVariantElement(
        actionBaseName: String,
        accountName: String,
        variantTypeIndex: Int,
        index: Int,
        level: Int
    ): FlattenedActionFields {
        actionStatesFlattedMap.clear()
        actionAbiMap.clear() // TODO: Should make this stateless
        initialIndex = index

        actionStatesFlattedMap.add(
            AntelopeActionAbi(
                actionName = actionBaseName,
                accountName = accountName,
                fieldName = actionBaseName,
                fieldType = actionBaseName,
                level = level,
                variantTypeIndex = variantTypeIndex,
                isVariant = false,
            )
        )
        actionAbiMap.add(index)

        resolveActionSubfields(accountName, actionBaseName, index + 1, level + 1)

        actionStatesFlattedMap[0] = actionStatesFlattedMap[0].copy(
            totalSubfieldElementsCount = actionStatesFlattedMap.size - 1 // Excluding itself
        )

        return FlattenedActionFields(actionStatesFlattedMap, actionAbiMap)
    }

    private suspend fun resolveActionSubfields(
        accountName: String,
        actionBaseName: String,
        index: Int,
        level: Int
    ) {
        if (actionBaseName in AntelopePrimitiveDataTypes.entries.map { it.value }) {
            return
        }

        val rootActionAbi = getActionAbiByContractAndActionNameUseCase(
            accountName,
            actionBaseName,
            false
        ).getOrDefault(emptyList())

        rootActionAbi.forEachIndexed { subFieldIndex, antelopeActionAbi ->
            antelopeActionAbi.level = level

            actionStatesFlattedMap.add(antelopeActionAbi)
            actionAbiMap.add(index)

            if (antelopeActionAbi.isPrimitive.not()) {
                resolveActionSubfields(
                    accountName = antelopeActionAbi.accountName,
                    actionBaseName = antelopeActionAbi.baseType,
                    index = initialIndex + actionStatesFlattedMap.size,
                    level = level + 1
                )
            }
        }
    }
}