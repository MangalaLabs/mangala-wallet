package com.mangala.wallet.core.notification.di

import com.mangala.wallet.core.notification.ApplicationStartPlatformSpecific
import org.koin.dsl.module

actual fun coreNotificationModulePlatformSpecific() = module {
    single {
        ApplicationStartPlatformSpecific()
    }
}