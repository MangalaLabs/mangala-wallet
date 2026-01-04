package com.linh.antelope_qr.domain.model

import com.mangala.wallet.utils.ByteArrayAsBase64StringSerializer
import kotlinx.serialization.Serializable

@Serializable
data class SyncAccountRequest(
    @Serializable(with = ByteArrayAsBase64StringSerializer::class)
    val ownerPublicKey: ByteArray,
    @Serializable(with = ByteArrayAsBase64StringSerializer::class)
    val activePublicKey: ByteArray,
    val accountName: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SyncAccountRequest

        if (!ownerPublicKey.contentEquals(other.ownerPublicKey)) return false
        if (!activePublicKey.contentEquals(other.activePublicKey)) return false
        if (accountName != other.accountName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ownerPublicKey.contentHashCode()
        result = 31 * result + activePublicKey.contentHashCode()
        result = 31 * result + accountName.hashCode()
        return result
    }
}