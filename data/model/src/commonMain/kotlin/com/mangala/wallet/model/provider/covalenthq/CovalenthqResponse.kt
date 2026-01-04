package com.mangala.wallet.model.provider.covalenthq

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CovalenthqResponse(
    @SerialName("data")
    val data: Data?,
    @SerialName("error")
    val error: Boolean?,
    @SerialName("error_message")
    val errorMessage: String?,
    @SerialName("error_code")
    val errorCode: Int?
){
    @Serializable
    data class Data(
        @SerialName("address")
        val address: String?,
        @SerialName("updated_at")
        val updatedAt: String?,
        @SerialName("next_update_at")
        val nextUpdateAt: String?,
        @SerialName("quote_currency")
        val quoteCurrency: String?,
        @SerialName("chain_id")
        val chainId: Int?,
        @SerialName("chain_name")
        val chainName: String?,
        @SerialName("items")
        val items: List<Item>?,
//    @SerialName("pagination")
//    val pagination: Any?
    ){
        @Serializable
        data class Item(
            @SerialName("contract_decimals")
            val contractDecimals: Long?,
            @SerialName("contract_name")
            val contractName: String?,
            @SerialName("contract_ticker_symbol")
            val contractTickerSymbol: String?,
            @SerialName("contract_address")
            val contractAddress: String?,
            @SerialName("supports_erc")
            val supportsErc: List<String>?,
            @SerialName("logo_url")
            val logoUrl: String?,
            @SerialName("last_transferred_at")
            val lastTransferredAt: String?,
            @SerialName("native_token")
            val nativeToken: Boolean?,
            @SerialName("type")
            val type: String?,
            @SerialName("balance")
            val balance: String?,
            @SerialName("balance_24h")
            val balance24h: String?,
            @SerialName("quote_rate")
            val quoteRate: Double?,
            @SerialName("quote_rate_24h")
            val quoteRate24h: Double?,
            @SerialName("quote")
            val quote: Double?,
            @SerialName("quote_24h")
            val quote24h: Double?,
//    @SerialName("nft_data")
//    val nftData: Any?
        )
    }
}



