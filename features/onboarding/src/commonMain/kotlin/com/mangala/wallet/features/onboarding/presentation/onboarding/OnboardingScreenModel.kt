package com.mangala.wallet.features.onboarding.presentation.onboarding

import com.mangala.wallet.features.onboarding.domain.navigator.CreateWalletNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel

class OnboardingScreenModel(
    private val createWalletNavigator: CreateWalletNavigator
) : BaseScreenModel() {

    fun isPinSetup(): Boolean = createWalletNavigator.isPinSetup()

    fun getSetupPinScreen(onPinSetupSuccess: () -> Unit): SharedScreen {
        return createWalletNavigator.getSetupPinScreen(onPinSetupSuccess)
    }

    fun getCreateWalletScreen(): SharedScreen {
        return createWalletNavigator.getCreateWalletScreen()
    }
}