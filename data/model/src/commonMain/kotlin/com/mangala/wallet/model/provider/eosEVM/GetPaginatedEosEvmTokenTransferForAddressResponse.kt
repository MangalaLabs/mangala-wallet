package com.mangala.wallet.model.provider.eosEVM


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPaginatedEosEvmTokenTransferForAddressResponse(
    @SerialName("message")
    val message: String?,
    @SerialName("result")
    val result: List<Result>?,
    @SerialName("status")
    val status: String?
) {
    @Serializable
    data class Result(
        @SerialName("blockHash")
        val blockHash: String?,
        @SerialName("blockNumber")
        val blockNumber: String?,
        @SerialName("confirmations")
        val confirmations: String?,
        @SerialName("contractAddress")
        val contractAddress: String?,
        @SerialName("cumulativeGasUsed")
        val cumulativeGasUsed: String?,
        @SerialName("from")
        val from: String?,
        @SerialName("gas")
        val gas: String?,
        @SerialName("gasPrice")
        val gasPrice: String?,
        @SerialName("gasUsed")
        val gasUsed: String?,
        @SerialName("hash")
        val hash: String?,
        @SerialName("input")
        val input: String?,
        @SerialName("logIndex")
        val logIndex: String?,
        @SerialName("nonce")
        val nonce: String?,
        @SerialName("timeStamp")
        val timeStamp: String?,
        @SerialName("to")
        val to: String?,
        @SerialName("tokenDecimal")
        val tokenDecimal: String?,
        @SerialName("tokenName")
        val tokenName: String?,
        @SerialName("tokenSymbol")
        val tokenSymbol: String?,
        @SerialName("transactionIndex")
        val transactionIndex: String?,
        @SerialName("value")
        val value: String?
    )
}