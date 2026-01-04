package com.mangala.wallet.menu_base.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.menu_base.presentation.aboutus.AboutUsScreen
import com.mangala.wallet.menu_base.presentation.aboutus.AboutUsScreenModel
import com.mangala.wallet.menu_base.presentation.connectwithus.ConnectWithUsScreen
import com.mangala.wallet.menu_base.presentation.connectwithus.ConnectWithUsScreenModel
import com.mangala.wallet.menu_base.presentation.dev.DevMenuScreen
import com.mangala.wallet.menu_base.presentation.dev.DevMenuScreenModel
import com.mangala.wallet.menu_base.presentation.helpcenter.HelpCenterScreen
import com.mangala.wallet.menu_base.presentation.helpcenter.HelpCenterScreenModel
import com.mangala.wallet.menu_base.presentation.iconsinapp.IconsInAppScreen
import com.mangala.wallet.menu_base.presentation.language.LanguageScreen
import com.mangala.wallet.menu_base.presentation.language.LanguageScreenModel
import com.mangala.wallet.menu_base.presentation.menu.BaseMenuScreenModel
import com.mangala.wallet.menu_base.presentation.notifications.NotificationsScreen
import com.mangala.wallet.menu_base.presentation.notifications.NotificationsScreenModel
import com.mangala.wallet.menu_base.presentation.security.SecurityScreen
import com.mangala.wallet.menu_base.presentation.security.SecurityScreenScreenModel
import com.mangala.wallet.menu_base.presentation.shareapp.ShareAppScreen
import com.mangala.wallet.menu_base.presentation.shareapp.ShareAppScreenModel
import com.mangala.wallet.menu_base.presentation.theme.ThemeScreen
import com.mangala.wallet.menu_base.presentation.theme.ThemeScreenModel
import com.mangala.wallet.menu_base.presentation.wallet.WalletScreen
import com.mangala.wallet.menu_base.presentation.wallet.WalletScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val menuBaseModule = module {
    factoryOf(::BaseMenuScreenModel)
    factoryOf(::LanguageScreenModel)
    factoryOf(::DevMenuScreenModel)
    factoryOf(::WalletScreenModel)
    factoryOf(::ThemeScreenModel)
    factoryOf(::ShareAppScreenModel)
    factoryOf(::SecurityScreenScreenModel)
    factoryOf(::NotificationsScreenModel)
    factoryOf(::HelpCenterScreenModel)
    factoryOf(::ConnectWithUsScreenModel)
    factoryOf(::AboutUsScreenModel)
}

val menuBaseScreenModule = screenModule {
    register<SharedScreen.ConnectWithUsScreen> { ConnectWithUsScreen() }
    register<SharedScreen.IconsInAppScreen> { IconsInAppScreen() }
    register<SharedScreen.AboutUsScreen> { AboutUsScreen() }
    register<SharedScreen.ShareAppScreen> { ShareAppScreen() }
    register<SharedScreen.HelpCenterScreen> { HelpCenterScreen() }
    register<SharedScreen.SecurityScreen> { SecurityScreen() }
    register<SharedScreen.NotificationsScreen> { NotificationsScreen() }
    register<SharedScreen.LanguageScreen> { LanguageScreen() }
    register<SharedScreen.ThemeScreen> { ThemeScreen() }
    register<SharedScreen.WalletScreen> { WalletScreen() }
    register<SharedScreen.DevMenuScreen> { DevMenuScreen() }
}