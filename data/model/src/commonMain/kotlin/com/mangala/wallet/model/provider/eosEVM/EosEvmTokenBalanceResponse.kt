package com.mangala.wallet.model.provider.eosEVM


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EosEvmTokenBalanceResponse(
    @SerialName("message")
    val message: String?,
    @SerialName("result")
    val result: List<Result>?,
    @SerialName("status")
    val status: String?
) {
    @Serializable
    data class Result(
        @SerialName("balance")
        val balance: String?,
        @SerialName("contractAddress")
        val contractAddress: String?,
        @SerialName("decimals")
        val decimals: String?,
        @SerialName("name")
        val name: String?,
        @SerialName("symbol")
        val symbol: String?,
        @SerialName("type")
        val type: String?
    )
}