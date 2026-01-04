package com.mangala.wallet.remote.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

actual fun ktorHttpClientEngineFactoryModule() = module {
    single {
        OkHttp.create {
            addInterceptor(ChuckerInterceptor(androidContext()))
        }
    }
}