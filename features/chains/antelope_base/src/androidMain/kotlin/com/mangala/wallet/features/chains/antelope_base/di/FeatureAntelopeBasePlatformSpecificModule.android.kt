package com.mangala.wallet.features.chains.antelope_base.di

import com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.CreateAccountFirebaseFunctionDataSource
import com.mangala.wallet.features.chains.antelope_base.data.remote.notification.AntelopeAccountNotificationFirebaseFunctionDataSource
import org.koin.dsl.module

actual fun featureAntelopeBasePlatformSpecificModule() = module {
    factory { CreateAccountFirebaseFunctionDataSource() }
    factory { AntelopeAccountNotificationFirebaseFunctionDataSource() }
}