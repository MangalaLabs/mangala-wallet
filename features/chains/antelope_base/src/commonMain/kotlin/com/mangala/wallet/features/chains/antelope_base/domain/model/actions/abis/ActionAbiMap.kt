package com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis

import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ActionAbi
import kotlinx.serialization.Serializable

// TODO: Put this in a higher-level module

data class ActionAbiMap(
    val actionMapField : Map<ActionAbi, List<AntelopeActionAbi>>,
    val actionMapIndex : List<Int> // Used to revert the flattened map to its original nested state. Index = index of element, Value = index of parent
)

@Serializable
data class FlattenedActionFields(
    val actionList: List<AntelopeActionAbi>,
    val actionMapIndex: List<Int>
)