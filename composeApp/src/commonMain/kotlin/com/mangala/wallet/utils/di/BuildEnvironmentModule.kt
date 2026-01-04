package com.mangala.wallet.utils.di

import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.mangala.wallet.utils.BuildEnvironmentProviderImpl
import org.koin.dsl.module

fun buildEnvironmentModule() = module {
    single<BuildEnvironmentProvider> { BuildEnvironmentProviderImpl() }
}