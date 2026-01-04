package com.mangala.wallet.features.addressbook.data.model.group

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.model.enum.PrivacyLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho nhóm contacts
 * Tương ứng với bảng 'GroupEntitys' trong database
 */
data class GroupEntity(
    val id: String, // UUID
    val name: String,
    val mainBlockchainId: String?, // Liên kết đến blockchain chính của nhóm
    val description: String?,
    val icon: String?, // Biểu tượng hoặc emoji đại diện
    val color: String?, // Mã màu của nhóm (Hex)
    val privacyLevel: PrivacyLevel,
    val securityLevel: SecurityLevel,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * Kiểm tra liệu nhóm có được xem là nhạy cảm không
     * @return true nếu nhóm có mức độ bảo mật cao hoặc tối đa
     */
    fun isSensitiveGroupEntity(): Boolean {
        return securityLevel == SecurityLevel.HIGH || securityLevel == SecurityLevel.MAXIMUM
    }

    /**
     * Kiểm tra liệu có yêu cầu xác thực để xem nhóm hay không
     * @return true nếu cần xác thực
     */
    fun requiresAuthentication(): Boolean {
        return securityLevel != SecurityLevel.NORMAL
    }

    /**
     * Kiểm tra nhóm có nằm trong điều kiện tìm kiếm không
     * @param query Chuỗi tìm kiếm
     * @return true nếu phù hợp với điều kiện tìm kiếm
     */
    fun matchesSearchQuery(query: String): Boolean {
        if (query.isBlank()) return true

        val lowerQuery = query.lowercase()
        return name.lowercase().contains(lowerQuery) ||
                (description?.lowercase()?.contains(lowerQuery) == true)
    }

    companion object {
        /**
         * Tạo một đối tượng GroupEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            name: String,
            mainBlockchainId: String? = null,
            description: String? = null,
            icon: String? = null,
            color: String? = null,
            privacyLevel: PrivacyLevel = PrivacyLevel.PUBLIC,
            securityLevel: SecurityLevel = SecurityLevel.NORMAL
        ): GroupEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return GroupEntity(
                id = id,
                name = name,
                mainBlockchainId = mainBlockchainId,
                description = description,
                icon = icon,
                color = color,
                privacyLevel = privacyLevel,
                securityLevel = securityLevel,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}