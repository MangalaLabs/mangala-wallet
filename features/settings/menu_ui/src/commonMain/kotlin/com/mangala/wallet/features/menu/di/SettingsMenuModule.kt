package com.mangala.wallet.features.menu.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.menu.presentation.menu.MenuScreen
import com.mangala.wallet.features.menu.presentation.preferences.PreferencesScreen
import com.mangala.wallet.features.menu.presentation.preferences.PreferencesScreenModel
import com.mangala.wallet.menu_base.presentation.menu.BaseMenuScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsMenuModule = module {
    factoryOf(::PreferencesScreenModel)
}

val settingsMenuScreenModule = screenModule {
    register<SharedScreen.PreferencesScreen> { PreferencesScreen() }
    register<SharedScreen.MenuScreen> { MenuScreen() }
}