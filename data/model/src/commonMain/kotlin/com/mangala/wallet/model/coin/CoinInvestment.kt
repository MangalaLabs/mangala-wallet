package com.mangala.wallet.model.coin

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.SerialName

data class CoinInvestment(
    val date: String,
    val round: String,
    val amount: BigDecimal?,
    val funds: List<Fund>
) {

    data class Fund(
        val uid: String,
        val name: String,
        val website: String,
        @SerialName("is_lead")
        val isLead: Boolean
    )
}