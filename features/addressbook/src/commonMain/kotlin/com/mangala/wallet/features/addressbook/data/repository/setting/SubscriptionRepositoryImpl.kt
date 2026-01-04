package com.mangala.wallet.features.addressbook.data.repository.setting

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.setting.SubscriptionLocalDataSource
import com.mangala.wallet.features.addressbook.data.model.enum.SubscriptionType
import com.mangala.wallet.features.addressbook.data.model.setting.UserSubscriptionEntity
import com.mangala.wallet.features.addressbook.domain.repository.setting.SubscriptionRepository
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

class SubscriptionRepositoryImpl(
    private val localDataSource: SubscriptionLocalDataSource
) : SubscriptionRepository {

    override suspend fun getUserSubscription(userSettingId: String): Result<UserSubscriptionEntity?> {
        return localDataSource.getUserSubscription(userSettingId)
    }

    override suspend fun createSubscription(
        userSettingId: String,
        subscriptionType: SubscriptionType,
        startDate: Instant?,
        endDate: Instant?,
        isActive: Boolean
    ): Result<UserSubscriptionEntity> {
        val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        val subscription = UserSubscriptionEntity(
            id = uuid4().toString(),
            userSettingId = userSettingId,
            subscriptionType = subscriptionType,
            startDate = startDate ?: now,
            endDate = endDate,
            isActive = isActive,
            createdAt = now,
            updatedAt = now
        )

        return localDataSource.insertUserSubscription(subscription).map { subscription }
    }

    override suspend fun upgradeSubscription(
        userSettingId: String,
        newType: SubscriptionType,
        endDate: Instant?
    ): Result<UserSubscriptionEntity?> {
        val subscriptionResult = localDataSource.getUserSubscription(userSettingId)

        if (subscriptionResult.isFailure) {
            return subscriptionResult
        }

        val currentSubscription = subscriptionResult.getOrNull()
        if (currentSubscription == null) {
            return Result.success(null)
        }

        val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        val updatedSubscription = currentSubscription.copy(
            subscriptionType = newType,
            endDate = endDate,
            updatedAt = now
        )

        return localDataSource.updateUserSubscription(updatedSubscription).map { updatedSubscription }
    }

    override suspend fun renewSubscription(
        userSettingId: String,
        newEndDate: Instant
    ): Result<UserSubscriptionEntity?> {
        val subscriptionResult = localDataSource.getUserSubscription(userSettingId)

        if (subscriptionResult.isFailure) {
            return subscriptionResult
        }

        val currentSubscription = subscriptionResult.getOrNull()
        if (currentSubscription == null) {
            return Result.success(null)
        }

        val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        val updatedSubscription = currentSubscription.copy(
            endDate = newEndDate,
            isActive = true,
            updatedAt = now
        )

        return localDataSource.updateUserSubscription(updatedSubscription).map { updatedSubscription }
    }

    override suspend fun deactivateSubscription(userSettingId: String): Result<Boolean> {
        val subscriptionResult = localDataSource.getUserSubscription(userSettingId)

        if (subscriptionResult.isFailure) {
            return subscriptionResult.map { false }
        }

        val subscription = subscriptionResult.getOrNull()
        if (subscription == null) {
            return Result.success(false)
        }

        val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        val updatedSubscription = subscription.copy(
            isActive = false,
            updatedAt = now
        )

        return localDataSource.updateUserSubscription(updatedSubscription).map { true }
    }

    override suspend fun hasActiveSubscription(userSettingId: String): Result<Boolean> {
        return getUserSubscription(userSettingId).map { subscription ->
            subscription?.isValid() ?: false
        }
    }

    override suspend fun hasPremiumAccess(userSettingId: String): Result<Boolean> {
        return getUserSubscription(userSettingId).map { subscription ->
            subscription?.let {
                it.isValid() && it.isPremiumOrHigher()
            } ?: false
        }
    }

    override suspend fun hasEnterpriseAccess(userSettingId: String): Result<Boolean> {
        return getUserSubscription(userSettingId).map { subscription ->
            subscription?.let {
                it.isValid() && it.isEnterprise()
            } ?: false
        }
    }
}