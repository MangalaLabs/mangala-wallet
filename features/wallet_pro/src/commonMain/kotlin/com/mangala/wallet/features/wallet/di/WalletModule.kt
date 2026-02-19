package com.mangala.wallet.features.wallet.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.wallet.presentation.addaccount.bitcoin.BitcoinCreateAccountScreen
import com.mangala.wallet.features.wallet.presentation.addaccount.bitcoin.BitcoinCreateAccountScreenModel
import com.mangala.wallet.features.wallet.presentation.addaccount.evm.AddAccountScreenModel
import com.mangala.wallet.features.wallet.presentation.addaccount.evm.AddAccountScreen
import com.mangala.wallet.features.wallet.presentation.main.WalletMainScreen
import com.mangala.wallet.features.wallet.presentation.main.WalletMainScreenModel
import com.mangala.features.wallet.presentationv2.antelope.AntelopeWalletScreenModel
import com.mangala.features.wallet.presentationv2.bitcoin.BitcoinWalletViewModel
import com.mangala.features.wallet.presentationv2.evm.EVMWalletScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val walletModule = module {
    factory {
        WalletMainScreenModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { AddAccountScreenModel(get(), get()) }
    factory { BitcoinCreateAccountScreenModel(get(), get()) }
    
    // Wallet V2 ViewModels
    factory { AntelopeWalletScreenModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { BitcoinWalletViewModel() }
    factoryOf(::EVMWalletScreenModel)
}

val walletScreenModule = screenModule {
    register<SharedScreen.EvmCreateAccountScreen> {
        AddAccountScreen(it.isPinVerified, it.walletId)
    }

    register<SharedScreen.WalletMainScreen> {
        WalletMainScreen()
    }

    register<SharedScreen.BitcoinCreateAccountScreen> {
        BitcoinCreateAccountScreen(it.isPinVerified)
    }
}
