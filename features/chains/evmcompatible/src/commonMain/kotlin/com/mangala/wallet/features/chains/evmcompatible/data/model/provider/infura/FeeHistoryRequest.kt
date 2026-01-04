package com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura

data class FeeHistoryRequest(
    val jsonrpc: String?,
    val method: String?,
    val params: List<Any?>?,
    val id: Int?
)