package com.mangala.wallet.utils.di

import com.mangala.wallet.utils.ClipboardFactory
import com.mangala.wallet.utils.MailToFactory
import com.mangala.wallet.utils.ShareFactory
import com.mangala.wallet.utils.ToastFactory
import com.mangala.wallet.utils.app.AppVersionUtils
import org.koin.dsl.module

actual fun utilsModule() = module {
    single { ShareFactory() }
    single { ClipboardFactory() }
    single { ToastFactory() }
    single { com.mangala.wallet.utils.OpenScreenByPlatform() }
    single { AppVersionUtils() }
    factory { MailToFactory() }
}