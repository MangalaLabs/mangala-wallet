package com.mangala.wallet.features.wallet.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.wallet.domain.usecases.SyncAccountUseCase
import com.mangala.wallet.features.wallet.presentation.addaccount.CreateAccountScreen
import com.mangala.wallet.features.wallet.presentation.main.WalletMainScreen
import com.mangala.wallet.features.wallet.presentation.main.WalletMainScreenModel
import com.mangala.wallet.features.wallet.presentation.syncaccount.SyncAccountScreen
import com.mangala.wallet.features.wallet.presentation.syncaccount.SyncAccountScreenModel
import com.mangala.wallet.model.qr.SyncAccountRequest
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val walletModule = module {
    factoryOf(::WalletMainScreenModel)
    factoryOf(::SyncAccountUseCase)
    factory { (syncAccountRequest: SyncAccountRequest) ->
        SyncAccountScreenModel(
            syncAccountRequest = syncAccountRequest,
            syncAccountUseCase = get()
        )
    }
}

val walletScreenModule = screenModule {
    register<SharedScreen.EvmCreateAccountScreen> {
        CreateAccountScreen(it.isPinVerified)
    }

    register<SharedScreen.WalletMainScreen> {
        WalletMainScreen()
    }

    register<SharedScreen.UiWalletSyncAccountScreen> {
        SyncAccountScreen(it.syncAccountRequest)
    }
}