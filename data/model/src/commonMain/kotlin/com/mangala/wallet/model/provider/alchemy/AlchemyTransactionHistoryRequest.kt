package com.mangala.wallet.model.provider.alchemy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlchemyTransactionHistoryRequest (
    @SerialName("id") val id: Int,
    @SerialName("jsonrpc") val jsonrpc: String,
    @SerialName("method") val method: String,
    @SerialName("params") val params: List<TransferParams>
) {
    @Serializable
    data class TransferParams(
        @SerialName("fromBlock") val fromBlock: String,
        @SerialName("toBlock") val toBlock: String,
        @SerialName("fromAddress") val fromAddress: String?,
        @SerialName("toAddress") val toAddress: String?,
        @SerialName("contractAddresses") val contractAddresses: List<String>?,
        @SerialName("category") val category: List<String>,
        @SerialName("maxCount") val maxCount: String,
        @SerialName("excludeZeroValue") val excludeZeroValue: Boolean,
        @SerialName("pageKey") val pageKey: String? = null,
        @SerialName("order") val order: String,
        @SerialName("withMetadata") val withMetadata: Boolean
    )
}
