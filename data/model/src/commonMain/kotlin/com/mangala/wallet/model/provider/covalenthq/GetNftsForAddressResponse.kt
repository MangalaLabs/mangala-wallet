package com.mangala.wallet.model.provider.covalenthq


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetNftsForAddressResponse(
    @SerialName("data")
    val `data`: Data? = Data(),
    @SerialName("error")
    val error: Boolean? = false,
    @SerialName("error_code")
    val errorCode: Int? = null,
    @SerialName("error_message")
    val errorMessage: String? = null
) {
    @Serializable
    data class Data(
        @SerialName("address")
        val address: String? = "",
        @SerialName("items")
        val items: List<Item?>? = listOf(),
        @SerialName("updated_at")
        val updatedAt: String? = ""
    ) {
        @Serializable
        data class Item(
            @SerialName("balance")
            val balance: String? = "",
            @SerialName("balance_24h")
            val balance24h: String? = "",
            @SerialName("contract_address")
            val contractAddress: String? = "",
            @SerialName("contract_name")
            val contractName: String? = null,
            @SerialName("contract_ticker_symbol")
            val contractTickerSymbol: String? = null,
            @SerialName("is_spam")
            val isSpam: Boolean? = false,
            @SerialName("last_transfered_at")
            val lastTransferedAt: String? = "",
            @SerialName("nft_data")
            val nftData: List<NftData?>? = listOf(),
            @SerialName("supports_erc")
            val supportsErc: List<String?>? = listOf(),
            @SerialName("type")
            val type: String? = ""
        ) {
            @Serializable
            data class NftData(
                @SerialName("asset_cached")
                val assetCached: Boolean? = false,
                @SerialName("external_data")
                val externalData: ExternalData? = ExternalData(),
                @SerialName("image_cached")
                val imageCached: Boolean? = false,
                @SerialName("original_owner")
                val originalOwner: String? = "",
                @SerialName("token_id")
                val tokenId: String? = "",
                @SerialName("token_url")
                val tokenUrl: String? = ""
            ) {
                @Serializable
                data class ExternalData(
                    @SerialName("animation_url")
                    val animationUrl: String? = null,
                    @SerialName("asset_file_extension")
                    val assetFileExtension: String? = "",
                    @SerialName("asset_mime_type")
                    val assetMimeType: String? = "",
                    @SerialName("asset_size_bytes")
                    val assetSizeBytes: String? = "",
                    @SerialName("asset_url")
                    val assetUrl: String? = "",
                    @SerialName("attributes")
                    val attributes: List<Attribute>? = listOf(),
                    @SerialName("description")
                    val description: String? = "",
                    @SerialName("external_url")
                    val externalUrl: String? = null,
                    @SerialName("image")
                    val image: String? = "",
                    @SerialName("image_1024")
                    val image1024: String? = "",
                    @SerialName("image_256")
                    val image256: String? = "",
                    @SerialName("image_512")
                    val image512: String? = "",
                    @SerialName("name")
                    val name: String? = ""
                ) {
                    @Serializable
                    data class Attribute(
                        @SerialName("trait_type")
                        val traitType: String? = null,
                        @SerialName("value")
                        val value: String? = null
                    )
                }
            }
        }
    }
}