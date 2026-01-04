package com.mangala.wallet.features.chains.bitcoin.data.remote.balance.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MempoolGetBalanceResponse(
    @SerialName("address")
    val address: String? = null,
    @SerialName("chain_stats")
    val chainStats: ChainStats? = null,
    @SerialName("mempool_stats")
    val mempoolStats: MempoolStats? = null
)