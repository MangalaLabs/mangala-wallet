package com.mangala.wallet.core.hdwallet.domain.model

import fr.acinq.secp256k1.Secp256k1

data class HDKey(val privateKey: ByteArray, val chainCode: ByteArray, val accountIndex: Int) {
    val publicKey by lazy {
        Secp256k1.pubkeyCreate(privateKey)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as HDKey

        if (!privateKey.contentEquals(other.privateKey)) return false
        if (!chainCode.contentEquals(other.chainCode)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = privateKey.contentHashCode()
        result = 31 * result + chainCode.contentHashCode()
        return result
    }
}
