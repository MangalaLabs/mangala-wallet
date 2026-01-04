package com.mangala.wallet.features.addressbook.domain.usecase.setting

import com.mangala.wallet.features.addressbook.domain.repository.setting.SubscriptionRepository

class CheckPremiumAccessUseCase(
    private val repository: SubscriptionRepository
) {
    /**
     * Execute the use case to check premium access.
     * @param userSettingId The ID of the user setting.
     * @return Result containing whether the user has premium access.
     */
    suspend operator fun invoke(userSettingId: String): Result<Boolean> {
        return repository.hasPremiumAccess(userSettingId)
    }
}