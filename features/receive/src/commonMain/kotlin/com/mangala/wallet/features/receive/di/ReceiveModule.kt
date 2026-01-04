package com.mangala.wallet.features.receive.di

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.receive.presentation.AddAmountToReceiveQrScreenModel
import com.mangala.wallet.features.receive.presentation.ReceiveScreenProviderImpl
import com.mangala.wallet.features.receive.presentation.ReceiveTokenScreen
import com.mangala.wallet.features.receive.presentation.ReceiveTokenScreenModel
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.scanqr.ReceiveScreenProvider
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val receiveModule = module {
    factory<ReceiveScreenProvider> { ReceiveScreenProviderImpl() }

    factory { (accountId: String, address: String, networkType: NetworkType, initialBlockchainUid: String?) ->
        ReceiveTokenScreenModel(
            accountId = accountId,
            address = address,
            networkType = networkType,
            initialBlockchainUid = initialBlockchainUid,
            getAccountByIdUseCase = get(),
            getAccountsUseCase = get(),
            buildEnvironmentProvider = get(),
            getSelectedNetworkUseCase = get(),
            getNativeCoinUseCase = get(),
        )
    }

    factory { (initialAmount: String, decimal: Long?) ->
        AddAmountToReceiveQrScreenModel(
            initialAmount = initialAmount,
            decimal = decimal,
        )
    }
}

val receiveScreenModule = screenModule {
    ScreenRegistry.register<SharedScreen.ReceiveTokenScreen> { provider ->
        ReceiveTokenScreen(provider.accountId, provider.address, provider.networkType, provider.initialBlockchainUid)
    }
}