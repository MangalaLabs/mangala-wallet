package com.mangala.features.browser.di

import com.mangala.features.browser.OpenBrowser
import org.koin.dsl.module

actual fun browserTabModule() = module {
    single { OpenBrowser() }
}