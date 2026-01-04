package com.mangala.wallet.model.provider.moralis


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoralisBalanceResponse(
    @SerialName("block_number")
    val blockNumber: Int? = null,
    @SerialName("cursor")
    val cursor: String? = null,
    @SerialName("page")
    val page: Int? = null,
    @SerialName("page_size")
    val pageSize: Int? = null,
    @SerialName("result")
    val result: List<Result?>? = null
) {
    @Serializable
    data class Result(
        @SerialName("balance")
        val balance: String? = null,
        @SerialName("balance_formatted")
        val balanceFormatted: String? = null,
        @SerialName("decimals")
        val decimals: Long? = null,
        @SerialName("logo")
        val logo: String? = null,
        @SerialName("name")
        val name: String? = null,
        @SerialName("native_token")
        val nativeToken: Boolean? = null,
        @SerialName("percentage_relative_to_total_supply")
        val percentageRelativeToTotalSupply: Double? = null,
        @SerialName("portfolio_percentage")
        val portfolioPercentage: Double? = null,
        @SerialName("possible_spam")
        val possibleSpam: Boolean? = null,
        @SerialName("security_score")
        val securityScore: String? = null,
        @SerialName("symbol")
        val symbol: String? = null,
        @SerialName("thumbnail")
        val thumbnail: String? = null,
        @SerialName("token_address")
        val tokenAddress: String? = null,
        @SerialName("total_supply")
        val totalSupply: String? = null,
        @SerialName("total_supply_formatted")
        val totalSupplyFormatted: String? = null,
        @SerialName("usd_price")
        val usdPrice: Double? = null,
        @SerialName("usd_price_24hr_percent_change")
        val usdPrice24hrPercentChange: Double? = null,
        @SerialName("usd_price_24hr_usd_change")
        val usdPrice24hrUsdChange: Double? = null,
        @SerialName("usd_value")
        val usdValue: Double? = null,
        @SerialName("usd_value_24hr_usd_change")
        val usdValue24hrUsdChange: Double? = null,
        @SerialName("verified_contract")
        val verifiedContract: Boolean? = null
    )
}