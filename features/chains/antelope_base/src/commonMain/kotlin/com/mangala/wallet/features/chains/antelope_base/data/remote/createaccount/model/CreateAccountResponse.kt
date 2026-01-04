package com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountResponse(
    @SerialName("message") val message: String? = null,
    @SerialName("newAccountName") val newAccountName: String? = null,
    @SerialName("publicActiveKey") val publicActiveKey: String? = null,
    @SerialName("publicOwnerKey") val publicOwnerKey: String? = null,
)