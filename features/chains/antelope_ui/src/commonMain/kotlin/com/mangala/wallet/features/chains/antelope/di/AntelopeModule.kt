package com.mangala.wallet.features.chains.antelope.di

import com.mangala.wallet.features.chains.antelope.domain.usecase.PushSignedTransactionUseCase
import com.mangala.wallet.features.chains.antelope.presentation.createaccount.CreateAccountScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.importaccount.ImportAccountScreenModel
import org.koin.dsl.module

val antelopeModule = module {
    factory { (ownerPublicKey: ByteArray, activePublicKey: ByteArray) ->
        CreateAccountScreenModel(
            ownerPublicKeyBytes = ownerPublicKey,
            activePublicKeyBytes = activePublicKey,
            validateAccountUseCase = get(),
            generateCreateAccountSignRequestUseCase = get(),
            createAccountWithInAppPurchaseUseCase = get(),
            generateRandomAccountNameUseCase = get(),
            encodeSyncAccountRequestUseCase = get(),
            saveAccountUseCase = get(),
            getAccountsUseCase = get(),
            getAccountPermissionUseCase = get(),
            encodeRequestToQrCodeUseCase = get(),
            pushSignedTransactionUseCase = get()
        )
    }
    factory { PushSignedTransactionUseCase(get()) }
    factory {
        ImportAccountScreenModel(get(), get())
    }
}