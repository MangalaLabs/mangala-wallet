package com.mangala.wallet.model.provider.covalenthq


import com.mangala.wallet.model.provider.BaseGetPaginatedTransactionsForAddressResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GetPaginatedCovalenthqTransactionsForAddressResponse(
    @SerialName("data")
    val `data`: Data? = Data(),
    @SerialName("error")
    val error: Boolean? = false,
    @SerialName("error_code")
    val errorCode: Int? = 0,
    @SerialName("error_message")
    val errorMessage: String? = null
): BaseGetPaginatedTransactionsForAddressResponse() {
    @Serializable
    data class Data(
        @SerialName("address")
        val address: String? = "",
        @SerialName("chain_id")
        val chainId: Int? = 0,
        @SerialName("chain_name")
        val chainName: String? = "",
        @SerialName("current_page")
        val currentPage: Int? = 0,
        @SerialName("items")
        val items: List<Item?>? = listOf(),
        @SerialName("links")
        val links: Links? = Links(),
        @SerialName("next_update_at")
        val nextUpdateAt: String? = "",
        @SerialName("quote_currency")
        val quoteCurrency: String? = "",
        @SerialName("updated_at")
        val updatedAt: String? = ""
    ) {
        @Serializable
        data class Item(
            @SerialName("block_height")
            val blockHeight: Int? = 0,
            @SerialName("block_signed_at")
            val blockSignedAt: String? = "",
            @SerialName("block_hash")
            val blockHash: String? = "",
            @SerialName("fees_paid")
            val feesPaid: String? = "",
            @SerialName("from_address")
            val fromAddress: String? = "",
            @SerialName("from_address_label")
            val fromAddressLabel: String? = null,
            @SerialName("gas_metadata")
            val gasMetadata: GasMetadata? = GasMetadata(),
            @SerialName("gas_offered")
            val gasOffered: Long? = 0,
            @SerialName("gas_price")
            val gasPrice: Long? = 0,
            @SerialName("gas_quote")
            val gasQuote: Double? = null,
            @SerialName("gas_quote_rate")
            val gasQuoteRate: Double? = null,
            @SerialName("explorers")
            val explorers: List<Explorers>? = null,
            @SerialName("gas_spent")
            val gasSpent: Long? = 0,
            @SerialName("log_events")
            val logEvents: List<LogEvent?>? = listOf(),
            @SerialName("miner_address")
            val minerAddress: String? = "",
            @SerialName("pretty_gas_quote")
            val prettyGasQuote: String? = null,
            @SerialName("pretty_value_quote")
            val prettyValueQuote: String? = null,
            @SerialName("successful")
            val successful: Boolean? = false,
            @SerialName("to_address")
            val toAddress: String? = "",
            @SerialName("to_address_label")
            val toAddressLabel: String? = null,
            @SerialName("tx_hash")
            val txHash: String? = "",
            @SerialName("tx_offset")
            val txOffset: Int? = 0,
            @SerialName("value")
            val value: String? = "",
            @SerialName("value_quote")
            val valueQuote: Double? = null
        ) {
            @Serializable
            data class GasMetadata(
                @SerialName("contract_address")
                val contractAddress: String? = "",
                @SerialName("contract_decimals")
                val contractDecimals: Int? = 0,
                @SerialName("contract_name")
                val contractName: String? = "",
                @SerialName("contract_ticker_symbol")
                val contractTickerSymbol: String? = "",
                @SerialName("logo_url")
                val logoUrl: String? = "",
                @SerialName("supports_erc")
                val supportsErc: List<JsonElement>? = null
            )

            @Serializable
            data class LogEvent(
                @SerialName("block_height")
                val blockHeight: Int? = 0,
                @SerialName("block_signed_at")
                val blockSignedAt: String? = "",
                @SerialName("decoded")
                val decoded: Decoded? = Decoded(),
                @SerialName("log_offset")
                val logOffset: Int? = 0,
                @SerialName("raw_log_data")
                val rawLogData: String? = "",
                @SerialName("raw_log_topics")
                val rawLogTopics: List<String?>? = listOf(),
                @SerialName("sender_address")
                val senderAddress: String? = "",
                @SerialName("sender_address_label")
                val senderAddressLabel: String? = "",
                @SerialName("sender_contract_decimals")
                val senderContractDecimals: Int? = 0,
                @SerialName("sender_contract_ticker_symbol")
                val senderContractTickerSymbol: String? = "",
                @SerialName("sender_logo_url")
                val senderLogoUrl: String? = "",
                @SerialName("supports_erc")
                val supportsErc: List<String>? = null,
                @SerialName("sender_factory_address")
                val senderFactoryAddress: String? = null,
                @SerialName("sender_name")
                val senderName: String? = "",
                @SerialName("tx_hash")
                val txHash: String? = "",
                @SerialName("tx_offset")
                val txOffset: Int? = 0
            ) {
                @Serializable
                data class Decoded(
                    @SerialName("name")
                    val name: String? = "",
                    @SerialName("params")
                    val params: List<Param?>? = listOf(),
                    @SerialName("signature")
                    val signature: String? = ""
                ) {
                    @Serializable
                    data class Param(
                        @SerialName("decoded")
                        val decoded: Boolean? = false,
                        @SerialName("indexed")
                        val indexed: Boolean? = false,
                        @SerialName("name")
                        val name: String? = "",
                        @SerialName("type")
                        val type: String? = "",
                        @SerialName("value")
                        val value: JsonElement? = null // can be a String, or an array if the data is an array
                    )
                }
            }
        }

        @Serializable
        data class Links(
            @SerialName("next")
            val next: String? = "",
            @SerialName("prev")
            val prev: String? = ""
        )

        @Serializable
        data class Explorers(
            @SerialName("label")
            val label: JsonElement? = null,
            @SerialName("url")
            val url: String? = ""
        )
    }
}