package com.mangala.wallet.features.addressbook.domain.usecase.setting

import com.mangala.wallet.features.addressbook.data.model.enum.SubscriptionType
import com.mangala.wallet.features.addressbook.data.model.setting.UserSubscriptionEntity
import com.mangala.wallet.features.addressbook.domain.repository.setting.SubscriptionRepository
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

class CreateSubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    /**
     * Execute the use case to create a new subscription.
     * @param userSettingId The ID of the user setting.
     * @param subscriptionType The type of subscription.
     * @param durationMonths The duration in months (null for lifetime).
     * @return Result containing the created subscription entity.
     */
    suspend operator fun invoke(
        userSettingId: String,
        subscriptionType: SubscriptionType,
        durationMonths: Int? = null
    ): Result<UserSubscriptionEntity> {
        val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        val endDate = durationMonths?.let {
            // Calculate end date based on duration (simple implementation - actual might need calendar adjustments)
            val durationMillis = durationMonths * 30L * 24L * 60L * 60L * 1000L
            Instant.fromEpochMilliseconds(now.toEpochMilliseconds() + durationMillis)
        }

        return repository.createSubscription(
            userSettingId = userSettingId,
            subscriptionType = subscriptionType,
            startDate = now,
            endDate = endDate,
            isActive = true
        )
    }
}