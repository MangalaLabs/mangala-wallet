package com.mangala.wallet.features.addressbook.data.repository.setting

import com.mangala.wallet.features.addressbook.data.local.note.AddressNoteLocalDataSourceImpl
import com.mangala.wallet.features.addressbook.data.local.setting.SettingsLocalDataSource
import com.mangala.wallet.features.addressbook.data.model.setting.AppVersionEntity
import com.mangala.wallet.features.addressbook.data.model.setting.OfflineQueueEntity
import com.mangala.wallet.features.addressbook.data.model.setting.UserSettingEntity
import com.mangala.wallet.features.addressbook.domain.repository.setting.SettingsRepository
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

class SettingsRepositoryImpl(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {
    override suspend fun getUserSettings(id: String): UserSettingEntity? {
        return localDataSource.getUserSettings()
    }

    override fun observeUserSettings(id: String): Flow<UserSettingEntity?> {
        return localDataSource.observeUserSettings(id)
    }

    override suspend fun saveUserSettings(settings: UserSettingEntity): Result<Boolean> {
        return try {
            // Cập nhật thời gian
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            val updatedSettings = settings.copy(
                updatedAt = now,
                // Nếu đang tạo mới, cập nhật createdAt
                createdAt = if (localDataSource.getUserSettings(settings.id) == null) now else settings.createdAt
            )

            // Lưu vào local database
            val saveSuccess = localDataSource.saveUserSettings(updatedSettings)

            if (saveSuccess) {
                // Đồng bộ lên cloud nếu có remote data source và sync enabled
//                if (remoteDataSource != null && settings.syncEnabled) {
//                    try {
//                        remoteDataSource.saveUserSettings(updatedSettings)
//                    } catch (e: Exception) {
//                        // Log lỗi sync nhưng không ảnh hưởng đến kết quả trả về
//                        // vì đã lưu thành công vào local
//                        logError("Error syncing user settings to cloud", e)
//                    }
//                }
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to save user settings to local database"))
            }
        } catch (e: Exception) {
            println("Error in repository while saving user settings")
            Result.failure(e)
        }
    }

    override suspend fun togglePrivacyMode(enabled: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun toggleBiometricAuth(enabled: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun toggleTwoFactorAuth(enabled: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun toggleSync(enabled: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setDefaultSortOption(sortOption: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setTheme(theme: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun addSafeZone(
        name: String,
        latitude: Double,
        longitude: Double,
        radiusMeters: Double
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun removeSafeZone(zoneId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentAppVersion(): AppVersionEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun saveAppVersion(appVersion: AppVersionEntity): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun needsDatabaseMigration(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getOfflineQueue(): List<OfflineQueueEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun addToOfflineQueue(offlineQueue: OfflineQueueEntity): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromOfflineQueue(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateOfflineQueueItem(offlineQueue: OfflineQueueEntity): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun countOfflineQueueItems(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun incrementOfflineQueueAttempts(id: String): Boolean {
        TODO("Not yet implemented")
    }
}