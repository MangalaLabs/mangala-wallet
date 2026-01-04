package com.wallet.iap.purchases.di

import com.wallet.iap.purchases.OpenIapScreen
import com.wallet.iap.purchases.device.IAPManager
import com.wallet.iap.purchases.device.PurchaseManager
import org.koin.core.module.Module
import org.koin.dsl.module

public actual fun openIapModule(): Module = module {
    single {
        OpenIapScreen()
    }
    single { PurchaseManager(get()) }
}

fun iosIapModule(iapManager: IAPManager): Module = module {
    single {
        iapManager
    }
}
