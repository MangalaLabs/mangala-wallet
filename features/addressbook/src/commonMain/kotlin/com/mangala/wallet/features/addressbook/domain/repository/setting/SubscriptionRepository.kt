package com.mangala.wallet.features.addressbook.domain.repository.setting

import com.mangala.wallet.features.addressbook.data.model.enum.SubscriptionType
import com.mangala.wallet.features.addressbook.data.model.setting.UserSubscriptionEntity
import kotlinx.datetime.Instant

interface SubscriptionRepository {
    /**
     * Get a user subscription by user setting ID.
     * @param userSettingId The ID of the user setting.
     * @return The subscription entity or null if not found.
     */
    suspend fun getUserSubscription(userSettingId: String): Result<UserSubscriptionEntity?>

    /**
     * Create a new subscription for a user.
     * @param userSettingId The ID of the user setting.
     * @param subscriptionType The type of subscription.
     * @param startDate The start date of the subscription.
     * @param endDate The end date of the subscription (null for lifetime).
     * @param isActive Whether the subscription is active.
     * @return The created subscription entity.
     */
    suspend fun createSubscription(
        userSettingId: String,
        subscriptionType: SubscriptionType = SubscriptionType.FREE,
        startDate: Instant? = null,
        endDate: Instant? = null,
        isActive: Boolean = true
    ): Result<UserSubscriptionEntity>

    /**
     * Upgrade a user's subscription type.
     * @param userSettingId The ID of the user setting.
     * @param newType The new subscription type.
     * @param endDate The new end date for the subscription.
     * @return The updated subscription entity or null if not found.
     */
    suspend fun upgradeSubscription(
        userSettingId: String,
        newType: SubscriptionType,
        endDate: Instant? = null
    ): Result<UserSubscriptionEntity?>

    /**
     * Renew a user's subscription.
     * @param userSettingId The ID of the user setting.
     * @param newEndDate The new end date for the subscription.
     * @return The updated subscription entity or null if not found.
     */
    suspend fun renewSubscription(
        userSettingId: String,
        newEndDate: Instant
    ): Result<UserSubscriptionEntity?>

    /**
     * Deactivate a user's subscription.
     * @param userSettingId The ID of the user setting.
     * @return True if the subscription was deactivated, false if not found.
     */
    suspend fun deactivateSubscription(userSettingId: String): Result<Boolean>

    /**
     * Check if a user has an active subscription.
     * @param userSettingId The ID of the user setting.
     * @return True if the user has a valid subscription.
     */
    suspend fun hasActiveSubscription(userSettingId: String): Result<Boolean>

    /**
     * Check if a user has premium features access.
     * @param userSettingId The ID of the user setting.
     * @return True if the user has premium access.
     */
    suspend fun hasPremiumAccess(userSettingId: String): Result<Boolean>

    /**
     * Check if a user has enterprise features access.
     * @param userSettingId The ID of the user setting.
     * @return True if the user has enterprise access.
     */
    suspend fun hasEnterpriseAccess(userSettingId: String): Result<Boolean>
}