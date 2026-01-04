package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

import com.mangala.wallet.features.chains.antelope_base.domain.model.ActionPagingModel

data class ListActionDataUiModel(
    val summaryHeaders: List<ActionDataSummaryHeaderUiModel>,
    val blockTime: String?,
    val actionDataUiModel: ActionDataUiModel,
    private val txId: String
) {
    val formattedTxId = txId.take(8) + "..." + txId.takeLast(8)
}

fun ActionPagingModel.toListActionDataUiModel(accountName: String): ListActionDataUiModel {
    val blockTime = actionTrace?.firstOrNull()?.blockTime ?: "00:00"
    val actionDataUiModel = ActionDataUiModel(
        actionTraces = actionTrace.orEmpty(),
        trxId = trxId,
    )

    val groups = actionTrace?.getGroupedActionTraces(accountName).orEmpty()
    val summaryHeaders = groups.toActionDataSummaryHeaderUiModels(accountName)

    return ListActionDataUiModel(
        summaryHeaders = summaryHeaders,
        blockTime = blockTime,
        actionDataUiModel = actionDataUiModel,
        txId = trxId.orEmpty()
    )
}
