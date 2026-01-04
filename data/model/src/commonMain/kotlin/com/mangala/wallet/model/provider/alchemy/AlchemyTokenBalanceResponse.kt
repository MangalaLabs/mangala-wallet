package com.mangala.wallet.model.provider.alchemy

import kotlinx.serialization.Serializable

@Serializable
data class AlchemyTokenBalanceResponse(
    val jsonrpc: String? = null,
    val id: Long? = null,
    val result: TokenBalanceResult? = null
) {
    @Serializable
    data class TokenBalanceResult(
        val address: String? = null,
        val tokenBalances: List<TokenBalance>? = null,
        val pageKey: String? = null
    ) {
        @Serializable
        data class TokenBalance(
            val contractAddress: String? = null,
            val tokenBalance: String? = null
        )
    }
}