package com.mangala.wallet.features.addressbook.domain.usecase.setting

import com.mangala.wallet.features.addressbook.data.model.enum.SubscriptionType
import com.mangala.wallet.features.addressbook.data.model.setting.UserSubscriptionEntity
import com.mangala.wallet.features.addressbook.domain.repository.setting.SubscriptionRepository
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

class UpgradeSubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    /**
     * Execute the use case to upgrade a subscription.
     * @param userSettingId The ID of the user setting.
     * @param newType The new subscription type.
     * @param durationMonths The new duration in months (null to keep current).
     * @return Result containing the updated subscription entity or null if not found.
     */
    suspend operator fun invoke(
        userSettingId: String,
        newType: SubscriptionType,
        durationMonths: Int? = null
    ): Result<UserSubscriptionEntity?> {
        // If duration is provided, calculate new end date
        val endDate = if (durationMonths != null) {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            val durationMillis = durationMonths * 30L * 24L * 60L * 60L * 1000L
            Instant.fromEpochMilliseconds(now.toEpochMilliseconds() + durationMillis)
        } else {
            // Get current subscription to preserve existing end date
            val currentSubscription = repository.getUserSubscription(userSettingId).getOrNull()
            currentSubscription?.endDate
        }

        return repository.upgradeSubscription(
            userSettingId = userSettingId,
            newType = newType,
            endDate = endDate
        )
    }
}