package com.mangala.wallet.pin.di

import com.mangala.wallet.pin.data.DeviceIdProvider
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformNewPinModule(): Module = module {
    single {
        DeviceIdProvider()
    }
}
