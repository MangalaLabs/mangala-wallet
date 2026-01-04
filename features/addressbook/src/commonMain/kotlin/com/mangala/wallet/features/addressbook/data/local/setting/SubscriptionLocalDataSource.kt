package com.mangala.wallet.features.addressbook.data.local.setting

import com.mangala.wallet.features.addressbook.data.model.setting.UserSubscriptionEntity

/**
 * Interface for local data source operations related to user subscriptions.
 */
interface SubscriptionLocalDataSource {
    /**
     * Get a user subscription by user setting ID.
     * @param userSettingId The ID of the user setting.
     * @return The subscription entity or null if not found.
     */
    suspend fun getUserSubscription(userSettingId: String): Result<UserSubscriptionEntity?>

    /**
     * Insert a new user subscription.
     * @param subscription The subscription entity to insert.
     */
    suspend fun insertUserSubscription(subscription: UserSubscriptionEntity): Result<Unit>

    /**
     * Update an existing user subscription.
     * @param subscription The subscription entity to update.
     */
    suspend fun updateUserSubscription(subscription: UserSubscriptionEntity): Result<Unit>

    /**
     * Delete a user subscription by ID.
     * @param id The ID of the subscription to delete.
     */
    suspend fun deleteUserSubscription(id: String): Result<Unit>
}