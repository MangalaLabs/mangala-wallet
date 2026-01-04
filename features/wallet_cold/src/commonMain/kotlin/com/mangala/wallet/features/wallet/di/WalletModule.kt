package com.mangala.wallet.features.wallet.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.wallet.domain.usecases.GetSyncAccountQrUseCase
import com.mangala.wallet.features.wallet.presentation.addaccount.CreateAccountScreen
import com.mangala.wallet.features.wallet.presentation.confirmqr.ConfirmQrScreen
import com.mangala.wallet.features.wallet.presentation.confirmqr.ConfirmQrScreenModel
import com.mangala.wallet.features.wallet.presentation.main.WalletMainScreen
import com.mangala.wallet.features.wallet.presentation.main.WalletMainScreenModel
import com.mangala.wallet.features.wallet.presentation.signedtransactionqr.SignedTransactionQrScreen
import com.mangala.wallet.features.wallet.presentation.signedtransactionqr.SignedTransactionQrScreenModel
import com.mangala.wallet.features.wallet.presentation.syncaccount.SyncAccountScreen
import com.mangala.wallet.features.wallet.presentation.syncaccount.SyncAccountScreenModel
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.utils.di.JSON
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val walletModule = module {
    factory { GetSyncAccountQrUseCase(get(), get(), get(named(JSON))) }

    factoryOf(::WalletMainScreenModel)
    factory { (accountId: String) ->
        SyncAccountScreenModel(
            accountId = accountId,
            getSyncAccountQrUseCase = get()
        )
    }
    factory { (signTransactionRequest: SignTransactionRequest) ->
        SignedTransactionQrScreenModel(
            request = signTransactionRequest,
            signAndEncodeTransactionRequestUseCase = get()
        )
    }
    factory { (qrCode: String) ->
        ConfirmQrScreenModel(
            qrCode = qrCode
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

    register<SharedScreen.ColdWalletSyncAccountScreen> {
        SyncAccountScreen(it.accountId)
    }

    register<SharedScreen.SignedTransactionQrScreen> {
        SignedTransactionQrScreen(
            requestId = it.requestId,
            walletId = it.walletId,
            accountId = it.accountId,
            nonce = it.nonce,
            blockchainUid = it.blockchainUid,
            fromAddress = it.fromAddress,
            toAddress = it.toAddress,
            value = it.value,
            input = it.input,
            legacyGasPrice = it.legacyGasPrice,
            maxFeePerGas = it.maxFeePerGas,
            maxPriorityFeePerGas = it.maxPriorityFeePerGas,
            baseFee = it.baseFee,
            gasLimit = it.gasLimit,
            gasFiatValue = it.gasFiatValue,
            contactName = it.contactName,
            contactAddress = it.contactAddress,
            transactionType = it.transactionType
        )
    }

    register<SharedScreen.ConfirmQrScreen> {
        ConfirmQrScreen(it.qrCode)
    }
}