package com.mangala.browser.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.browser.presentation.ConfirmTransactionScreen
import com.mangala.browser.presentation.qr.TransactionQrScreenModel
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val browserBridgeModule = module {
    factory { (signTransactionRequest: SignTransactionRequest) -> TransactionQrScreenModel(signTransactionRequest, get()) }
}

val browserBridgeScreenModule = screenModule {
    register<SharedScreen.BrowserConfirmTransactionScreen> {
        ConfirmTransactionScreen(
            it.url,
            it.accountId,
            it.coinDecimals,
            it.chainId,
            it.callbackId,
            it.value,
            it.recipient,
            it.payload,
            it.nonce,
            it.isLegacyTransaction,
            it.onSignMessageFail,
            it.onSignMessageSuccessful,
            it.onConfirm,
            it.onDecline
        )
    }
}