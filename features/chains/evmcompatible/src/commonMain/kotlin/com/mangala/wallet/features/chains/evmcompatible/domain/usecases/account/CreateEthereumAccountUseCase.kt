package com.mangala.wallet.features.chains.evmcompatible.domain.usecases.account

import com.mangala.wallet.domain.wallet.usecases.account.AccountCreator
import com.mangala.wallet.model.wallet.domain.WalletModel

class CreateEthereumAccountUseCase(
): AccountCreator {
    override suspend fun createAccount(
        accountId: String,
        derivationPathIndex: Int,
        wallet: WalletModel
    ) {

    }
}