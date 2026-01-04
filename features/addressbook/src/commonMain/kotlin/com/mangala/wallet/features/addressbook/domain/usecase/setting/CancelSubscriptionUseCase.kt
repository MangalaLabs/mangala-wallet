package com.mangala.wallet.features.addressbook.domain.usecase.setting

import com.mangala.wallet.features.addressbook.domain.repository.setting.SubscriptionRepository

class CancelSubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    /**
     * Execute the use case to cancel a subscription.
     * @param userSettingId The ID of the user setting.
     * @return Result containing whether the operation was successful.
     */
    suspend operator fun invoke(userSettingId: String): Result<Boolean> {
        return repository.deactivateSubscription(userSettingId)
    }
}