package com.mangala.wallet.features.home.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.home.presentation.HomeScreen
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val homeModule = module {
}

val homeScreenModule = screenModule {
    register<SharedScreen.HomeScreen> { HomeScreen() }
}