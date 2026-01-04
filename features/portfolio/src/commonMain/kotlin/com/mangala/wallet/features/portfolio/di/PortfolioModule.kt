package com.mangala.wallet.features.portfolio.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.portfolio.presentation.PortfolioScreen
import com.mangala.wallet.features.portfolio.presentation.PortfolioScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val portfolioModule = module {
    factory { (accountId: String, networkType: String, address: String, initialAccountName: String) ->
        PortfolioScreenModel(
            accountId = accountId,
            networkType = networkType,
            address = address,
            initialAccountName = initialAccountName,
            getCurrentCurrencyCodeUseCase = get(),
            getBalanceVisibleStatusUseCase = get(),
            saveBalanceVisibleStatusUseCase = get(),
            getAccountBalancesInEvmAccountUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getAntelopeAccountBalanceUseCase = get(),
            getAccountBalancesInBitcoinAccountUseCase = get()
        )
    }
}
val portfolioScreenModule = screenModule {
    register<SharedScreen.PortfolioScreen> {
        PortfolioScreen(it.accountId, it.address, it.networkType, it.accountName)
    }
}