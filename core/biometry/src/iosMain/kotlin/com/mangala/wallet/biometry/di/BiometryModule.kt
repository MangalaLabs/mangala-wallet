package com.mangala.wallet.biometry.di

import com.mangala.wallet.biometry.BiometryAuthenticator
import com.mangala.wallet.biometry.presentation.BiometryScreenModel
import com.mangala.wallet.biometry.presentation.IBiometryScreenModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun biometryModule() = module {
    single {
        BiometryAuthenticator()
    }
    single<IBiometryScreenModel> {
        BiometryScreenModel()
    }
}