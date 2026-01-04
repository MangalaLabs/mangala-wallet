package com.mangala.wallet.features.settings.currency.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.settings.currency.presentation.CurrencyScreen
import com.mangala.wallet.features.settings.currency.presentation.CurrencyScreenModel
import com.mangala.wallet.features.settings.currency.presentation.CurrencyScreenUiModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val currencyModule = module {
    factory { (currenciesSupported: List<CurrencyScreenUiModel>) ->
        CurrencyScreenModel(currenciesSupported, get(), get())
    }
}

val currencyScreenModule = screenModule {
    register<SharedScreen.CurrencyScreen> { CurrencyScreen() }
}