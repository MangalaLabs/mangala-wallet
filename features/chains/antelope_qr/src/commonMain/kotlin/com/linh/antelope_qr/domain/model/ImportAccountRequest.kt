package com.linh.antelope_qr.domain.model

import com.mangala.wallet.utils.ByteArrayAsBase64StringSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ImportAccountRequest(
    @Serializable(with = ByteArrayAsBase64StringSerializer::class)
    val publicKey: ByteArray,
    val accountName: String?,
    val permissionName: String?
)
