package com.mangala.features.wallet.presentation

import com.ionspin.kotlin.bignum.decimal.BigDecimal

interface BaseAccountItemUiModel {
    val isBalanceVisible: Boolean
    val currencySymbol: String
    val formattedPnl: String?
}