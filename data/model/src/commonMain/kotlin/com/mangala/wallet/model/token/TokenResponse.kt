package com.mangala.wallet.model.token

data class TokenResponse(
    val coin_uid: String,
    val blockchain_uid: String,
    val type: String,
    val decimals: Int?,
    val address: String?,
    val symbol: String?
)
