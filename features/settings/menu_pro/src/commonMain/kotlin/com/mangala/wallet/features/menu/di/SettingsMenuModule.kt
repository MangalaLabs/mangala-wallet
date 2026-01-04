package com.mangala.wallet.features.menu.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.menu.presentation.menu.MenuScreen
import com.mangala.wallet.features.menu.presentation.preferences.PreferencesScreen
import com.mangala.wallet.features.menu.presentation.preferences.PreferencesScreenModel
import com.mangala.wallet.features.menu.presentation.wallet.WalletScreen
import com.mangala.wallet.features.menu.presentation.wallet.WalletScreenModel
import com.mangala.wallet.features.menu.presentation.wallet.add_wallet.AddWalletScreen
import com.mangala.wallet.features.menu.presentation.wallet.add_wallet.AddWalletScreenModel
import com.mangala.wallet.features.menu.presentation.wallet.details.WalletDetailsScreen
import com.mangala.wallet.features.menu.presentation.wallet.details.WalletDetailsScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsMenuModule = module {
    factoryOf(::PreferencesScreenModel)
    factoryOf(::AddWalletScreenModel)
    factoryOf(::WalletDetailsScreenModel)
    factoryOf(::WalletScreenModel)
}

val settingsMenuScreenModule = screenModule {
    register<SharedScreen.WalletScreen> {
        WalletScreen()
    }
    register<SharedScreen.WalletDetailsScreen> {
        WalletDetailsScreen(it.walletId)
    }
    register<SharedScreen.AddWalletScreen> { AddWalletScreen() }
    register<SharedScreen.PreferencesScreen> { PreferencesScreen() }
    register<SharedScreen.MenuScreen> { MenuScreen() }
}