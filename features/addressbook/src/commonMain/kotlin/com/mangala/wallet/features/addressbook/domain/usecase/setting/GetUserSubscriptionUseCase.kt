package com.mangala.wallet.features.addressbook.domain.usecase.setting

import com.mangala.wallet.features.addressbook.data.model.setting.UserSubscriptionEntity
import com.mangala.wallet.features.addressbook.domain.repository.setting.SubscriptionRepository

class GetUserSubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    /**
     * Execute the use case to get a user's subscription.
     * @param userSettingId The ID of the user setting.
     * @return Result containing the subscription entity or null if not found.
     */
    suspend operator fun invoke(userSettingId: String): Result<UserSubscriptionEntity?> {
        return repository.getUserSubscription(userSettingId)
    }
}