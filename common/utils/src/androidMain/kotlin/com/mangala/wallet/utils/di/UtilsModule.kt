package com.mangala.wallet.utils.di

import com.mangala.wallet.utils.AppLifecycleObserver
import com.mangala.wallet.utils.ClipboardFactory
import com.mangala.wallet.utils.MailToFactory
import com.mangala.wallet.utils.OpenScreenByPlatform
import com.mangala.wallet.utils.ShareFactory
import com.mangala.wallet.utils.ToastFactory
import com.mangala.wallet.utils.app.AppVersionUtils
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun utilsModule() = module {
    single { ShareFactory(applicationContext = androidContext()) }
    single { ClipboardFactory(applicationContext = androidContext()) }
    single { ToastFactory(applicationContext = androidContext()) }
    single { OpenScreenByPlatform(applicationContext = androidContext()) }
    single { AppLifecycleObserver(application = get()) }
    single { AppVersionUtils(context = androidContext()) }
    factory { MailToFactory(context = androidContext()) }
}