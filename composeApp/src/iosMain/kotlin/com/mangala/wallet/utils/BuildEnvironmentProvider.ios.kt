package com.mangala.wallet.utils

import platform.Foundation.NSBundle

actual class BuildEnvironmentProviderImpl : BuildEnvironmentProvider {
    actual override fun getBuildEnvironment(): BuildEnvironment {
        val bundle = NSBundle.mainBundle
        val environment = bundle.objectForInfoDictionaryKey(KEY_ENVIRONMENT)

        return BuildEnvironment.from(environment as? String)
    }

    companion object {
        private const val KEY_ENVIRONMENT = "Environment"
    }
}