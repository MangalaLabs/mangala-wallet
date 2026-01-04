package com.mangala.wallet.remote.di

import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

actual fun ktorHttpClientEngineFactoryModule() = module {
    single { CIO.create() }
}