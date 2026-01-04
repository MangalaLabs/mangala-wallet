package com.mangala.wallet.model.provider.quicknode

import kotlinx.serialization.Serializable

data class QuickNodeTokenBalanceResponse(
    val id: Int? = null,
    val jsonrpc: String? = null,
    val result: TokenBalanceResult? = null
) {
    @Serializable
    data class TokenBalanceResult(
        val assets: List<Asset>? = null,
        val totalCount: Int? = null,
        val wallet: String? = null
    )

    @Serializable
    data class Asset(
        val name: String? = null,
        val symbol: String? = null,
        val decimals: Int? = null,
        val amount: String? = null,
        val contract: String? = null
    )
}