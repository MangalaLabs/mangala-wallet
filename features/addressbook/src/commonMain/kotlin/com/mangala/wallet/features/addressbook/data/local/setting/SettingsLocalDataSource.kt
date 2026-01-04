package com.mangala.wallet.features.addressbook.data.local.setting

import com.mangala.wallet.features.addressbook.data.model.setting.AppVersionEntity
import com.mangala.wallet.features.addressbook.data.model.setting.OfflineQueueEntity
import com.mangala.wallet.features.addressbook.data.model.setting.UserSettingEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface định nghĩa các phương thức truy cập dữ liệu liên quan đến Settings từ local database
 */
interface SettingsLocalDataSource {
    /**
     * Lấy user settings
     * @param id ID của user settings cần lấy (thường là "current" hoặc user ID)
     * @return UserSettingEntity hoặc null nếu không tìm thấy
     */
    suspend fun getUserSettings(id: String = "current"): UserSettingEntity?

    /**
     * Theo dõi thay đổi user settings
     * @param id ID của user settings cần theo dõi
     * @return Flow của UserSettingEntity
     */
    fun observeUserSettings(id: String = "current"): Flow<UserSettingEntity?>

    /**
     * Lưu user settings
     * @param settings UserSettingEntity cần lưu
     * @return true nếu lưu thành công
     */
    suspend fun saveUserSettings(settings: UserSettingEntity): Boolean

    /**
     * Bật/tắt chế độ riêng tư
     * @param enabled true để bật, false để tắt
     * @return true nếu cập nhật thành công
     */
    suspend fun togglePrivacyMode(enabled: Boolean): Boolean

    /**
     * Bật/tắt xác thực sinh trắc học
     * @param enabled true để bật, false để tắt
     * @return true nếu cập nhật thành công
     */
    suspend fun toggleBiometricAuth(enabled: Boolean): Boolean

    /**
     * Bật/tắt xác thực hai lớp
     * @param enabled true để bật, false để tắt
     * @return true nếu cập nhật thành công
     */
    suspend fun toggleTwoFactorAuth(enabled: Boolean): Boolean

    /**
     * Bật/tắt đồng bộ hóa
     * @param enabled true để bật, false để tắt
     * @return true nếu cập nhật thành công
     */
    suspend fun toggleSync(enabled: Boolean): Boolean

    /**
     * Đặt tùy chọn sắp xếp mặc định
     * @param sortOption Tùy chọn sắp xếp
     * @return true nếu cập nhật thành công
     */
    suspend fun setDefaultSortOption(sortOption: String): Boolean

    /**
     * Đặt chủ đề giao diện
     * @param theme Chủ đề (LIGHT, DARK, SYSTEM)
     * @return true nếu cập nhật thành công
     */
    suspend fun setTheme(theme: String): Boolean

    /**
     * Thêm một safe zone
     * @param name Tên của safe zone
     * @param latitude Vĩ độ
     * @param longitude Kinh độ
     * @param radiusMeters Bán kính theo mét
     * @return true nếu thêm thành công
     */
    suspend fun addSafeZone(name: String, latitude: Double, longitude: Double, radiusMeters: Double = 100.0): Boolean

    /**
     * Xóa một safe zone
     * @param zoneId ID của safe zone cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun removeSafeZone(zoneId: String): Boolean

    /**
     * Lấy phiên bản app hiện tại
     * @return AppVersionEntity hoặc null nếu không tìm thấy
     */
    suspend fun getCurrentAppVersion(): AppVersionEntity?

    /**
     * Lưu phiên bản app mới
     * @param appVersion AppVersionEntity cần lưu
     * @return true nếu lưu thành công
     */
    suspend fun saveAppVersion(appVersion: AppVersionEntity): Boolean

    /**
     * Kiểm tra schema có cần migration không
     * @return true nếu cần migration
     */
    suspend fun needsDatabaseMigration(): Boolean

    /**
     * Lấy danh sách offline queue
     * @return Danh sách các OfflineQueueEntity đang chờ xử lý
     */
    suspend fun getOfflineQueue(): List<OfflineQueueEntity>

    /**
     * Thêm một item vào offline queue
     * @param offlineQueue OfflineQueueEntity cần thêm
     * @return true nếu thêm thành công
     */
    suspend fun addToOfflineQueue(offlineQueue: OfflineQueueEntity): Boolean

    /**
     * Xóa một item khỏi offline queue
     * @param id ID của item cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun removeFromOfflineQueue(id: String): Boolean

    /**
     * Cập nhật một item trong offline queue
     * @param offlineQueue OfflineQueueEntity cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updateOfflineQueueItem(offlineQueue: OfflineQueueEntity): Boolean

    /**
     * Đếm số lượng items trong offline queue
     * @return Số lượng items
     */
    suspend fun countOfflineQueueItems(): Int

    /**
     * Tăng số lần thử của một item trong offline queue
     * @param id ID của item
     * @return true nếu cập nhật thành công
     */
    suspend fun incrementOfflineQueueAttempts(id: String): Boolean
}