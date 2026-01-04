package com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequestIos(
    val token: String,
    val newAccountName: String,
    val newPublicAccountActiveKey: String,
    val newPublicAccountOwnerKey: String,
    val iapPlatform: Int
)