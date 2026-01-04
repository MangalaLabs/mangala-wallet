package com.mangala.wallet.utils

import com.mangala.wallet.BuildConfig

actual class BuildEnvironmentProviderImpl : BuildEnvironmentProvider {
    actual override fun getBuildEnvironment(): BuildEnvironment {
        return BuildEnvironment.from(BuildConfig.FLAVOR_environment)
    }
}