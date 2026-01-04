package com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.ActionAbiMap
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes

class GetActionAbi(
    private val getActionAbiByContractAndActionNameUseCase: GetActionAbiByContractAndActionNameUseCase
) {

    private var actionStatesFlattedMap: MutableMap<ActionAbi, List<AntelopeActionAbi>> = mutableMapOf()
    private var actionAbiMap: MutableList<Int> = mutableListOf()

    // Flattens the list of nested AntelopeActionAbi in a depth-first manner
    // Indices are depth-first
    suspend operator fun invoke(
        actionAbi: List<AntelopeActionAbi>, action: ActionAbi
    ): ActionAbiMap {
        actionStatesFlattedMap = mutableMapOf()
        actionAbiMap = mutableListOf()

        actionAbi.filter { it.actionName.isNotEmpty() }
            .forEachIndexed { index, antelopeActionAbi ->
                processActionAbi(antelopeActionAbi, action)
            }

        return ActionAbiMap(actionStatesFlattedMap, actionAbiMap)
    }

    private suspend fun processActionAbi(
        actionAbi: AntelopeActionAbi,
        action: ActionAbi,
        parentIndex: Int = -1,
        level: Int = 0
    ) {
        actionAbi.level = level

        if (actionAbi.isPrimitive.not() && actionAbi.isArray.not() && actionAbi.isExtension.not() && actionAbi.isOptional.not()) {
            // We don't resolve array elements since it's not strictly necessary
            val actionQuery = if (actionAbi.isVariant && actionAbi.isExtension.not() && actionAbi.isOptional.not()) {
                // For mandatory variants, we resolve the first argument by default
                actionAbi.baseType.split(",").firstOrNull().orEmpty()
            } else {
                actionAbi.baseType
            }

            val subFields = getActionAbiByContractAndActionNameUseCase(
                action.account,
                actionQuery,
                false
            ).getOrDefault(emptyList())

            actionStatesFlattedMap = actionStatesFlattedMap.apply {
                this[action] = this[action]?.plus(actionAbi) ?: listOf(actionAbi)
            }
            val newParentIndex = actionAbiMap.size
            actionAbiMap.add(parentIndex)

            if (AntelopePrimitiveDataTypes.entries.map { it.value }.contains(actionQuery) && subFields.isEmpty()) {
                // This case most likely comes from a variant with primitive types
                val primitiveField = AntelopeActionAbi(
                    actionName = actionQuery,
                    accountName = action.account,
                    fieldName = actionQuery,
                    fieldType = actionQuery,
                    level = level,
                    isVariant = false,
                    variantTypeIndex = 0
                )

                actionStatesFlattedMap = actionStatesFlattedMap.apply {
                    this[action] = this[action]?.plus(primitiveField) ?: listOf(primitiveField)
                }
                actionAbiMap.add(newParentIndex)

                return
            }

            actionAbi.subFields = subFields
            subFields.forEachIndexed { subFieldIndex, antelopeActionAbi ->
                processActionAbi(antelopeActionAbi, action, newParentIndex, level + 1)
            }
        } else {
            actionStatesFlattedMap = actionStatesFlattedMap.apply {
                this[action] = this[action]?.plus(actionAbi) ?: listOf(actionAbi)
            }
            actionAbiMap.add(parentIndex)
        }
    }
}