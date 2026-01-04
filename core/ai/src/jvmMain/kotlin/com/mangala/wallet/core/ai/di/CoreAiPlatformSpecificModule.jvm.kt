package com.mangala.wallet.core.ai.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun coreAiPlatformSpecificModule(): Module = module {
    includes(resourceReaderModule)
    
    single<HttpClientEngine> { CIO.create() }
}