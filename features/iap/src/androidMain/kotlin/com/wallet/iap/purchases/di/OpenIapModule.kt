package com.wallet.iap.purchases.di

import com.mangala.wallet.utils.di.IGNORE_UNKNOWN_KEY_JSON
import com.wallet.iap.purchases.OpenIapScreen
import com.wallet.iap.purchases.device.PurchaseManager
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

public actual fun openIapModule(): Module = module {
    single {
        OpenIapScreen(applicationContext = androidApplication().applicationContext)
    }
    factory { PurchaseManager(get(named(IGNORE_UNKNOWN_KEY_JSON))) }
}
