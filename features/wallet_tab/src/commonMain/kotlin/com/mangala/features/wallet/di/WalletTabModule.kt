package com.mangala.features.wallet.di

import cafe.adriel.voyager.core.registry.screenModule
import org.koin.core.parameter.ParametersHolder
import org.koin.dsl.module

val walletTabModule = module {

}

val walletTabScreenModule = screenModule {

}

inline operator fun <reified T> ParametersHolder.component6(): T = elementAt(5, T::class)

