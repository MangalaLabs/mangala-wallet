package com.mangala.wallet.features.addressbook.domain.usecase.setting

import com.mangala.wallet.features.addressbook.data.model.setting.UserSubscriptionEntity
import com.mangala.wallet.features.addressbook.domain.repository.setting.SubscriptionRepository
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

class RenewSubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    /**
     * Execute the use case to renew a subscription.
     * @param userSettingId The ID of the user setting.
     * @param durationMonths The duration to add in months.
     * @return Result containing the updated subscription entity or null if not found.
     */
    suspend operator fun invoke(
        userSettingId: String,
        durationMonths: Int
    ): Result<UserSubscriptionEntity?> {
        // Get current subscription to determine new end date
        val subscriptionResult = repository.getUserSubscription(userSettingId)
        if (subscriptionResult.isFailure) {
            return subscriptionResult
        }

        val subscription = subscriptionResult.getOrNull() ?: return Result.success(null)

        // Calculate new end date
        val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        val baseDate = if (subscription.endDate != null && subscription.endDate > now) {
            // If subscription hasn't expired, add time to current end date
            subscription.endDate
        } else {
            // If expired or no end date, start from current time
            now
        }

        val durationMillis = durationMonths * 30L * 24L * 60L * 60L * 1000L
        val newEndDate = Instant.fromEpochMilliseconds(baseDate.toEpochMilliseconds() + durationMillis)

        return repository.renewSubscription(
            userSettingId = userSettingId,
            newEndDate = newEndDate
        )
    }
}