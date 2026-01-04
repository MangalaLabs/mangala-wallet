package com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount

import com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.model.CreateAccountResponse
import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountResponseIos(
    val message: String? = null,
    val accountName: String? = null,
    val activePublicKey: String? = null,
    val ownerPublicKey: String? = null
)

fun CreateAccountResponseIos.toCreateAccountResponse() = CreateAccountResponse(message, accountName, activePublicKey, ownerPublicKey)