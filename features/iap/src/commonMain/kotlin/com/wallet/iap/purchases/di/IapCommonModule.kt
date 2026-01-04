package com.wallet.iap.purchases.di

import com.wallet.iap.purchases.domain.usecases.GetPurchaseStatusUseCase
import org.koin.dsl.module

val iapCommonModule = module {
    factory { GetPurchaseStatusUseCase(get()) }
}