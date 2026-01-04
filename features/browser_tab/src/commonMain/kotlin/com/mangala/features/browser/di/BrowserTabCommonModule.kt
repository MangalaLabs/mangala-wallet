package com.mangala.features.browser.di

import com.mangala.features.browser.BrowserTabScreenModel
import org.koin.dsl.module

fun browserTabCommonModule() = module {
    factory {
        BrowserTabScreenModel(
            walletRepository = get(),
            getSelectedWalletAccountsUseCase = get(),
            getListOfCategoriesUseCase = get(),
            getDAppsByCategoriesUseCase = get(),
            getListDAppUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getDappsJson = get(),
            buildEnvironmentProvider = get()
        )
    }
}