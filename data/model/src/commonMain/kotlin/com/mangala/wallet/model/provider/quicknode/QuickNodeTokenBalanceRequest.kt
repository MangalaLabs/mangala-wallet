package com.mangala.wallet.model.provider.quicknode

import kotlinx.serialization.Serializable

data class QuickNodeTokenBalanceRequest(
    val id: Int = 67, // Arbitrary ID
    val jsonrpc: String = "2.0",
    val method: String = "qn_getWalletTokenBalance",
    val params: List<WalletParam>
) {
    @Serializable
    data class WalletParam(
        val wallet: String
    )
}