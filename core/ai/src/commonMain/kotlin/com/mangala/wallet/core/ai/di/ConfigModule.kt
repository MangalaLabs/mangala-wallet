package com.mangala.wallet.core.ai.di

import com.mangala.wallet.core.ai.domain.model.function.config.ConfigurationManager
import com.mangala.wallet.core.ai.domain.model.function.config.FunctionConfigSource
import com.mangala.wallet.core.ai.domain.model.function.config.LocalJsonConfigSource
import com.mangala.wallet.mokoresources.MR
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/**
 * Koin module for the function configuration system.
 * This provides dependencies for loading function configurations from different sources.
 */
val configModule = module {
    // Provide JSON instance for serialization/deserialization
    single {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = false
        }
    }
    
    // Provide the function configuration source
    // Note: ResourceReader is provided by platform-specific modules
    single<FunctionConfigSource> {
        LocalJsonConfigSource(
            jsonFileResource = MR.files.function_calls_json,
            json = get(),
            resourceReader = get()
        )
    }
    
    // Provide the configuration manager
    single {
        ConfigurationManager(
            configSource = get(),
            functionRegistry = get()
        ).apply {
            // Initialize the manager when it's created
            initialize()
        }
    }
}