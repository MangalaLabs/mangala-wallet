package com.mangala.wallet.navigation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.domain.datastore.usecases.CheckOnboardingCompletedUseCase
import com.mangala.wallet.features.onboarding.presentation.onboarding.OnboardingScreen
import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.pin.presentation.unlock.UnlockPinScreen
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.pin.presentation.unlock.UnlockPinScreenV2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootScreenModel : ScreenModel, KoinComponent {
    private val getIsPinSetupUseCase: GetIsPinSetupUseCase by inject()
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase by inject()
    private val checkOnboardingCompletedUseCase: CheckOnboardingCompletedUseCase by inject()
    
    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Loading)
    val navigationState = _navigationState.asStateFlow()
    
    init {
        screenModelScope.launch {
            val isOnboardingCompleted = checkOnboardingCompletedUseCase()
            val isPinSetup = getIsPinSetupUseCase()
            val hasWallet = getSelectedWalletUseCase() != null
            
            val screen = when {
                isPinSetup || hasWallet -> UnlockPinScreenV2(
                    SharedScreen.UnlockPinScreen.OPEN_APP,
                    antelopeAccountName = null
                )
                isOnboardingCompleted -> ScreenRegistry.get(SharedScreen.HomeScreen())
                else -> OnboardingScreen()
            }
            
            _navigationState.value = NavigationState.Ready(screen)
        }
    }
}