package com.mangala.wallet.features.onboarding.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.onboarding.domain.navigator.CreateWalletNavigator
import com.mangala.wallet.features.onboarding.domain.navigator.CreateWalletNavigatorFactory
import com.mangala.wallet.features.onboarding.presentation.onboarding.OnboardingScreen
import com.mangala.wallet.features.onboarding.presentation.onboarding.OnboardingScreenModel
import com.mangala.wallet.features.onboarding.presentation.trywithai.TryWithAIScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val onboardingModule = module {
    single<CreateWalletNavigator> { CreateWalletNavigatorFactory.create(getIsPinSetupUseCase = get()) }
    factory { OnboardingScreenModel(get()) }
    factory { TryWithAIScreenModel(get()) }
}

val onboardingScreenModule = screenModule {
    register<SharedScreen.OnboardingScreen> {
        OnboardingScreen()
    }
    register<SharedScreen.TermsAndPolicyScreen> {
        com.mangala.wallet.features.onboarding.presentation.TermsOfServiceScreen()
    }
}