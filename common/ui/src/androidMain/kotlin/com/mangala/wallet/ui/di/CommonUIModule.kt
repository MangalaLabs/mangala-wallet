package com.mangala.wallet.ui.di

import com.mangala.wallet.ui.GalleryHelper
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

actual fun commonUiModule() = module {
    factory { GalleryHelper(applicationContext = androidContext()) }
}