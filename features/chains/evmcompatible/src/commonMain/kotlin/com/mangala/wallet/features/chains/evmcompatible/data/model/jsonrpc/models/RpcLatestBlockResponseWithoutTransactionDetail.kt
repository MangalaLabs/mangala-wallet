package com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// for eth_getBlockByNumber with flag transaction_detail_flag = false
@Serializable
class RpcLatestBlockResponseWithoutTransactionDetail(
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("jsonrpc")
    val jsonrpc: String? = "",
    @SerialName("result")
    val latestBlock: LatestBlock? = LatestBlock()
) {
    @Serializable
    data class LatestBlock(
        @SerialName("baseFeePerGas")
        val baseFeePerGas: String? = "",
        @SerialName("difficulty")
        val difficulty: String? = "",
        @SerialName("extraData")
        val extraData: String? = "",
        @SerialName("gasLimit")
        val gasLimit: String? = "",
        @SerialName("gasUsed")
        val gasUsed: String? = "",
        @SerialName("hash")
        val hash: String? = "",
        @SerialName("logsBloom")
        val logsBloom: String? = "",
        @SerialName("miner")
        val miner: String? = "",
        @SerialName("mixHash")
        val mixHash: String? = "",
        @SerialName("nonce")
        val nonce: String? = "",
        @SerialName("number")
        val number: String? = "",
        @SerialName("parentHash")
        val parentHash: String? = "",
        @SerialName("receiptsRoot")
        val receiptsRoot: String? = "",
        @SerialName("sha3Uncles")
        val sha3Uncles: String? = "",
        @SerialName("size")
        val size: String? = "",
        @SerialName("stateRoot")
        val stateRoot: String? = "",
        @SerialName("timestamp")
        val timestamp: String? = "",
        @SerialName("totalDifficulty")
        val totalDifficulty: String? = "",
        @SerialName("transactions")
        val transactions: List<String?>? = listOf(),
        @SerialName("transactionsRoot")
        val transactionsRoot: String? = "",
        @SerialName("withdrawals")
        val withdrawals: List<Withdrawals?>? = listOf(),
        @SerialName("withdrawalsRoot")
        val withdrawalsRoot: String? = "",
        @SerialName("uncles")
        val uncles: List<String?>? = listOf()
    ) {
        @Serializable
        data class Withdrawals(
            @SerialName("address")
            val address: String? = "",
            @SerialName("amount")
            val amount: String? = "",
            @SerialName("index")
            val index: String? = "",
            @SerialName("validatorIndex")
            val validatorIndex: String? = "",
        )
    }
}