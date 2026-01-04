package com.mangala.wallet.features.chains.antelope.create_account.presentation.ui

import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAccountWithInAppPurchaseUseCase
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.StringResource

fun Throwable?.mapToErrorMessageStringResource(): StringResource {
    return if (this is CreateAccountWithInAppPurchaseUseCase.CreateAccountError) {
        when (this) {
            CreateAccountWithInAppPurchaseUseCase.CreateAccountError.AntelopeNodeError -> {
                MR.strings.message_antelope_create_account_payment_unknown_error
            }

            CreateAccountWithInAppPurchaseUseCase.CreateAccountError.NetworkError -> {
                MR.strings.message_antelope_create_account_payment_network_error
            }

            CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseAlreadyConsumed -> {
                MR.strings.message_antelope_create_account_payment_consumed
            }

            CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseCancelled -> {
                MR.strings.message_antelope_create_account_payment_cancelled
            }

            CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchasePending -> {
                MR.strings.message_antelope_create_account_payment_pending_error
            }

            CreateAccountWithInAppPurchaseUseCase.CreateAccountError.UnknownError -> {
                MR.strings.message_antelope_create_account_payment_unknown_error
            }
        }
    } else {
        MR.strings.message_antelope_create_account_payment_unknown_error
    }
}