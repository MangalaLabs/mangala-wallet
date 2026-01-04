package com.mangala.wallet.model.provider.alchemy

import kotlinx.serialization.Serializable

@Serializable
data class AlchemyNativeCoinBalanceResponse(
    val jsonrpc: String,
    val id: Int,
    val result: String
)