package com.mangala.wallet.model.ram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GetAccountRequest(
    @SerialName("account_name")
    val accountName: String
)