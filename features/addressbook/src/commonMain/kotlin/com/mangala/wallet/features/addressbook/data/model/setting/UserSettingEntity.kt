package com.mangala.wallet.features.addressbook.data.model.setting

import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import com.mangala.wallet.utils.toRadians
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

/**
 * Entity cho cài đặt người dùng
 * Tương ứng với bảng 'user_settings' trong database
 */
data class UserSettingEntity(
    val id: String, // UUID
    val privacyModeEnabled: Boolean,
    val defaultPrivacyDisplay: DisplayMode,
    val biometricAuthEnabled: Boolean,
    val twoFactorAuthEnabled: Boolean,
    val syncEnabled: Boolean,
    val defaultSortOption: String, // A_Z, Z_A, NEWEST, OLDEST, BLOCKCHAIN
    val theme: String, // LIGHT, DARK, SYSTEM
    val reminderSettings: String?, // JSON string
    val safeZones: String?, // JSON string
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * Parse reminderSettings từ JSON sang đối tượng
     * @return JsonElement hoặc null nếu không thể parse
     */
    fun getReminderSettingsJson(): JsonElement? {
        if (reminderSettings.isNullOrBlank()) return null

        return try {
            Json.parseToJsonElement(reminderSettings)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parse safeZones từ JSON sang đối tượng
     * @return JsonElement hoặc null nếu không thể parse
     */
    fun getSafeZonesJson(): JsonElement? {
        if (safeZones.isNullOrBlank()) return null

        return try {
            Json.parseToJsonElement(safeZones)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Kiểm tra một vị trí có nằm trong safe zone không
     * @param latitude Vĩ độ
     * @param longitude Kinh độ
     * @return true nếu vị trí nằm trong safe zone
     */
    fun isLocationInSafeZone(latitude: Double, longitude: Double): Boolean {
        val zonesJson = getSafeZonesJson() ?: return false

        try {
            val zones = zonesJson.jsonObject["zones"]?.jsonObject ?: return false

            for ((_, zoneElement) in zones) {
                val zone = zoneElement.jsonObject
                val zoneLat = zone["latitude"]?.toString()?.toDoubleOrNull() ?: continue
                val zoneLng = zone["longitude"]?.toString()?.toDoubleOrNull() ?: continue
                val radiusMeters = zone["radiusMeters"]?.toString()?.toDoubleOrNull() ?: 100.0

                // Tính khoảng cách giữa hai điểm (đơn giản hóa)
                val distance = calculateDistance(latitude, longitude, zoneLat, zoneLng)

                if (distance <= radiusMeters) {
                    return true
                }
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }

    /**
     * Tính khoảng cách giữa hai điểm trên bản đồ
     * @param lat1 Vĩ độ điểm 1
     * @param lng1 Kinh độ điểm 1
     * @param lat2 Vĩ độ điểm 2
     * @param lng2 Kinh độ điểm 2
     * @return Khoảng cách theo mét
     */
    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371000.0 // Bán kính trái đất tính theo mét

        val dLat = (lat2 - lat1).toRadians()
        val dLng = (lng2 - lng1).toRadians()

        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(lat1.toRadians()) * kotlin.math.cos(lat2.toRadians()) *
                kotlin.math.sin(dLng / 2) * kotlin.math.sin(dLng / 2)

        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

        return earthRadius * c
    }

    companion object {
        // Các tùy chọn sắp xếp
        const val SORT_A_Z = "A_Z"
        const val SORT_Z_A = "Z_A"
        const val SORT_NEWEST = "NEWEST"
        const val SORT_OLDEST = "OLDEST"
        const val SORT_BLOCKCHAIN = "BLOCKCHAIN"

        // Các chủ đề
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"
        const val THEME_SYSTEM = "SYSTEM"

        /**
         * Lấy danh sách các tùy chọn sắp xếp
         * @return Danh sách các tùy chọn sắp xếp
         */
        fun getSortOptions(): List<String> {
            return listOf(SORT_A_Z, SORT_Z_A, SORT_NEWEST, SORT_OLDEST, SORT_BLOCKCHAIN)
        }

        /**
         * Lấy danh sách các chủ đề
         * @return Danh sách các chủ đề
         */
        fun getThemes(): List<String> {
            return listOf(THEME_LIGHT, THEME_DARK, THEME_SYSTEM)
        }

        /**
         * Tạo một đối tượng UserSettingEntity mặc định
         */
        fun createDefault(id: String): UserSettingEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return UserSettingEntity(
                id = id,
                privacyModeEnabled = false,
                defaultPrivacyDisplay = DisplayMode.FULL,
                biometricAuthEnabled = false,
                twoFactorAuthEnabled = false,
                syncEnabled = false,
                defaultSortOption = SORT_A_Z,
                theme = THEME_SYSTEM,
                reminderSettings = null,
                safeZones = null,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}