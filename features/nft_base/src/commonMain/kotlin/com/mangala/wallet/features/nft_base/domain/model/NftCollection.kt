package com.mangala.wallet.features.nft_base.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class NftCollection(
    val contractName: String,
    val contractTickerSymbol: String,
    val contractAddress: String,
    val nft: List<Nft>,
    val type: NftType
) {
    data class Nft(
        val tokenId: String,
        val tokenUrl: String,
        val name: String,
        val description: String,
        val image: String,
        val attributes: List<Attribute>,
        val isFavorite: Boolean = false
    ) {
        @Serializable
        data class Attribute(
            @SerialName("traitType")
            val traitType: String,
            @SerialName("value")
            val value: String
        )
    }
}