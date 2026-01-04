package com.mangala.wallet.features.swap_base.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.swap_base.presentation.SwapTokenScreen
import com.mangala.wallet.features.swap_base.presentation.SwapTokenScreenModel
import com.mangala.wallet.features.swap_base.presentation.preview.PreviewSwapTokenScreenModel
import com.mangala.wallet.features.swap_base.presentation.selecttoken.SelectTokenScreenModel
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.TradeData
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.dsl.module

val swapBaseModule = module {
    factory { (tradeDataFromSwapScreen: TradeData, accountAddress: String, accountId: String, dex: Dex) ->
        PreviewSwapTokenScreenModel(
            tradeDataFromSwapScreen = tradeDataFromSwapScreen,
            accountAddress = accountAddress,
            accountId = accountId,
            dex = dex,
            getSelectedNetworkUseCase = get(),
            getCurrentCurrencyCodeUseCase = get(),
            getSwapTradeDataUseCase = get(),
            getSwapTransactionDataUseCase = get(),
            getNativeCoinUseCase = get(),
            fetchTokenPriceUseCase = get(),
            getTransactionFeeOptionsUseCase = get(),
            getTokenBalanceByTokenIdUseCase = get(),
            getRecommendedGasPriceUseCase = get(),
            swapTokenUseCase = get(),
            estimateGasUseCase = get(),
            allowanceUseCase = get(),
            approveUseCase = get(),
            getLatestBlockUseCase = get(),
            getNonceUseCase = get(),
            getSelectedWalletUseCase = get()
        )
    }

    factory { (accountAddress: String, accountId: String, blockChainUid: String) ->
        SelectTokenScreenModel(
            accountAddress = accountAddress,
            accountId = accountId,
            blockChainUid = blockChainUid,
            getTokenByBlockchainUidUseCase = get(),
            getAccountBalanceUseCase = get(),
            getAllCoinUseCase = get()
        )
    }

    factory {
        SwapTokenScreenModel(
            getSelectedNetworkUseCase = get(),
            getTokenByBlockchainUidUseCase = get(),
            getAccountBalanceUseCase = get(),
            getWalletAccountsUseCase = get(),
            getSelectedWalletUseCase = get(),
            getSwapTradeDataUseCase = get(),
            getAllCoinUseCase = get()
        )
    }
}

val swapBaseScreenModule = screenModule {
    register<SharedScreen.SwapTokenScreen> {
        SwapTokenScreen()
    }.also {
        Napier.base(DebugAntilog())
    }
}