package com.mangala.wallet.features.chains.antelope.domain.usecase.account

import com.mangala.wallet.domain.portfolio.model.InitialWallet
import com.mangala.wallet.domain.portfolio.usecases.PortfolioWalletProvider
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase

class AntelopePortfolioWalletProvider(
    private val getAccountsUseCase: GetAccountsUseCase
): PortfolioWalletProvider {
    override suspend fun provide(): List<InitialWallet> {
        val accounts = getAccountsUseCase(
            includeTempAccounts = false
        )

        return accounts.map {
            InitialWallet(
                address = it.accountName,
                label = it.accountName
            )
        }
    }
}