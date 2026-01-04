package com.mangala.browser_bridge_base.di

import com.mangala.browser_bridge_base.ConfirmTransactionViewModel
import com.mangala.browser_bridge_base.switchchain.SwitchChainScreenModel
import com.mangala.wallet.utils.di.IGNORE_UNKNOWN_KEY_JSON
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val browserBridgeBaseModule = module {
    factory { SwitchChainScreenModel(get(), get(), get()) }
    factory {
        ConfirmTransactionViewModel(
            getCurrentCurrencyCodeUseCase = get(),
            getAccountByIdUseCase = get(),
            getFeeHistoryUseCase = get(),
            signTransactionUseCase = get(),
            estimateGasUseCase = get(),
            generateHDKeyUseCase = get(),
            deriveAddressUseCase = get(),
            getNativeCoinUseCase = get(),
            getTokenBalanceByTokenIdUseCase = get(),
            fetchTokenPriceUseCase = get(),
            getRecommendedGasPriceUseCase = get(),
            getTransactionFeeOptionsUseCase = get(),
            signPersonalMessageUseCase = get(),
            getSelectedWalletAccountsUseCase = get(),
            getSelectedWalletUseCase = get(),
            getLatestBlockUseCase = get(),
            json = get(named(IGNORE_UNKNOWN_KEY_JSON)),
            getSelectedNetworkUseCase = get(),
            getNonceUseCase = get()
        )
    }
}

expect fun browserBridgePlatformSpecificModule(): Module