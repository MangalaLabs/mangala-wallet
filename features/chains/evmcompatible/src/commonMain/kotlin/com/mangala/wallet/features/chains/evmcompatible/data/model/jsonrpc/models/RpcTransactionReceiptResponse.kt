package com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RpcTransactionReceiptResponse(
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("jsonrpc")
    val jsonrpc: String? = "",
    @SerialName("result")
    val result: Result? = Result()
) {
    @Serializable
    data class Result(
        @SerialName("blockHash")
        val blockHash: String? = "",
        @SerialName("blockNumber")
        val blockNumber: String? = "",
        @SerialName("contractAddress")
        val contractAddress: String? = "",
        @SerialName("cumulativeGasUsed")
        val cumulativeGasUsed: String? = "",
        @SerialName("effectiveGasPrice")
        val effectiveGasPrice: String? = "",
        @SerialName("from")
        val from: String? = "",
        @SerialName("gasUsed")
        val gasUsed: String? = "",
        @SerialName("logs")
        val logs: List<Log?>? = listOf(),
        @SerialName("logsBloom")
        val logsBloom: String? = "",
        @SerialName("status")
        val status: String? = "",
        @SerialName("root")
        val root: String? = "",
        @SerialName("to")
        val to: String? = "",
        @SerialName("transactionHash")
        val transactionHash: String? = "",
        @SerialName("transactionIndex")
        val transactionIndex: String? = "",
        @SerialName("type")
        val type: String? = ""
    ) {
        @Serializable
        data class Log(
            @SerialName("address")
            val address: String? = "",
            @SerialName("blockHash")
            val blockHash: String? = "",
            @SerialName("blockNumber")
            val blockNumber: String? = "",
            @SerialName("data")
            val `data`: String? = "",
            @SerialName("logIndex")
            val logIndex: String? = "",
            @SerialName("removed")
            val removed: Boolean? = false,
            @SerialName("topics")
            val topics: List<String?>? = listOf(),
            @SerialName("transactionHash")
            val transactionHash: String? = "",
            @SerialName("transactionIndex")
            val transactionIndex: String? = ""
        )
    }
}