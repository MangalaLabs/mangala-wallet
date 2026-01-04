package com.mangala.wallet.features.addressbook.data.model.setting

import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho phiên bản ứng dụng và schema database
 * Tương ứng với bảng 'app_version' trong database
 */
data class AppVersionEntity (
    val id: Int?, // ID tự động tăng
    val versionCode: Int,
    val versionName: String,
    val schemaVersion: Int,
    val lastUpdated: Instant,
    val migrationNotes: String?
) {
    /**
     * Kiểm tra xem phiên bản hiện tại có mới hơn không
     * @param otherVersion phiên bản khác
     * @return true nếu phiên bản hiện tại mới hơn
     */
    fun isNewerThan(otherVersion: AppVersionEntity): Boolean {
        return versionCode > otherVersion.versionCode
    }

    /**
     * Kiểm tra xem schema có cần migration không
     * @param currentSchema phiên bản schema hiện tại
     * @return true nếu cần migration
     */
    fun needsMigration(currentSchema: Int): Boolean {
        return schemaVersion > currentSchema
    }

    companion object {
        /**
         * Tạo một đối tượng AppVersionEntity mới
         */
        fun create(
            versionCode: Int,
            versionName: String,
            schemaVersion: Int,
            migrationNotes: String? = null
        ): AppVersionEntity {
            return AppVersionEntity(
                id = null, // ID sẽ được tự động tạo bởi database
                versionCode = versionCode,
                versionName = versionName,
                schemaVersion = schemaVersion,
                lastUpdated = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow())),
                migrationNotes = migrationNotes
            )
        }
    }
}