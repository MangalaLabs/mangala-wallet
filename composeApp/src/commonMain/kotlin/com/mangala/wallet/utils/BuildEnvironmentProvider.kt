package com.mangala.wallet.utils

expect class BuildEnvironmentProviderImpl constructor(): BuildEnvironmentProvider {
    override fun getBuildEnvironment(): BuildEnvironment
}