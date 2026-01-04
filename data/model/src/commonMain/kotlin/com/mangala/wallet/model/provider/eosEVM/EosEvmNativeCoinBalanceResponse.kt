package com.mangala.wallet.model.provider.eosEVM


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EosEvmNativeCoinBalanceResponse(
    @SerialName("message")
    val message: String?,
    @SerialName("result")
    val result: String?,
    @SerialName("status")
    val status: String?
)