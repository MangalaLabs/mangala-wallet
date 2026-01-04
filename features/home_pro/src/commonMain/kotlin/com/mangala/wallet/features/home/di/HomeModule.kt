package com.mangala.wallet.features.home.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.home.presentation.CreateAccountNotificationScreen
import com.mangala.wallet.features.home.presentation.CreateAccountNotificationScreenModel
import com.mangala.wallet.features.home.presentation.HomeScreen
import com.mangala.wallet.features.home.presentation.HomeScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.ParametersHolder
import org.koin.dsl.module

val homeModule = module {
    factory {
        HomeScreenModel(get(), get(), get(), get(), get(), get(), get(), get())
    }

    factory {
        CreateAccountNotificationScreenModel(
            updateAccountStatusUseCase = get()
        )
    }
}

val homeScreenModule = screenModule {
    register<SharedScreen.HomeScreen> { HomeScreen(it.initialTab) }

    register<SharedScreen.CreateAccountNotificationScreen> {
        CreateAccountNotificationScreen(
            accountName = it.accountName,
            chainId = it.chainId,
            isSuccess = it.isSuccess,
            errorMessage = it.errorMessage,
            onDismiss = it.onDismiss
        )
    }
}

inline operator fun <reified T> ParametersHolder.component6(): T = elementAt(5, T::class)
inline operator fun <reified T> ParametersHolder.component7(): T = elementAt(6, T::class)
inline operator fun <reified T> ParametersHolder.component8(): T = elementAt(7, T::class)