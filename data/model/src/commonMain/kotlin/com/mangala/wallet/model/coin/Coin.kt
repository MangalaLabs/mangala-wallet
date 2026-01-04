package com.mangala.wallet.model.coin

import kotlinx.serialization.Serializable

@Serializable
data class Coin(
    val uid: String,
    val name: String,
    val code: String,
    val marketCapRank: Long? = null,
    val coinGeckoId: String? = null
) {
    override fun equals(other: Any?): Boolean {
        return other is Coin && other.uid == uid
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    override fun toString(): String {
        return "Coin [uid: $uid; name: $name; code: $code; marketCapRank: $marketCapRank; coinGeckoId: $coinGeckoId]"
    }
}
