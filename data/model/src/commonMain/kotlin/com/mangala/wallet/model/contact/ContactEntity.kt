package com.mangala.wallet.model.contact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactEntity(
    val id: Long,
    val name: String,
    @SerialName("blockchain_uid")
    val blockchainUid: String,
    val address: String,
)