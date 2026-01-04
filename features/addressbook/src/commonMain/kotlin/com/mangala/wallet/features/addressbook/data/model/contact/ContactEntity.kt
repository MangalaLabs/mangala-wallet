package com.mangala.wallet.features.addressbook.data.model.contact

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SyncStatus
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Entity cho một liên hệ (Contact) trong Address Book
 * Tương ứng với bảng 'contacts' trong database
 */
data class ContactEntity(
    val id: String, // UUID
    val name: String,
    val notes: String?,
    val avatar: String? = null,
    val solarBirthday: LocalDate?,
    val lunarBirthday: LocalDate?,
    val isSensitive: Boolean?,
    val securityLevel: SecurityLevel,
    val privacyDisplayMode: DisplayMode,
    val authRequirement: AuthRequirement,
    val createdAt: Instant?,
    val updatedAt: Instant,
    val lastViewedAt: Instant?,
    val syncStatus: SyncStatus,
    val encryptedData: String?
) {
    /**
     * Trả về tên hiển thị của contact tùy thuộc vào chế độ riêng tư
     * @param privacyModeEnabled Có đang bật chế độ riêng tư hay không
     * @return Tên hiển thị phù hợp
     */
    fun getDisplayName(privacyModeEnabled: Boolean): String {
        if (!privacyModeEnabled) return name

        return when (privacyDisplayMode) {
            DisplayMode.FULL -> name
            DisplayMode.HIDDEN -> "********"
            DisplayMode.SECRET -> "**SECRET**"
        }
    }

    /**
     * Kiểm tra xem contact có yêu cầu xác thực để xem hay không
     * @return true nếu cần xác thực
     */
    fun requiresAuthentication(): Boolean {
        return authRequirement != AuthRequirement.NONE
    }

    /**
     * Kiểm tra liên hệ có nằm trong điều kiện tìm kiếm không
     * @param query Chuỗi tìm kiếm
     * @return true nếu phù hợp với điều kiện tìm kiếm
     */
    fun matchesSearchQuery(query: String): Boolean {
        if (query.isBlank()) return true

        val lowerQuery = query.lowercase()
        return name.lowercase().contains(lowerQuery) ||
                (notes?.lowercase()?.contains(lowerQuery) == true)
    }

    companion object {
        /**
         * Tạo một contact mới với ID và timestamp được tạo tự động
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            name: String,
            notes: String? = null,
            icon: String? = null,
            solarBirthday: LocalDate? = null,
            lunarBirthday: LocalDate? = null,
            isSensitive: Boolean = false,
            securityLevel: SecurityLevel = SecurityLevel.NORMAL,
            privacyDisplayMode: DisplayMode = DisplayMode.FULL,
            authRequirement: AuthRequirement = AuthRequirement.NONE
        ): ContactEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return ContactEntity(
                id = id,
                name = name,
                notes = notes,
                avatar = icon,
                solarBirthday = solarBirthday,
                lunarBirthday = lunarBirthday,
                isSensitive = isSensitive,
                securityLevel = securityLevel,
                privacyDisplayMode = privacyDisplayMode,
                authRequirement = authRequirement,
                createdAt = now,
                updatedAt = now,
                lastViewedAt = null,
                syncStatus = SyncStatus.LOCAL_ONLY,
                encryptedData = null
            )
        }
    }
}