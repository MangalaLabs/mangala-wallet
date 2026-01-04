package com.mangala.wallet.features.chains.bitcoin.data.remote.balance.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MempoolUtxoResponseItem(
    @SerialName("txid")
    val txid: String? = null,
    @SerialName("vout")
    val vout: Int? = null,
    @SerialName("status")
    val status: Status? = null,
    @SerialName("value")
    val value: Int? = null
) {
    @Serializable
    data class Status(
        @SerialName("confirmed")
        val confirmed: Boolean? = null,
        @SerialName("block_height")
        val blockHeight: Int? = null,
        @SerialName("block_hash")
        val blockHash: String? = null,
        @SerialName("block_time")
        val blockTime: Int? = null
    )
}