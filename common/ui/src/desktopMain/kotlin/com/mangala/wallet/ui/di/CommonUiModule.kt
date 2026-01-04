package com.mangala.wallet.ui.di

import com.mangala.wallet.ui.GalleryHelper
import org.koin.dsl.module

actual fun commonUiModule() = module {
    factory { GalleryHelper() }
}