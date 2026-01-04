package com.mangala.wallet.features.chains.bitcoin.data.remote.fee.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MempoolRecommendedFeesResponse(
    // All fee rates are in sats/vByte
    @SerialName("fastestFee")
    val fastestFee: Int,
    @SerialName("halfHourFee")
    val halfHourFee: Int,
    @SerialName("hourFee")
    val hourFee: Int,
    @SerialName("economyFee")
    val economyFee: Int,
    @SerialName("minimumFee")
    val minimumFee: Int
)