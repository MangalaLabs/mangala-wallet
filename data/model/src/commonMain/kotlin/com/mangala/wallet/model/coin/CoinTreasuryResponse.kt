package com.mangala.wallet.model.coin

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.SerialName

data class CoinTreasuryResponse(
    val type: String,
    val fund: String,
    @SerialName("fund_uid")
    val fundUid: String,
    val amount: BigDecimal,
    @SerialName("amount_in_currency")
    val amountInCurrency: BigDecimal,
    @SerialName("country")
    val countryCode: String
)
