package com.mangala.wallet.features.onboarding.presentation.onboarding

import com.mangala.wallet.features.onboarding.domain.navigator.CreateWalletNavigator
import com.mangala.wallet.features.onboarding.domain.navigator.ImportWalletNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel

class OnboardingScreenModel(
    private val createWalletNavigator: CreateWalletNavigator,
    private val importWalletNavigator: ImportWalletNavigator
) : BaseScreenModel() {

    // Create Wallet
    fun isPinSetup(): Boolean = createWalletNavigator.isPinSetup()

    fun getSetupPinScreen(onPinSetupSuccess: () -> Unit): SharedScreen {
        return createWalletNavigator.getSetupPinScreen(onPinSetupSuccess)
    }

    fun getCreateWalletScreen(): SharedScreen {
        return createWalletNavigator.getCreateWalletScreen()
    }

    // Import Wallet
    fun getImportWalletScreen(): SharedScreen {
        return importWalletNavigator.getImportWalletScreen()
    }
}