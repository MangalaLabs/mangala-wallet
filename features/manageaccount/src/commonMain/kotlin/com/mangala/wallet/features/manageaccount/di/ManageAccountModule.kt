package com.mangala.wallet.features.manageaccount.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.manageaccount.presentation.ManageAccountsScreen
import com.mangala.wallet.features.manageaccount.presentation.ManageAccountsScreenModel
import com.mangala.wallet.features.manageaccount.presentation.accountdetail.AccountDetailsScreen
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val manageAccountModule = module {
    factory {
        ManageAccountsScreenModel(
            getSelectedWalletAccountsUseCase = get(),
            updateAccountsUseCase = get(),
            getAccountBalanceUseCase = get()
        )
    }
}

val manageAccountScreenModule = screenModule {
    register<SharedScreen.ManageAccountsScreen> {
        ManageAccountsScreen()
    }

    register<SharedScreen.AccountDetailsScreen> {
        AccountDetailsScreen(it.accountId)
    }
}