package com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto

class TokenSupported(val isSupported: Boolean, val index: Int, val name: String) {
    override fun toString(): String =
        "TokenSupported: isSupported: $isSupported, index: $index, name: $name"
}