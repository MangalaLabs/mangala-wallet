package com.mangala.wallet.core.ai.di

import android.content.Context
import com.mangala.wallet.core.ai.domain.model.function.config.ResourceReader
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific implementation of the ResourceReader module
 */
val resourceReaderModule = module {
    single { 
        ResourceReader(androidContext()) 
    }
}