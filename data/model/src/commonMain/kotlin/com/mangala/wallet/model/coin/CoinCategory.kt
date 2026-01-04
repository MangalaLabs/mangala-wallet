package com.mangala.wallet.model.coin
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.SerialName

data class CoinCategory(
    val uid: String,
    val name: String,
    val description: Map<String, String>,
    @SerialName("market_cap")
    val marketCap: BigDecimal,
    @SerialName("change_24h")
    val diff24H: BigDecimal?,
    @SerialName("change_1w")
    val diff1W: BigDecimal?,
    @SerialName("change_1m")
    val diff1M: BigDecimal?,
) {

    override fun toString(): String {
        return "CoinCategory [uid: $uid; name: $name; descriptionCount: ${description.size}]"
    }

}
