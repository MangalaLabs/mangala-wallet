package com.mangala.wallet.features.chains.antelope_base.data.repository.actions

import com.mangala.antelope.base.api.model.ActionAbi
import com.mangala.antelope.base.api.model.GetAbiResponse
import com.mangala.antelope.base.api.model.Struct
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionAbiEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionEntity
import kotlinx.datetime.Clock

fun List<ActionAbi>.toAntelopeActionEntities(
    accountName: String,
    lastTimeUpdatedCode: Long
): List<AntelopeActionEntity> {

    return this.flatMap { action ->
        listOf(
            AntelopeActionEntity(
                actionName = action.name?: "",
                accountName = accountName,
                lastTimeUpdatedCode = lastTimeUpdatedCode
            )
        )
    }
}


fun GetAbiResponse.toAntelopeActionEntities(
    accountName: String,
    lastTimeUpdatedCode: Long
): List<AntelopeActionEntity> {
    return this.abi?.actions?.toAntelopeActionEntities(
        accountName,
        lastTimeUpdatedCode
    ) ?: emptyList()
}


fun List<AntelopeActionEntity>.toAntelopeActionsAbi(): List<AntelopeActionAbi> {
    return this.map { actionAbiEntity ->
        AntelopeActionAbi(
            actionName = actionAbiEntity.actionName,
            accountName = actionAbiEntity.accountName,
            isVariant = false
        )
    }
}