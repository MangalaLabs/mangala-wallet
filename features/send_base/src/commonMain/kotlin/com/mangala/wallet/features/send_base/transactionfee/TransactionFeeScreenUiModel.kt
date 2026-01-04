package com.mangala.wallet.features.send_base.transactionfee

import com.mangala.wallet.features.chains.ui.FeeOptionUiModel

data class TransactionFeeScreenUiModel(
    val feeOptions: List<FeeOptionUiModel> = emptyList()
)