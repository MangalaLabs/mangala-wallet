package com.mangala.wallet.walletconnect.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.walletconnect.WalletConnectScreen
import org.koin.dsl.module

val walletConnectModule = screenModule {
    register<SharedScreen.WalletConnectScreen> { provider ->
        WalletConnectScreen(provider.uri)
    }
}