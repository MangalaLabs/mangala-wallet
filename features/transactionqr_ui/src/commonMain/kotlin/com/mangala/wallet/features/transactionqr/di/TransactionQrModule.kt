package com.mangala.wallet.features.transactionqr.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.transactionqr.presentation.TransactionQrScreen
import com.mangala.wallet.features.transactionqr.presentation.TransactionQrScreenModel
import com.mangala.wallet.features.transactionqr.presentation.toSignTransactionRequest
import com.mangala.wallet.features.transactionqr.presentation.toSignedTransactionResponseArgs
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val transactionQrModule = module {
    factory { (signTransactionRequest: SignTransactionRequest) -> TransactionQrScreenModel(signTransactionRequest, get(), get()) }
}

val transactionQrScreenModule = screenModule {
    register<SharedScreen.TransactionQrScreen> { provider ->
        TransactionQrScreen(
            signTransactionRequest = provider.signTransactionRequestArgs.toSignTransactionRequest(),
            onScannedSignedTransaction = { v, r, s ->
                provider.onScannedSignedTransaction(v, r, s)
            },
            onDispose = {
                provider.onDispose()
            }
        )
    }
}