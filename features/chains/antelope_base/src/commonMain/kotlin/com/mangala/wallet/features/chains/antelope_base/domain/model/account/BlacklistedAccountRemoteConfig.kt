package com.mangala.wallet.features.chains.antelope_base.domain.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlacklistedAccountRemoteConfig(
    @SerialName("data")
    val data: List<String>
)