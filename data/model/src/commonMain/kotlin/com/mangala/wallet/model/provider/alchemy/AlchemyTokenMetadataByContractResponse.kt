package com.mangala.wallet.model.provider.alchemy

import kotlinx.serialization.Serializable

@Serializable
data class AlchemyTokenMetadataByContractResponse(
    val jsonrpc: String? = null,
    val id: Int? = null,
    val result: AlchemyTokenMetadata? = null
) {
    @Serializable
    data class AlchemyTokenMetadata(
        val name: String? = null,
        val symbol: String? = null,
        val decimals: Int? = null,
        val logo: String? = null
    )
}