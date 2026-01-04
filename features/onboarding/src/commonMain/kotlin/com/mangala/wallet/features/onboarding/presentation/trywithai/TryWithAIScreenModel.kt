package com.mangala.wallet.features.onboarding.presentation.trywithai

import com.mangala.wallet.domain.datastore.usecases.CompleteOnboardingUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel

class TryWithAIScreenModel(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
): BaseScreenModel() {
    
    suspend fun completeOnboarding() {
        completeOnboardingUseCase()
    }
}