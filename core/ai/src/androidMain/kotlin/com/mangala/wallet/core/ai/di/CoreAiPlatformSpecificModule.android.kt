package com.mangala.wallet.core.ai.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun coreAiPlatformSpecificModule(): Module = module {
    includes(resourceReaderModule)
    
    single<HttpClientEngine> {
        OkHttp.create {
            addInterceptor(ChuckerInterceptor.Builder(get()).build())
        }
    }
}