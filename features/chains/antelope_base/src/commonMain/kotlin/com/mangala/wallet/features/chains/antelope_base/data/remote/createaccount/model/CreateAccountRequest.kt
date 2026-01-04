package com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequest(
    @SerialName("newAccountName")
    val newAccountName: String,
    @SerialName("newPublicAccountActiveKey")
    val newPublicAccountActiveKey: String,
    @SerialName("newPublicAccountOwnerKey")
    val newPublicAccountOwnerKey: String,
    @SerialName("token")
    val token: String
)