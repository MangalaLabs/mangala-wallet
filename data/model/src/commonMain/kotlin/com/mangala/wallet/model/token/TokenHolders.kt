package com.mangala.wallet.model.token

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.SerialName

data class TokenHolders(
    val count: BigDecimal,
    @SerialName("holders_url")
    val holdersUrl: String?,
    @SerialName("top_holders")
    val topHolders: List<Holder>
)

data class Holder(
    val address: String,
    val percentage: BigDecimal,
    val balance: BigDecimal
)
