package com.mangala.wallet.features.chains.evmcompatible.domain.mapper

import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcTransactionReceipt
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcTransactionReceiptResponse
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import io.ktor.utils.io.core.toByteArray

@OptIn(ExperimentalStdlibApi::class)
fun RpcTransactionReceiptResponse.toRpcTransactionReceipt(): RpcTransactionReceipt? {
    if (result == null) return null

    return RpcTransactionReceipt(
        transactionHash = result.transactionHash.orEmpty(),
        transactionIndex = result.transactionIndex?.removePrefix("0x")?.hexToInt() ?: 0,
        blockHash = result.blockHash?.removePrefix("0x")?.toByteArray() ?: byteArrayOf(),
        blockNumber = result.blockNumber?.removePrefix("0x")?.hexToLong() ?: 0,
        from = Address(result.from.orEmpty()),
        to = result.to?.let { Address(result.to) },
        effectiveGasPrice = result.effectiveGasPrice?.removePrefix("0x")?.hexToLong() ?: 0,
        cumulativeGasUsed = result.cumulativeGasUsed?.removePrefix("0x")?.hexToLong() ?: 0,
        gasUsed = result.gasUsed?.removePrefix("0x")?.hexToLong() ?: 0,
        contractAddress = result.contractAddress?.let { Address(result.contractAddress) },
        logs = emptyList(), // TODO: Map
        logsBloom = result.logsBloom?.removePrefix("0x")?.hexToByteArray() ?: byteArrayOf(),
        root = result.root?.removePrefix("0x")?.hexToByteArray(),
        status = result.status?.removePrefix("0x")?.hexToInt()
    )
}