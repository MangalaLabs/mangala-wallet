package com.mangala.wallet.features.addressbook.data.model.setting

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.model.enum.SubscriptionType
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

data class UserSubscriptionEntity (
    val id: String,
    val userSettingId: String,
    val subscriptionType: SubscriptionType,
    val startDate: Instant,
    val endDate: Instant?,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    fun isValid(): Boolean {
        val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        return isActive && (endDate == null || endDate > now)
    }

    /**
     * Returns whether the subscription has an expiry date.
     */
    fun hasExpiryDate(): Boolean = endDate != null

    /**
     * Returns whether the subscription is premium or higher.
     */
    fun isPremiumOrHigher(): Boolean {
        return subscriptionType == SubscriptionType.PREMIUM || subscriptionType == SubscriptionType.ENTERPRISE
    }

    /**
     * Returns whether the subscription is enterprise.
     */
    fun isEnterprise(): Boolean {
        return subscriptionType == SubscriptionType.ENTERPRISE
    }

    companion object {
        /**
         * Creates a new subscription with default values.
         */
        fun create(
            userSettingId: String,
            subscriptionType: SubscriptionType = SubscriptionType.FREE,
            startDate: Instant = Instant.fromEpochMilliseconds(
                localDateTimeToMillis(
                    localDateTimeNow()
                )
            ),
            endDate: Instant? = null,
            isActive: Boolean = true
        ): UserSubscriptionEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return UserSubscriptionEntity(
                id = uuid4().toString(),
                userSettingId = userSettingId,
                subscriptionType = subscriptionType,
                startDate = startDate,
                endDate = endDate,
                isActive = isActive,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}