package com.mangala.wallet.features.chains.antelope_base.domain.mapper

import com.mangala.antelope.base.api.model.EosAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.ActionPagingModel
import com.mangala.wallet.features.chains.antelope_base.domain.model.ActionPagingModelBuilder
import com.mangala.wallet.features.chains.antelope_base.domain.model.GetActionsPagingModel
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionsEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun List<EosAction>.toAntelopeActionsEntity(
    accountName: String,
    blockchainUid: String
): List<AntelopeActionsEntity> {
    val getActionPagingModel = transformGetActionsFilterResponse(this)
    return getActionPagingModel.actions?.map {
        AntelopeActionsEntity(
            accountName = accountName,
            blockchain_uid = blockchainUid,
            blockNum = it.blockNum ?: emptyList(),
            trxId = it.trxId ?: "",
            actionTrace = it.actionTrace?.map { actionTrace ->
                Json.encodeToString(actionTrace)
            } ?: emptyList(),
            accountActionSeq = it.accountActionSeq ?: emptyList(),
        )
    } ?: emptyList()
}


private fun transformGetActionsFilterResponse(actions: List<EosAction>?): GetActionsPagingModel {
    // Map to group data by trxId
    val groupedByTrxId = mutableMapOf<String, ActionPagingModelBuilder>()
    actions?.forEach { action ->
        val trace = action.act
        val trxId = action.trxId
        if (trxId != null) {
            if (!groupedByTrxId.containsKey(trxId)) {
                groupedByTrxId[trxId] = ActionPagingModelBuilder(trxId)
            }
            groupedByTrxId[trxId]?.addAct(action)
        }
    }

    // Convert each entry in the map to an ActionPagingModel
    val actionPagingModels = groupedByTrxId.values.map {
        it.build()
    }

    return GetActionsPagingModel(
        actions = actionPagingModels
    )
}

fun AntelopeActionsEntity.toActionPagingModel() = ActionPagingModel(
    blockNum = blockNum,
    trxId = trxId,
    actionTrace = actionTrace.map { Json.decodeFromString(it) },
    accountActionSeq = accountActionSeq,
)