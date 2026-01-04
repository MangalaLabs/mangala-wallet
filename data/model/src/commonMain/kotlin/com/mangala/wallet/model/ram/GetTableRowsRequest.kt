package com.mangala.wallet.model.ram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetTableRowsRequest(
    val code: String,
    val scope: String,
    val table: String,
    @SerialName("lower_bound") val lowerBound: String,
    @SerialName("upper_bound") val upperBound: String,
    @SerialName("encode_type") val encodeType: String,
    val limit: Int = 10,
    @SerialName("key_type") val keyType: String,
    @SerialName("index_position") val indexPosition: String,
    val reverse: Boolean = false,
    @SerialName("show_payer") val showPayer: Boolean = false,
)

