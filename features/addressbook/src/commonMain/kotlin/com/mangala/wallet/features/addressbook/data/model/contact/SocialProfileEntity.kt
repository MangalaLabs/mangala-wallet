package com.mangala.wallet.features.addressbook.data.model.contact

import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho các hồ sơ mạng xã hội của một contact
 * Tương ứng với bảng 'social_profiles' trong database
 */
data class SocialProfileEntity(
    val id: String, // UUID
    val contactId: String,
    val platform: String, // Ví dụ: "Twitter", "Facebook", "Instagram", "Telegram"
    val username: String,
    val url: String?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * Tạo URL đầy đủ dựa trên platform nếu URL không được cung cấp
     * @return URL đầy đủ hoặc null nếu không thể tạo
     */
    fun getFullUrl(): String? {
        if (!url.isNullOrBlank()) return url

        // Tạo URL dựa trên platform và username
        return when (platform.lowercase()) {
            "twitter", "x" -> "https://twitter.com/$username"
            "facebook" -> "https://facebook.com/$username"
            "instagram" -> "https://instagram.com/$username"
            "linkedin" -> "https://linkedin.com/in/$username"
            "github" -> "https://github.com/$username"
            "telegram" -> "https://t.me/$username"
            "youtube" -> "https://youtube.com/@$username"
            "tiktok" -> "https://tiktok.com/@$username"
            "reddit" -> "https://reddit.com/user/$username"
            "medium" -> "https://medium.com/@$username"
            else -> null
        }
    }

    /**
     * Lấy tên hiển thị của platform
     * @return Tên hiển thị chuẩn hóa
     */
    fun getDisplayPlatform(): String {
        return when (platform.lowercase()) {
            "twitter" -> "Twitter"
            "x" -> "X"
            "facebook" -> "Facebook"
            "instagram" -> "Instagram"
            "linkedin" -> "LinkedIn"
            "github" -> "GitHub"
            "telegram" -> "Telegram"
            "youtube" -> "YouTube"
            "tiktok" -> "TikTok"
            "reddit" -> "Reddit"
            "medium" -> "Medium"
            else -> platform
        }
    }

    companion object {
        // Các platform phổ biến
        const val PLATFORM_TWITTER = "Twitter"
        const val PLATFORM_X = "X"
        const val PLATFORM_FACEBOOK = "Facebook"
        const val PLATFORM_INSTAGRAM = "Instagram"
        const val PLATFORM_LINKEDIN = "LinkedIn"
        const val PLATFORM_GITHUB = "GitHub"
        const val PLATFORM_TELEGRAM = "Telegram"
        const val PLATFORM_YOUTUBE = "YouTube"
        const val PLATFORM_TIKTOK = "TikTok"
        const val PLATFORM_REDDIT = "Reddit"
        const val PLATFORM_MEDIUM = "Medium"

        /**
         * Lấy danh sách các platform phổ biến
         * @return Danh sách các platform
         */
        fun getCommonPlatforms(): List<String> {
            return listOf(
                PLATFORM_TWITTER,
                PLATFORM_X,
                PLATFORM_FACEBOOK,
                PLATFORM_INSTAGRAM,
                PLATFORM_LINKEDIN,
                PLATFORM_GITHUB,
                PLATFORM_TELEGRAM,
                PLATFORM_YOUTUBE,
                PLATFORM_TIKTOK,
                PLATFORM_REDDIT,
                PLATFORM_MEDIUM
            )
        }

        /**
         * Tạo một đối tượng SocialProfileEntity mới
         */
        fun create(
            id: String, // UUID được tạo từ repository
            contactId: String,
            platform: String,
            username: String,
            url: String? = null
        ): SocialProfileEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return SocialProfileEntity(
                id = id,
                contactId = contactId,
                platform = platform,
                username = username,
                url = url,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}