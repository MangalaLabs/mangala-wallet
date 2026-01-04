package com.mangala.wallet.features.chains.bitcoin.data.remote.balance.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MempoolStats(
    @SerialName("funded_txo_count")
    val fundedTxoCount: Long? = null,
    @SerialName("funded_txo_sum")
    val fundedTxoSum: Long? = null,
    @SerialName("spent_txo_count")
    val spentTxoCount: Long? = null,
    @SerialName("spent_txo_sum")
    val spentTxoSum: Long? = null,
    @SerialName("tx_count")
    val txCount: Long? = null
)