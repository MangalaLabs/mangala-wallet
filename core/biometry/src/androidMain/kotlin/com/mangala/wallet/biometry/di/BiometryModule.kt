package com.mangala.wallet.biometry.di

import com.mangala.wallet.biometry.BiometryAuthenticator
import com.mangala.wallet.biometry.presentation.BiometryScreenModel
import com.mangala.wallet.biometry.presentation.IBiometryScreenModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

actual fun biometryModule() = module {
    single {
        BiometryAuthenticator(applicationContext = androidApplication().applicationContext)
    }

    single<IBiometryScreenModel> {
        BiometryScreenModel()
    }
}