package com.mangala.wallet.core.ai.di

import com.mangala.wallet.core.ai.domain.model.function.config.ResourceReader
import org.koin.dsl.module

/**
 * JVM-specific implementation of the ResourceReader module
 */
val resourceReaderModule = module {
    single { 
        ResourceReader() 
    }
}