package com.mangala.wallet.features.chains.bitcoin.domain.model.balance

data class BitcoinBalance(
    val confirmedSats: Long,
    val unconfirmedSats: Long,
)