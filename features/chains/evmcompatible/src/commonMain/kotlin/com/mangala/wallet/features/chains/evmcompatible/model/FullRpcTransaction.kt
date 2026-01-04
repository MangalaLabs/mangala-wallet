package com.mangala.wallet.features.chains.evmcompatible.model

import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcBlock
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcTransaction
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcTransactionReceipt

data class FullRpcTransaction(
    val rpcTransaction: RpcTransaction,
    val rpcReceipt: RpcTransactionReceipt?,
    val rpcBlock: RpcBlock?,
    var internalTransactions: MutableList<InternalTransaction> = mutableListOf()
) {

    val isFailed: Boolean =
        when {
            rpcReceipt == null -> false
            rpcReceipt.status == null -> rpcTransaction.gas == rpcReceipt.cumulativeGasUsed
            else -> rpcReceipt.status == 0
        }

    fun transaction(timestamp: Long) =
        Transaction(
            rpcTransaction.hash,
            timestamp,
            isFailed,
            rpcBlock?.number,
            rpcReceipt?.transactionIndex,
            rpcTransaction.from,
            rpcTransaction.to,
            rpcTransaction.value,
            rpcTransaction.input,
            rpcTransaction.nonce,
            rpcTransaction.gasPrice,
            rpcTransaction.maxFeePerGas,
            rpcTransaction.maxPriorityFeePerGas,
            rpcTransaction.gas,
            rpcReceipt?.gasUsed
        )

}
