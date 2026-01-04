package com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura

import kotlinx.serialization.Serializable

@Serializable
data class GasPriceDto(
    val id: Int?,
    val jsonrpc: String?,
    val result: String?
)