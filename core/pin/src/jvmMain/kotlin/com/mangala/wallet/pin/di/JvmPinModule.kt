package com.mangala.wallet.pin.di

import com.mangala.wallet.pin.presentation.SystemInfoManager
import com.mangala.wallet.utils.ISystemInfoManager
import org.koin.dsl.module

actual fun platformPinModule() = module {
    single<ISystemInfoManager> { SystemInfoManager() }
}