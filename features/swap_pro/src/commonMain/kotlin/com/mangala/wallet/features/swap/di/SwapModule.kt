package com.mangala.wallet.features.swap.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.swap.presentation.PreviewSwapTokenScreen
import com.mangala.wallet.features.swap_base.presentation.preview.getTradeData
import com.mangala.wallet.model.token.Token
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.Fraction
import com.mangala.wallet.uniswap.domain.models.Route
import com.mangala.wallet.uniswap.domain.models.TokenAmount
import com.mangala.wallet.uniswap.domain.models.Trade
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.uniswap.domain.models.TradeOptions
import com.mangala.wallet.uniswap.domain.models.TradeType
import org.koin.dsl.module

val swapModule = module {
}

val swapScreenModule = screenModule {
    register<SharedScreen.PreviewSwapTokenScreen>() {
        with(it.args) {
            PreviewSwapTokenScreen(
                accountAddress = accountAddress,
                accountName = accountName,
                accountId = accountId,
                tokenFromSymbol = tokenFromSymbol,
                tokenFromLogoUrl = tokenFromLogoUrl,
                tokenToSymbol = tokenToSymbol,
                tokenToLogoUrl = tokenToLogoUrl,
                tradeData = getTradeData(),
                dex = Dex.fromName(dex)
            )
        }
    }
}