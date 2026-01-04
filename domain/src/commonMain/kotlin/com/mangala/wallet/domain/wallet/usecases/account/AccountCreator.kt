package com.mangala.wallet.domain.wallet.usecases.account

import com.mangala.wallet.model.wallet.domain.WalletModel

interface AccountCreator {
    suspend fun createAccount(
        accountId: String,
        derivationPathIndex: Int,
        wallet: WalletModel,
    )
}