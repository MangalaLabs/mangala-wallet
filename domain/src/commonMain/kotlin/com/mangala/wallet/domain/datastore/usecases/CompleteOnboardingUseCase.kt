package com.mangala.wallet.domain.datastore.usecases

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import com.mangala.wallet.utils.analytics.MangalaAnalytics

class CompleteOnboardingUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke() {
        MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.ONBOARDING_COMPLETED)
        dataStoreRepository.saveOnboardingCompleted(true)
    }
}