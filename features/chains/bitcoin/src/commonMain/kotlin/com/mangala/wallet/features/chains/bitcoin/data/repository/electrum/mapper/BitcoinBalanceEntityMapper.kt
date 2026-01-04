package com.mangala.wallet.features.chains.bitcoin.data.repository.electrum.mapper

import com.mangala.wallet.features.chains.bitcoin.BitcoinBalanceEntity
import com.mangala.wallet.features.chains.bitcoin.domain.model.balance.BitcoinBalance

fun BitcoinBalanceEntity.toBitcoinBalance(): BitcoinBalance {
    return BitcoinBalance(
        confirmedSats = confirmed_balance,
        unconfirmedSats = unconfirmed_balance,
    )
}