package com.mangala.wallet.features.chains.evmcompatible.domain.mapper

import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcLatestBlock
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcLatestBlockResponseWithoutTransactionDetail

@OptIn(ExperimentalStdlibApi::class)
fun RpcLatestBlockResponseWithoutTransactionDetail.toRpcLatestBlock(): RpcLatestBlock? {
    if (latestBlock == null) return null

    return RpcLatestBlock(
        hash = latestBlock.hash.orEmpty(),
        gasLimit = latestBlock.gasLimit?.removePrefix("0x")?.hexToLong() ?: 0
    )
}
