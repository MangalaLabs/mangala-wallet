package com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models

import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionLog

data class RpcTransactionReceipt(
    val transactionHash: String,
    val transactionIndex: Int,
    val blockHash: ByteArray,
    val blockNumber: Long,
    val from: Address,
    val to: Address?,
    val effectiveGasPrice: Long,
    val cumulativeGasUsed: Long,
    val gasUsed: Long,
    val contractAddress: Address?,
    val logs: List<TransactionLog>,
    val logsBloom: ByteArray,
    val root: ByteArray?,
    val status: Int?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RpcTransactionReceipt

        if (transactionHash != other.transactionHash) return false
        if (transactionIndex != other.transactionIndex) return false
        if (!blockHash.contentEquals(other.blockHash)) return false
        if (blockNumber != other.blockNumber) return false
        if (from != other.from) return false
        if (to != other.to) return false
        if (effectiveGasPrice != other.effectiveGasPrice) return false
        if (cumulativeGasUsed != other.cumulativeGasUsed) return false
        if (gasUsed != other.gasUsed) return false
        if (contractAddress != other.contractAddress) return false
        if (logs != other.logs) return false
        if (!logsBloom.contentEquals(other.logsBloom)) return false
        if (root != null) {
            if (other.root == null) return false
            if (!root.contentEquals(other.root)) return false
        } else if (other.root != null) return false
        if (status != other.status) return false

        return true
    }

    override fun hashCode(): Int {
        var result = transactionHash.hashCode()
        result = 31 * result + transactionIndex
        result = 31 * result + blockHash.contentHashCode()
        result = 31 * result + blockNumber.hashCode()
        result = 31 * result + from.hashCode()
        result = 31 * result + (to?.hashCode() ?: 0)
        result = 31 * result + effectiveGasPrice.hashCode()
        result = 31 * result + cumulativeGasUsed.hashCode()
        result = 31 * result + gasUsed.hashCode()
        result = 31 * result + (contractAddress?.hashCode() ?: 0)
        result = 31 * result + logs.hashCode()
        result = 31 * result + logsBloom.contentHashCode()
        result = 31 * result + (root?.contentHashCode() ?: 0)
        result = 31 * result + (status ?: 0)
        return result
    }
}
