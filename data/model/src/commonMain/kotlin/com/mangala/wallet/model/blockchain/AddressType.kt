package com.mangala.wallet.model.blockchain

sealed class AddressType(val derivationPathPurpose: Int) {

    // aka Legacy Address
    data object Bip44 : AddressType(derivationPathPurpose = 44)

    // aka Segwit
    data object Bip49 : AddressType(derivationPathPurpose = 49)

    // aka Bech32
    data object Bip84 : AddressType(derivationPathPurpose = 84)

    val uid: String
        get() = when (this) {
            is Bip44 -> "legacy"
            is Bip49 -> "segwit"
            is Bip84 -> "bech32"
        }

    companion object {
        fun fromUid(uid: String): AddressType {
            return when (uid) {
                "legacy" -> Bip44
                "segwit" -> Bip49
                "bech32" -> Bip84
                else -> throw IllegalArgumentException("Unknown address type uid: $uid")
            }
        }
    }
}