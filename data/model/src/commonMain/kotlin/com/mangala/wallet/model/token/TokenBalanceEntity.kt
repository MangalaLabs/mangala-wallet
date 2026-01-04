package com.mangala.wallet.model.token

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TokenBalanceEntity(
    @SerialName("token_id")
    val tokenId: Long,
    @SerialName("account_id")
    val accountId: String,
    @SerialName("blockchain_uid")
    val blockchainUid: String,
    val balance: String,
    @SerialName("balance_24h")
    val balance24h: String,
    @SerialName("balance_locked")
    val balanceLocked: String,
    @SerialName("order_number")
    val orderNumber: Int,
    @SerialName("contract_decimals")
    val contractDecimals: Long,
    @SerialName("contract_name")
    val contractName: String,
    @SerialName("contract_symbol")
    val contractSymbol: String,
    @SerialName("contract_address")
    val contractAddress: String,
    @SerialName("logo_url")
    val logoUrl: String,
    @SerialName("last_updated")
    val lastUpdated: Long
) {
    val isCoin = contractAddress == "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
}