package com.mangala.wallet.utils

actual class BuildEnvironmentProviderImpl : BuildEnvironmentProvider {
    actual override fun getBuildEnvironment(): BuildEnvironment {
        return BuildEnvironment.PRODUCTION
    }
}