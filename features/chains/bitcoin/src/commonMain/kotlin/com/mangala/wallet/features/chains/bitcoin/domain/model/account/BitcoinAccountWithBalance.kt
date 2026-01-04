package com.mangala.wallet.features.chains.bitcoin.domain.model.account

import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.model.token.domain.TokenBalanceModel

data class BitcoinAccountWithBalance(
    val account: BitcoinAccount,
    val balanceInSatoshis: Resource<TokenBalanceModel?>
)