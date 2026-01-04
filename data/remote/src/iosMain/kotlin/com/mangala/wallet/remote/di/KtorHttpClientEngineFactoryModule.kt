package com.mangala.wallet.remote.di

import org.koin.dsl.module
import io.ktor.client.engine.darwin.Darwin

actual fun ktorHttpClientEngineFactoryModule() = module {
    single { Darwin.create() }
}