package com.mangala.wallet.utils

import org.koin.mp.KoinPlatformTools

interface BuildEnvironmentProvider {
    fun getBuildEnvironment(): BuildEnvironment
    fun isDevelopmentEnvironment(): Boolean {
        return getBuildEnvironment() == BuildEnvironment.DEV
    }
}

// A hacky way to get the BuildEnvironmentProvider statically
// should not be used unless absolutely necessary. In other cases, inject the BuildEnvironmentProvider instead
object BuildEnvironmentProviderObject {
    private val buildEnvironmentProviderImpl: BuildEnvironmentProvider by lazy {
        KoinPlatformTools.defaultContext().get().get<BuildEnvironmentProvider>()
    }

    fun isDevelopmentEnvironment(): Boolean {
        return buildEnvironmentProviderImpl.isDevelopmentEnvironment()
    }
}

enum class BuildEnvironment(private val value: String) {
    DEV("dev"),
    STAGING("stg"),
    UAT("uat"),
    PRODUCTION("prod");

    companion object {
        fun from(value: String?): BuildEnvironment {
            return when (value) {
                DEV.value -> DEV
                STAGING.value -> STAGING
                UAT.value -> UAT
                PRODUCTION.value -> PRODUCTION
                else -> {
                    throw IllegalArgumentException("Invalid value $value for BuildEnvironment")
                }
            }
        }
    }
}