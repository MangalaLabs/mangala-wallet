package com.mangala.wallet.model.token

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenEntity(
    val id: Long,
    @SerialName("coin_uid")
    val coinUid: String,
    @SerialName("blockchain_uid")
    val blockchainUid: String,
    val type: String,
    val decimals: Long?,
    val reference: String?
)
