package com.mangala.wallet.utils.di

import com.mangala.wallet.utils.isDebug
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun commonUtilsModule() = module {
    single<Json>(named(IGNORE_UNKNOWN_KEY_JSON)) { Json { ignoreUnknownKeys = true } }
    single<Json>(named(JSON)) { Json {
        ignoreUnknownKeys = isDebug.not()
        allowStructuredMapKeys = true
    } }
    single<Json>(named(LOCAL_CACHE_JSON)) { Json {
        ignoreUnknownKeys = isDebug.not()
        allowStructuredMapKeys = true
    } }
}

enum class ParsingJsonMode {
    IGNORE_UNKNOWN_KEY_JSON,
    DEFAULT
}

const val IGNORE_UNKNOWN_KEY_JSON = "ignoreUnknownKeyJson"
const val JSON = "json"
const val LOCAL_CACHE_JSON = "localCacheJson"