package com.mangala.wallet.features.addressbook.data.local.setting

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.setting.AppVersionEntity
import com.mangala.wallet.features.addressbook.data.model.setting.OfflineQueueEntity
import com.mangala.wallet.features.addressbook.data.model.setting.UserSettingEntity
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
/**
 * Implementation của SettingsLocalDataSource sử dụng SQLDelight database
 */
class SettingsLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SettingsLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    /**
     * Chuyển đổi từ database UserSettings row sang UserSettingEntity
     */
    private fun mapToUserSettingEntity(
        id: String,
        privacy_mode_enabled: Boolean?,  // Thay đổi thành nullable
        default_privacy_display: String?,  // Thay đổi thành nullable
        biometric_auth_enabled: Boolean?,  // Thay đổi thành nullable
        two_factor_auth_enabled: Boolean?,  // Thay đổi thành nullable
        sync_enabled: Boolean?,  // Thay đổi thành nullable
        default_sort_option: String?,  // Thay đổi thành nullable
        theme: String?,  // Thay đổi thành nullable
        reminder_settings: String?,
        safe_zones: String?,
        created_at: Long,
        updated_at: Long
    ): UserSettingEntity {
        return UserSettingEntity(
            id = id,
            privacyModeEnabled = privacy_mode_enabled ?: false,  // Xử lý null
            defaultPrivacyDisplay = DisplayMode.fromString(default_privacy_display),
            biometricAuthEnabled = biometric_auth_enabled ?: false,  // Xử lý null
            twoFactorAuthEnabled = two_factor_auth_enabled ?: false,  // Xử lý null
            syncEnabled = sync_enabled ?: false,  // Xử lý null
            defaultSortOption = default_sort_option ?: "A_Z",  // Xử lý null với giá trị mặc định
            theme = theme ?: "SYSTEM",  // Xử lý null với giá trị mặc định
            reminderSettings = reminder_settings,
            safeZones = safe_zones,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    /**
     * Chuyển đổi từ database AppVersion row sang AppVersionEntity
     */
    private fun mapToAppVersionEntity(
        id: Long?,
        version_code: Long,
        version_name: String,
        schema_version: Long,
        last_updated: Long,
        migration_notes: String?
    ): AppVersionEntity {
        return AppVersionEntity(
            id = id?.toInt(),
            versionCode = version_code.toInt(),
            versionName = version_name,
            schemaVersion = schema_version.toInt(),
            lastUpdated = Instant.fromEpochMilliseconds(last_updated),
            migrationNotes = migration_notes
        )
    }

    /**
     * Chuyển đổi từ database OfflineQueue row sang OfflineQueueEntity
     */
    private fun mapToOfflineQueueEntity(
        id: String,
        action_type: String,
        entity_type: String,
        entity_id: String,
        data: String,  // Đảm bảo parameter này có tên là 'data'
        attempt_count: Long?,  // Thay đổi thành nullable
        created_at: Long,
        updated_at: Long
    ): OfflineQueueEntity {
        return OfflineQueueEntity(
            id = id,
            actionType = action_type,
            entityType = entity_type,
            entityId = entity_id,
            data = data,
            attemptCount = attempt_count?.toInt() ?: 0,  // Xử lý null
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    override suspend fun getUserSettings(id: String): UserSettingEntity? = withContext(ioDispatcher) {
        dbQuery.getUserSettings(id).executeAsOneOrNull()?.let { settings ->
            mapToUserSettingEntity(
                id = settings.id,
                privacy_mode_enabled = settings.privacy_mode_enabled,
                default_privacy_display = settings.default_privacy_display,
                biometric_auth_enabled = settings.biometric_auth_enabled,
                two_factor_auth_enabled = settings.two_factor_auth_enabled,
                sync_enabled = settings.sync_enabled,
                default_sort_option = settings.default_sort_option,
                theme = settings.theme,
                reminder_settings = settings.reminder_settings,
                safe_zones = settings.safe_zones,
                created_at = settings.created_at,
                updated_at = settings.updated_at
            )
        }
    }

    override fun observeUserSettings(id: String): Flow<UserSettingEntity?> {
        return dbQuery.getUserSettings(id)
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { settings ->
                settings?.let {
                    mapToUserSettingEntity(
                        id = it.id,
                        privacy_mode_enabled = it.privacy_mode_enabled,
                        default_privacy_display = it.default_privacy_display,
                        biometric_auth_enabled = it.biometric_auth_enabled,
                        two_factor_auth_enabled = it.two_factor_auth_enabled,
                        sync_enabled = it.sync_enabled,
                        default_sort_option = it.default_sort_option,
                        theme = it.theme,
                        reminder_settings = it.reminder_settings,
                        safe_zones = it.safe_zones,
                        created_at = it.created_at,
                        updated_at = it.updated_at
                    )
                }
            }
    }

    override suspend fun saveUserSettings(settings: UserSettingEntity): Boolean = withContext(ioDispatcher) {
        try {
            database.transaction {
                val existingSettings = dbQuery
                    .getUserSettings(settings.id)
                    .executeAsOneOrNull()

                if (existingSettings == null) {
                    // Thêm mới
                    dbQuery.insertUserSettings(
                        id = settings.id,
                        privacy_mode_enabled = settings.privacyModeEnabled,
                        default_privacy_display = settings.defaultPrivacyDisplay.name,
                        biometric_auth_enabled = settings.biometricAuthEnabled,
                        two_factor_auth_enabled = settings.twoFactorAuthEnabled,
                        sync_enabled = settings.syncEnabled,
                        default_sort_option = settings.defaultSortOption,
                        theme = settings.theme,
                        reminder_settings = settings.reminderSettings,
                        safe_zones = settings.safeZones,
                        created_at = settings.createdAt.toEpochMilliseconds(),
                        updated_at = settings.updatedAt.toEpochMilliseconds()
                    )
                } else {
                    // Cập nhật
                    dbQuery.updateUserSettings(
                        privacy_mode_enabled = settings.privacyModeEnabled,
                        default_privacy_display = settings.defaultPrivacyDisplay.name,
                        biometric_auth_enabled = settings.biometricAuthEnabled,
                        two_factor_auth_enabled = settings.twoFactorAuthEnabled,
                        sync_enabled = settings.syncEnabled,
                        default_sort_option = settings.defaultSortOption,
                        theme = settings.theme,
                        reminder_settings = settings.reminderSettings,
                        safe_zones = settings.safeZones,
                        updated_at = settings.updatedAt.toEpochMilliseconds(),
                        id = settings.id
                    )
                }
            }
            return@withContext true
        } catch (e: Exception) {
            // Log lỗi nếu có
            println("Error saving user settings")
            return@withContext false
        }
    }

    override suspend fun togglePrivacyMode(enabled: Boolean): Boolean = withContext(ioDispatcher) {
        // Lấy settings hiện tại
        val currentSettings = getUserSettings("current") ?:
        UserSettingEntity.createDefault("current")

        // Cập nhật trạng thái và lưu
        val updatedSettings = currentSettings.copy(
            privacyModeEnabled = enabled,
            updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        )

        return@withContext saveUserSettings(updatedSettings)
    }

    override suspend fun toggleBiometricAuth(enabled: Boolean): Boolean = withContext(ioDispatcher) {
        // Lấy settings hiện tại
        val currentSettings = getUserSettings("current") ?:
        UserSettingEntity.createDefault("current")

        // Cập nhật trạng thái và lưu
        val updatedSettings = currentSettings.copy(
            biometricAuthEnabled = enabled,
            updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        )

        return@withContext saveUserSettings(updatedSettings)
    }

    override suspend fun toggleTwoFactorAuth(enabled: Boolean): Boolean = withContext(ioDispatcher) {
        // Lấy settings hiện tại
        val currentSettings = getUserSettings("current") ?:
        UserSettingEntity.createDefault("current")

        // Cập nhật trạng thái và lưu
        val updatedSettings = currentSettings.copy(
            twoFactorAuthEnabled = enabled,
            updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        )

        return@withContext saveUserSettings(updatedSettings)
    }

    override suspend fun toggleSync(enabled: Boolean): Boolean = withContext(ioDispatcher) {
        // Lấy settings hiện tại
        val currentSettings = getUserSettings("current") ?:
        UserSettingEntity.createDefault("current")

        // Cập nhật trạng thái và lưu
        val updatedSettings = currentSettings.copy(
            syncEnabled = enabled,
            updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        )

        return@withContext saveUserSettings(updatedSettings)
    }

    override suspend fun setDefaultSortOption(sortOption: String): Boolean = withContext(ioDispatcher) {
        // Lấy settings hiện tại
        val currentSettings = getUserSettings("current") ?:
        UserSettingEntity.createDefault("current")

        // Cập nhật trạng thái và lưu
        val updatedSettings = currentSettings.copy(
            defaultSortOption = sortOption,
            updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        )

        return@withContext saveUserSettings(updatedSettings)
    }

    override suspend fun setTheme(theme: String): Boolean = withContext(ioDispatcher) {
        // Lấy settings hiện tại
        val currentSettings = getUserSettings("current") ?:
        UserSettingEntity.createDefault("current")

        // Cập nhật trạng thái và lưu
        val updatedSettings = currentSettings.copy(
            theme = theme,
            updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        )

        return@withContext saveUserSettings(updatedSettings)
    }

    override suspend fun addSafeZone(name: String, latitude: Double, longitude: Double, radiusMeters: Double): Boolean = withContext(ioDispatcher) {
        // Lấy settings hiện tại
        val currentSettings = getUserSettings("current") ?:
        UserSettingEntity.createDefault("current")

        // Tạo JSON mới cho safe zone
        val zoneId = uuid4().toString()

        // Đọc JSON hiện tại từ chuỗi nếu có
        val existingSafeZonesJson = currentSettings.safeZones
        val existingSafeZonesObj = if (existingSafeZonesJson.isNullOrBlank()) {
            buildJsonObject { }
        } else {
            try {
                Json.parseToJsonElement(existingSafeZonesJson).jsonObject
            } catch (e: Exception) {
                buildJsonObject { }
            }
        }

        // Xây dựng JSON mới kết hợp với dữ liệu hiện tại
        val safeZonesJson = buildJsonObject {
            // Copy các phần tử từ JSON hiện tại
            existingSafeZonesObj.entries.forEach { (key, value) ->
                put(key, value)
            }

            // Thêm zone mới hoặc cập nhật zones array
            val zonesObj = existingSafeZonesObj["zones"]?.jsonObject ?: buildJsonObject { }

            putJsonObject("zones") {
                // Copy các zone hiện tại
                zonesObj.entries.forEach { (key, value) ->
                    put(key, value)
                }

                // Thêm zone mới
                putJsonObject(zoneId) {
                    put("name", name)
                    put("latitude", latitude)
                    put("longitude", longitude)
                    put("radiusMeters", radiusMeters)
                }
            }
        }.toString()

        // Cập nhật settings và lưu
        val updatedSettings = currentSettings.copy(
            safeZones = safeZonesJson,
            updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        )

        return@withContext saveUserSettings(updatedSettings)
    }

    override suspend fun removeSafeZone(zoneId: String): Boolean = withContext(ioDispatcher) {
        // Lấy settings hiện tại
        val currentSettings = getUserSettings("current") ?: return@withContext false

        // Xử lý JSON
        val existingSafeZones = currentSettings.getSafeZonesJson() ?: return@withContext false

        // TODO: Implement JSON manipulation to remove the zone
        // This would require more complex JSON manipulation which depends on your JSON structure

        return@withContext true
    }

    override suspend fun getCurrentAppVersion(): AppVersionEntity? = withContext(ioDispatcher) {
        dbQuery.getCurrentAppVersion().executeAsOneOrNull()?.let { version ->
            mapToAppVersionEntity(
                id = version.id,
                version_code = version.version_code,
                version_name = version.version_name,
                schema_version = version.schema_version,
                last_updated = version.last_updated,
                migration_notes = version.migration_notes
            )
        }
    }

    override suspend fun saveAppVersion(appVersion: AppVersionEntity): Boolean = withContext(ioDispatcher) {
        // Giả định có phương thức saveAppVersion trong database
        // dbQuery.saveAppVersion(...)

        return@withContext true
    }

    override suspend fun needsDatabaseMigration(): Boolean = withContext(ioDispatcher) {
        val currentAppVersion = getCurrentAppVersion() ?: return@withContext false

        // Giả định logic kiểm tra schema version
        // Ví dụ: So sánh schemaVersion với một biến CURRENT_SCHEMA_VERSION

        return@withContext false
    }

    override suspend fun getOfflineQueue(): List<OfflineQueueEntity> = withContext(ioDispatcher) {
        dbQuery.getOfflineQueue()
            .executeAsList()
            .map { queue ->
                mapToOfflineQueueEntity(
                    id = queue.id,
                    action_type = queue.action_type,
                    entity_type = queue.entity_type,
                    entity_id = queue.entity_id,
                    data = queue.queue_data,  // Đảm bảo trường này trùng khớp với tên field trong database
                    attempt_count = queue.attempt_count,
                    created_at = queue.created_at,
                    updated_at = queue.updated_at
                )
            }
    }

    override suspend fun addToOfflineQueue(offlineQueue: OfflineQueueEntity): Boolean = withContext(ioDispatcher) {
        val id = offlineQueue.id.ifBlank { uuid4().toString() }

        // Giả định có phương thức addToOfflineQueue trong database
        // dbQuery.addToOfflineQueue(...)

        return@withContext true
    }

    override suspend fun removeFromOfflineQueue(id: String): Boolean = withContext(ioDispatcher) {
        // Giả định có phương thức removeFromOfflineQueue trong database
        // dbQuery.removeFromOfflineQueue(id)

        return@withContext true
    }

    override suspend fun updateOfflineQueueItem(offlineQueue: OfflineQueueEntity): Boolean = withContext(ioDispatcher) {
        // Giả định có phương thức updateOfflineQueueItem trong database
        // dbQuery.updateOfflineQueueItem(...)

        return@withContext true
    }

    override suspend fun countOfflineQueueItems(): Int = withContext(ioDispatcher) {
        // Giả định có phương thức countOfflineQueueItems trong database
        // return@withContext dbQuery.countOfflineQueueItems().executeAsOne().toInt()

        return@withContext getOfflineQueue().size
    }

    override suspend fun incrementOfflineQueueAttempts(id: String): Boolean = withContext(ioDispatcher) {
        // Lấy item hiện tại
        val current = getOfflineQueue().find { it.id == id } ?: return@withContext false

        // Tăng số lần thử
        val updated = current.copy(
            attemptCount = current.attemptCount + 1,
            updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        )

        // Cập nhật
        return@withContext updateOfflineQueueItem(updated)
    }
}