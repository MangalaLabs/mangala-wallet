package com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// for eth_getBlockByNumber with flag transaction_detail_flag = true
@Serializable
data class RpcLatestBlockResponse(
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
        val transactions: List<Transaction?>? = listOf(),
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
        data class Transaction(
            @SerialName("blockHash")
            val blockHash: String? = "",
            @SerialName("blockNumber")
            val blockNumber: String? = "",
            @SerialName("from")
            val from: String? = "",
            @SerialName("gas")
            val gas: String? = "",
            @SerialName("gasPrice")
            val gasPrice: String? = "",
            @SerialName("hash")
            val hash: String? = "",
            @SerialName("input")
            val input: String? = "",
            @SerialName("nonce")
            val nonce: String? = "",
            @SerialName("r")
            val r: String? = "",
            @SerialName("s")
            val s: String? = "",
            @SerialName("to")
            val to: String? = "",
            @SerialName("transactionIndex")
            val transactionIndex: String? = "",
            @SerialName("type")
            val type: String? = "",
            @SerialName("v")
            val v: String? = "",
            @SerialName("value")
            val value: String? = "",
            @SerialName("yParity")
            val yParity: String? = "",
            @SerialName("chainId")
            val chainId: String? = "",
            @SerialName("maxFeePerGas")
            val maxFeePerGas: String? = "",
            @SerialName("maxPriorityFeePerGas")
            val maxPriorityFeePerGas: String? = "",
            @SerialName("accessList")
            val accessList: List<AccessList?>? = null
        ) {

            @Serializable
            data class AccessList(
                @SerialName("address")
                val address: String? = "",
                @SerialName("storageKeys")
                val storageKeys: List<String?>? = listOf()
            )
        }

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