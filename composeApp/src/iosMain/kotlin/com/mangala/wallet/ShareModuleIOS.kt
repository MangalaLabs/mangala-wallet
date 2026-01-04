package com.mangala.wallet

import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import com.mangala.features.browser.OpenBrowser
import com.mangala.wallet.core.notification.initNotificationListener
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.viewmodel.ApplicationViewModel
import com.wallet.iap.purchases.device.IAPManager
import com.wallet.iap.purchases.di.iosIapModule
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.module
import platform.Foundation.NSBundle

fun initKoinIos(iapManager: IAPManager): KoinApplication = initKoin {
    setupCrashKiOSCrashlytics()
    modules(iosIapModule(iapManager))
    initNotificationListener()
}

fun setupCrashKiOSCrashlytics() {
    enableCrashlytics()
    setCrashlyticsUnhandledExceptionHook()
}

// Workaround class for injecting an `NSObject` class.
// When not used, an error "KClass of Objective-C classes is not supported." is thrown.
data class BundleProvider(val bundle: NSBundle)

val Koin.applicationViewModel: ApplicationViewModel
    get() = get()

val Koin.scanQRCode: ScanQRCode
    get() = get()

val Koin.openBrowser: OpenBrowser
    get() = get()