package com.mangala.wallet.features.menu.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.menu.presentation.menu.MenuScreen
import com.mangala.wallet.features.menu.presentation.preferences.PreferencesScreen
import com.mangala.wallet.features.menu.presentation.preferences.PreferencesScreenModel
import com.mangala.wallet.features.menu.presentation.wallet.add_wallet.AddWalletScreen
import com.mangala.wallet.features.menu.presentation.wallet.add_wallet.AddWalletScreenModel
import com.mangala.wallet.features.menu.presentation.wallet.details.WalletDetailsScreen
import com.mangala.wallet.features.menu.presentation.wallet.details.WalletDetailsScreenModel
import com.mangala.wallet.menu_base.presentation.menu.BaseMenuScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsMenuModule = module {
    factoryOf(::PreferencesScreenModel)
    factoryOf(::AddWalletScreenModel)
    factoryOf(::WalletDetailsScreenModel)
}

val settingsMenuScreenModule = screenModule {
    register<SharedScreen.MenuScreen> {
        MenuScreen()
    }

    register<SharedScreen.PreferencesScreen> {
        PreferencesScreen()
    }

    register<SharedScreen.AddWalletScreen> {
        AddWalletScreen()
    }

    register<SharedScreen.WalletDetailsScreen> {
        WalletDetailsScreen(it.walletId)
    }
}