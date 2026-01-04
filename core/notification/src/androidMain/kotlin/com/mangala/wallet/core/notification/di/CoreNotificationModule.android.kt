package com.mangala.wallet.core.notification.di

import com.mangala.wallet.core.notification.ApplicationStartPlatformSpecific
import org.koin.dsl.module
import org.koin.android.ext.koin.androidApplication

actual fun coreNotificationModulePlatformSpecific() = module {
    single {
        ApplicationStartPlatformSpecific(applicationContext = androidApplication().applicationContext)
    }
}