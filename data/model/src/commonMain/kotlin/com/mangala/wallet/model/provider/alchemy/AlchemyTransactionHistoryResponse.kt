package com.mangala.wallet.model.provider.alchemy


import com.mangala.wallet.model.provider.BaseGetPaginatedTransactionsForAddressResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlchemyTransactionHistoryResponse(
    @SerialName("jsonrpc") val jsonrpc: String,
    @SerialName("id") val id: Int,
    @SerialName("result") val result: TransferResult?
): BaseGetPaginatedTransactionsForAddressResponse() {

    @Serializable
    data class TransferResult(
        @SerialName("transfers") val transfers: List<Transfer>,
        @SerialName("pageKey") val pageKey: String? = null
    )

    @Serializable
    data class Transfer(
        @SerialName("blockNum") val blockNum: String,
        @SerialName("uniqueId") val uniqueId: String,
        @SerialName("hash") val hash: String,
        @SerialName("from") val from: String,
        @SerialName("to") val to: String,
        @SerialName("value") val value: Double?,
        @SerialName("erc721TokenId") val erc721TokenId: String?,
        @SerialName("erc1155Metadata") val erc1155Metadata: String?,
        @SerialName("tokenId") val tokenId: String?,
        @SerialName("asset") val asset: String?,
        @SerialName("category") val category: String,
        @SerialName("rawContract") val rawContract: RawContract,
        @SerialName("metadata") val metadata: Metadata
    )

    @Serializable
    data class RawContract(
        @SerialName("value") val value: String,
        @SerialName("address") val address: String?,
        @SerialName("decimal") val decimal: String?
    )

    @Serializable
    data class Metadata(
        @SerialName("blockTimestamp") val blockTimestamp: String?,
    )
}