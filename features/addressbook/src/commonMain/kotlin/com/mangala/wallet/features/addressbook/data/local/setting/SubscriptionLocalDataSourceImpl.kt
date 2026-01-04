package com.mangala.wallet.features.addressbook.data.local.setting

import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.enum.SubscriptionType
import com.mangala.wallet.features.addressbook.data.model.setting.UserSubscriptionEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

class SubscriptionLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SubscriptionLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    override suspend fun getUserSubscription(userSettingId: String): Result<UserSubscriptionEntity?> = withContext(ioDispatcher) {
        try {
            val query = dbQuery.getUserSubscription(userSettingId)
            val result = query.executeAsOneOrNull()

            if (result != null) {
                Result.success(mapToUserSubscriptionEntity(
                    id = result.id,
                    user_setting_id = result.user_setting_id,
                    subscription_type = result.subscription_type,
                    start_date = result.start_date,
                    end_date = result.end_date,
                    is_active = result.is_active,
                    created_at = result.created_at,
                    updated_at = result.updated_at
                ))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertUserSubscription(subscription: UserSubscriptionEntity): Result<Unit> = withContext(ioDispatcher) {
        try {
            dbQuery.insertUserSubscription(
                id = subscription.id,
                user_setting_id = subscription.userSettingId,
                subscription_type = subscription.subscriptionType.value,
                start_date = subscription.startDate.toEpochMilliseconds(),
                end_date = subscription.endDate?.toEpochMilliseconds(),
                is_active = subscription.isActive,
                created_at = subscription.createdAt.toEpochMilliseconds(),
                updated_at = subscription.updatedAt.toEpochMilliseconds()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserSubscription(subscription: UserSubscriptionEntity): Result<Unit> = withContext(ioDispatcher) {
        try {
            dbQuery.updateUserSubscription(
                subscription_type = subscription.subscriptionType.value,
                start_date = subscription.startDate.toEpochMilliseconds(),
                end_date = subscription.endDate?.toEpochMilliseconds(),
                is_active = subscription.isActive,
                updated_at = subscription.updatedAt.toEpochMilliseconds(),
                id = subscription.id
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUserSubscription(id: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            dbQuery.deleteUserSubscription(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper function to map database columns to entity
    private fun mapToUserSubscriptionEntity(
        id: String,
        user_setting_id: String,
        subscription_type: String?,
        start_date: Long,
        end_date: Long?,
        is_active: Boolean?,
        created_at: Long,
        updated_at: Long
    ): UserSubscriptionEntity {
        return UserSubscriptionEntity(
            id = id,
            userSettingId = user_setting_id,
            subscriptionType = SubscriptionType.fromString(subscription_type ?: "FREE"),
            startDate = Instant.fromEpochMilliseconds(start_date),
            endDate = end_date?.let { Instant.fromEpochMilliseconds(it) },
            isActive = is_active ?: false,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }
}