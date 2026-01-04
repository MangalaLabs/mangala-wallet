package com.linh.antelope_qr.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountForFriendRequest(
    val accountName: String,
    val activePublicKey: String,
    val ownerPublicKey: String,
    val blockchainUid: String
)