package com.mangala.wallet.core.security.di

import com.mangala.wallet.core.security.config.DefaultSecurityConfigProvider
import com.mangala.wallet.core.security.config.SecurityConfigProvider
import org.koin.dsl.module

val coreSecurityModule = module {
    single<SecurityConfigProvider> { DefaultSecurityConfigProvider() }
}