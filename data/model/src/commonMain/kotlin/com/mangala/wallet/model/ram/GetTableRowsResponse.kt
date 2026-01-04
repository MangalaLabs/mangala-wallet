package com.mangala.wallet.model.ram

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class GetTableRowsResponse(
    val rows: List<@Contextual Any>?
)