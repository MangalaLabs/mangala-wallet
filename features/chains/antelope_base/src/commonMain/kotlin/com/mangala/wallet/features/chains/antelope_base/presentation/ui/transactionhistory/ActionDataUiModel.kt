package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

import com.mangala.antelope.base.api.model.ActionTrace

data class ActionDataUiModel(
    val actionTraces: List<ActionTrace>,
    val trxId : String?,
)