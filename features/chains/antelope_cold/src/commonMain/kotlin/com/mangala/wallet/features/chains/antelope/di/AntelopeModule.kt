package com.mangala.wallet.features.chains.antelope.di

import com.mangala.wallet.features.chains.antelope.presentation.createkeypair.CreateKeyPairScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.importaccount.ImportAccountScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.signtransaction.SignTransactionScreenModel
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import org.koin.dsl.module

val antelopeModule = module {
    factory { CreateKeyPairScreenModel(get(), get(), get(), get(), get()) }
    factory { ImportAccountScreenModel(get(), get()) }
    factory { (signTransactionRequest: SignTransactionRequest) ->
        SignTransactionScreenModel(
            signTransactionRequest,
            get()
        )
    }
}