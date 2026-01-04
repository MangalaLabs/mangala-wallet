package com.mangala.wallet.model.account.domain

import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.model.token.domain.TokenBalanceModel

data class AccountBalanceModel(
    val totalAmount: String,
    val tokens: List<TokenBalanceModel>
)